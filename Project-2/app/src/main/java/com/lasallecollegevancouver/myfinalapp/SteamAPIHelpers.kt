package com.lasallecollegevancouver.myfinalapp

import android.os.Handler
import android.os.Looper
import android.util.Log
import org.jsoup.Jsoup
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

// --- API SERVICE ---

object RetrofitClient {
    private const val BASE_URL = "https://api.steampowered.com/"
    private const val STORE_URL = "https://store.steampowered.com/"

    val instance: SteamApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SteamApiService::class.java)
    }

    val storeInstance: SteamStoreApiService by lazy {
        Retrofit.Builder()
            .baseUrl(STORE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SteamStoreApiService::class.java)
    }
}

interface SteamApiService {
    @GET("ISteamUser/GetPlayerSummaries/v2/")
    fun getPlayerSummary(
        @Query("key") key: String,
        @Query("steamids") steamId: String
    ): Call<SteamResponse>

    @GET("IPlayerService/GetOwnedGames/v1/")
    fun getOwnedGames(
        @Query("key") key: String,
        @Query("steamid") steamId: String,
        @Query("include_appinfo") includeAppInfo: Int = 1,
        @Query("include_played_free_games") includePlayedFreeGames: Int = 1
    ): Call<OwnedGamesResponse>

    @GET("IPlayerService/GetRecentlyPlayedGames/v1/")
    fun getRecentlyPlayedGames(
        @Query("key") key: String,
        @Query("steamid") steamId: String,
        @Query("count") count: Int = 10
    ): Call<RecentlyPlayedResponse>

    @GET("ISteamUser/ResolveVanityURL/v1/")
    fun resolveVanityURL(
        @Query("key") key: String,
        @Query("vanityurl") vanityUrl: String
    ): Call<VanityURLResponse>
}

interface SteamStoreApiService {
    @GET("api/appdetails")
    fun getAppDetails(
        @Query("appids") appIds: String
    ): Call<AppDetailsResponse>
}

// --- REPOSITORY ---

class SteamRepository(private val apiKey: String) {

    private val tagBlacklist = arrayOf(
        "In-App Purchases", "Multi-player", "Free to Play",
        "Sexual Content", "Nudity", "+", "Indie", "Early Access",
        "Singleplayer", "Multiplayer", "Co-op", "Online Co-Op",
        "Steam Cloud", "Full controller support", "Tracked Controller Support",
        "PvP", "PvE", "Competitive", "Steam Achievements", "Steam Trading Cards",
        "Steam Workshop", "Steam Leaderboards", "Remote Play Together",
        "Remote Play on Phone", "Remote Play on Tablet", "Remote Play on TV",
        "VR Only", "VR Supported", "VR", "Partial Controller Support",
        "Great Soundtrack", "Soundtrack", "Violent", "Gore", "Family Sharing",
        "Software", "Software Training", "Education", "Utilities", "Design & Illustration",
        "Animation & Modeling", "Game Development", "Hentai", "Capitalism", "3D", "2D",
        "Cinematic", "Lore-Rich", "Casual", "Atmospheric", "Story Rich", "Single-player",
        "Action", "Adventure", "Action-Adventure", "FPS", "First-Person", "Third Person",
        "Hero Shooter", "Team-Based", "Difficult", "Superhero", "Massively Multiplayer",
        "MMO", "MMORPG", "Side Scroller", "Top-Down", "Third-Person Shooter"
    )

    /**
     * Searches for games on the Steam Store by a specific genre or tag.
     */
    fun searchGamesByGenre(genre: String, isNiche: Boolean = false, onResult: (List<Game>) -> Unit) {
        Thread {
            try {
                // Removing filter=topsellers to restore Relevance. 
                // Steam's relevance engine is better at finding "True" genre hits.
                val url = "https://store.steampowered.com/search/?term=${genre.replace(" ", "+")}&category1=998"
                
                val doc = Jsoup.connect(url).get()
                val searchResults = doc.select(".search_result_row")
                
                val games = searchResults.mapNotNull { element ->
                    val appId = element.attr("data-ds-appid").split(",").firstOrNull()?.toIntOrNull()
                    val name = element.select(".title").text()
                    
                    val reviewData = element.select(".search_review_summary")
                    val reviewTooltip = reviewData.attr("data-tooltip-html")
                    
                    val scoreMatch = Regex("(\\d+)%").find(reviewTooltip)
                    val countMatch = Regex("of the ([\\d,]+) user reviews").find(reviewTooltip)
                    
                    val score = scoreMatch?.groupValues?.get(1)?.toIntOrNull() ?: 0
                    val count = countMatch?.groupValues?.get(1)?.replace(",", "")?.toIntOrNull() ?: 0
                    
                    val isHighQuality = score >= 80 && (reviewTooltip.contains("Positive") || 
                                       reviewTooltip.contains("Very Positive") || 
                                       reviewTooltip.contains("Overwhelmingly Positive"))
                    
                    if (appId != null && name.isNotEmpty() && isHighQuality) {
                        Game(
                            appid = appId, 
                            name = name, 
                            reviewScore = score, 
                            reviewCount = count
                        )
                    } else null
                }
                
                val mainHandler = Handler(Looper.getMainLooper())
                mainHandler.post { onResult(games) }
            } catch (e: Exception) {
                Log.e("SteamDebug", "Error searching games for genre $genre", e)
                val mainHandler = Handler(Looper.getMainLooper())
                mainHandler.post { onResult(emptyList()) }
            }
        }.start()
    }

