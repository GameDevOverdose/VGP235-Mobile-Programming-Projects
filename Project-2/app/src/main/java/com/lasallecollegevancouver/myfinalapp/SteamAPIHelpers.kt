package com.lasallecollegevancouver.myfinalapp

import android.os.Handler
import android.os.Looper
import android.util.Log
import org.json.JSONObject
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
     * Checks if a game has a valid library image on Steam's servers.
     */
    fun hasLibraryImage(appId: Int?): Boolean {
        if (appId == null) return false
        val urls = mutableListOf<String>()
        if (HomeActivity.AlgoConfig.useLibrary2xFallback) urls.add("https://cdn.akamai.steamstatic.com/steam/apps/$appId/library_600x900_2x.jpg")
        if (HomeActivity.AlgoConfig.useLibrary1xFallback) urls.add("https://cdn.akamai.steamstatic.com/steam/apps/$appId/library_600x900.jpg")
        if (HomeActivity.AlgoConfig.useCapsuleFallback) urls.add("https://cdn.akamai.steamstatic.com/steam/apps/$appId/capsule_231x350.jpg")
        
        if (urls.isEmpty()) return true // If no specific fallbacks are checked, assume okay or bypass

        return try {
            for (url in urls) {
                val response = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
                    .header("Referer", "https://store.steampowered.com/")
                    .ignoreContentType(true)
                    .ignoreHttpErrors(true)
                    .method(org.jsoup.Connection.Method.HEAD)
                    .timeout(3000)
                    .execute()
                if (response.statusCode() == 200) return true
            }
            false
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Searches for games on the Steam Store by a specific genre or tag.
     * @param sortBy can be "relevance" (default) or "Reviews_DESC" for popularity.
     */
    fun searchGamesByGenre(
        genre: String, 
        isNiche: Boolean = false, 
        sortBy: String = "relevance",
        onResult: (List<Game>) -> Unit
    ) {
        Thread {
            try {
                val searchTerm = if (isNiche) "$genre+masterpiece" else genre.replace(" ", "+")
                
                // STRATEGY C: Use the AJAX JSON endpoint if enabled
                val url = if (HomeActivity.AlgoConfig.useJsonEndpoint) {
                    "https://store.steampowered.com/search/results/?term=$searchTerm&category1=998&sort_by=$sortBy&json=1"
                } else {
                    "https://store.steampowered.com/search/?term=$searchTerm&category1=998&sort_by=$sortBy"
                }
                
                val connection = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
                    .header("Referer", "https://store.steampowered.com/")
                    .timeout(5000)

                val doc = if (HomeActivity.AlgoConfig.useJsonEndpoint) {
                    // Extract HTML from the JSON response
                    val response = connection.ignoreContentType(true).execute().body()
                    val html = JSONObject(response).optString("results_html")
                    Jsoup.parseBodyFragment(html)
                } else {
                    connection.get()
                }

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
                    
                    Log.d("SteamDebug", "Parsed game: $name (ID: $appId), Reviews: $count, Score: $score")

                    // Basic sanity check to avoid completely broken data
                    if (appId != null && name.isNotEmpty()) {
                        val game = Game(
                            appid = appId, 
                            name = name,
                            reviewCount = count,
                            reviewScore = score
                        )
                        // Scrape the image from the search results if available
                        val imgElement = element.select(".search_capsule img")
                        game.fallbackImageUrl = imgElement.attr("src")
                        game
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
                        .take(10)
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
                                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
                                .header("Referer", "https://store.steampowered.com/")
                                .cookie("birthtime", "283993201") // Bypass age verification
                                .get()
                            
                            // Extract fallback image URL (og:image is the standard)
                            game.fallbackImageUrl = doc.select("meta[property=og:image]").attr("content")
                            
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

    /**
     * Fetches genres and tags for a specific list of games.
     */
    fun fetchGenresForSelectedGames(games: List<Game>, onResult: (Map<Int, List<String>>) -> Unit) {
        if (games.isEmpty()) {
            onResult(emptyMap())
            return
        }

        val results = java.util.concurrent.ConcurrentHashMap<Int, List<String>>()
        var completed = 0
        val mainHandler = Handler(Looper.getMainLooper())

        for (game in games) {
            val appId = game.appid ?: continue
            RetrofitClient.storeInstance.getAppDetails(appId.toString()).enqueue(object : Callback<AppDetailsResponse> {
                override fun onResponse(call: Call<AppDetailsResponse>, response: Response<AppDetailsResponse>) {
                    Thread {
                        try {
                            val gameTags = mutableListOf<String>()
                            val details = response.body()?.get(appId.toString())
                            if (details?.success == true) {
                                details.data?.genres?.mapNotNull { it.description }?.forEach { genre ->
                                    if (tagBlacklist.none { it.equals(genre, ignoreCase = true) }) {
                                        gameTags.add(genre)
                                    }
                                }
                            }

                            val url = "https://store.steampowered.com/app/$appId"
                            val doc = Jsoup.connect(url)
                                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
                                .header("Referer", "https://store.steampowered.com/")
                                .cookie("birthtime", "283993201")
                                .get()
                            
                            // Extract fallback image URL
                            game.fallbackImageUrl = doc.select("meta[property=og:image]").attr("content")

                            val tags = doc.select(".app_tag").map { it.text().trim() }
                            tags.forEach { tag ->
                                if (tag.isNotEmpty() && tagBlacklist.none { it.equals(tag, ignoreCase = true) }) {
                                    gameTags.add(tag)
                                }
                            }
                            results[appId] = gameTags.distinct()
                        } catch (e: Exception) {
                            Log.e("SteamDebug", "Error processing tags for $appId", e)
                        } finally {
                            synchronized(this@SteamRepository) {
                                completed++
                                if (completed == games.size) {
                                    mainHandler.post { onResult(results) }
                                }
                            }
                        }
                    }.start()
                }

                override fun onFailure(call: Call<AppDetailsResponse>, t: Throwable) {
                    synchronized(this@SteamRepository) {
                        completed++
                        if (completed == games.size) {
                            mainHandler.post { onResult(results) }
                        }
                    }
                }
            })
        }
    }
}