    /**
     * Orchestrates all three API calls and returns the combined data.
     * It first attempts to resolve the input if it's a vanity URL.
     */
    fun loadFullUserData(input: String, onResult: (FullUserData) -> Unit) {
        // If it looks like a 17-digit SteamID64, use it directly
        if (input.length == 17 && input.all { it.isDigit() }) {
            fetchData(input, onResult)
        } else {
            // Otherwise, try to resolve it as a vanity URL
            RetrofitClient.instance.resolveVanityURL(apiKey, input).enqueue(object : Callback<VanityURLResponse> {
                override fun onResponse(call: Call<VanityURLResponse>, response: Response<VanityURLResponse>) {
                    val resolvedId = response.body()?.response?.steamid
                    if (resolvedId != null) {
                        fetchData(resolvedId, onResult)
                    } else {
                        onResult(FullUserData(null, null, null))
                    }
                }
                override fun onFailure(call: Call<VanityURLResponse>, t: Throwable) {
                    onResult(FullUserData(null, null, null))
                }
            })
        }
    }

    private fun fetchData(steamId: String, onResult: (FullUserData) -> Unit) {
        var player: Player? = null
        var owned: OwnedGamesContainer? = null
        var recent: List<Game>? = null
        
        var completedCalls = 0
        fun checkCompletion() {
            completedCalls++
            if (completedCalls == 3) {
                // After fetching basic user data, fetch genres for top games
                fetchGenresForTopGames(owned?.games, player, owned, recent, onResult)
            }
        }

        // 1. Fetch Summary
        RetrofitClient.instance.getPlayerSummary(apiKey, steamId).enqueue(object : Callback<SteamResponse> {
            override fun onResponse(call: Call<SteamResponse>, response: Response<SteamResponse>) {
                player = response.body()?.response?.players?.firstOrNull()
                checkCompletion()
            }
            override fun onFailure(call: Call<SteamResponse>, t: Throwable) { checkCompletion() }
        })

        // 2. Fetch Owned Games
        RetrofitClient.instance.getOwnedGames(apiKey, steamId).enqueue(object : Callback<OwnedGamesResponse> {
            override fun onResponse(call: Call<OwnedGamesResponse>, response: Response<OwnedGamesResponse>) {
                owned = response.body()?.response
                checkCompletion()
            }
            override fun onFailure(call: Call<OwnedGamesResponse>, t: Throwable) { checkCompletion() }
        })

        // 3. Fetch Recently Played
        RetrofitClient.instance.getRecentlyPlayedGames(apiKey, steamId).enqueue(object : Callback<RecentlyPlayedResponse> {
            override fun onResponse(call: Call<RecentlyPlayedResponse>, response: Response<RecentlyPlayedResponse>) {
                recent = response.body()?.response?.games
                checkCompletion()
            }
            override fun onFailure(call: Call<RecentlyPlayedResponse>, t: Throwable) { checkCompletion() }
        })
    }

    private fun fetchGenresForTopGames(
        games: List<Game>?,
        player: Player?,
        owned: OwnedGamesContainer?,
        recent: List<Game>?,
        onResult: (FullUserData) -> Unit
    ) {
        if (games.isNullOrEmpty()) {
            onResult(FullUserData(player, owned, recent))
            return
        }

        val topGames = games.sortedByDescending { it.playtime_forever ?: 0 }.take(5)
        val genreCounts = java.util.concurrent.ConcurrentHashMap<String, Int>()
        var completed = 0
        val mainHandler = Handler(Looper.getMainLooper())

        fun checkAll() {
            synchronized(this) {
                completed++
                if (completed == topGames.size) {
                    val topGenres = genreCounts.entries
                        .sortedByDescending { it.value }
                        .take(3)
                        .map { it.key }
                    mainHandler.post {
                        onResult(FullUserData(player, owned, recent, topGenres))
                    }
                }
            }
        }

        for (game in topGames) {
            val appId = game.appid ?: continue
            RetrofitClient.storeInstance.getAppDetails(appId.toString()).enqueue(object : Callback<AppDetailsResponse> {
                override fun onResponse(call: Call<AppDetailsResponse>, response: Response<AppDetailsResponse>) {
                    Thread {
                        try {
                            // 1. Process Official Genres
                            val details = response.body()?.get(appId.toString())
                            if (details?.success == true) {
                                details.data?.genres?.mapNotNull { it.description }?.forEach { genre ->
                                    if (tagBlacklist.none { it.equals(genre, ignoreCase = true) }) {
                                        genreCounts[genre] = (genreCounts[genre] ?: 0) + 1
                                    }
                                }
                            }

                            // 2. Scrape Store Tags with Age-Gate Bypass
                            val url = "https://store.steampowered.com/app/$appId"
                            val doc = Jsoup.connect(url)
                                .cookie("birthtime", "283993201") // Bypass age verification
                                .get()
                            val tags = doc.select(".app_tag").map { it.text().trim() }
                            
                            Log.d("SteamDebug", "Processing tags for ${game.name ?: appId}: $tags")
                            
                            tags.forEach { tag ->
                                if (tag.isNotEmpty() && tagBlacklist.none { it.equals(tag, ignoreCase = true) }) {
                                    // Tags contribute to the same frequency map as genres
                                    genreCounts[tag] = (genreCounts[tag] ?: 0) + 1
                                }
                            }
                        } catch (e: Exception) {
                            Log.e("SteamDebug", "Error processing tags for $appId", e)
                        } finally {
                            checkAll()
                        }
                    }.start()
                }

                override fun onFailure(call: Call<AppDetailsResponse>, t: Throwable) {
                    checkAll()
                }
            })
        }
    }
}
