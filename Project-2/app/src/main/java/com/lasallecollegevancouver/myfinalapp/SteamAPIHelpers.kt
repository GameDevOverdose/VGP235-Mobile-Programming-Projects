package com.lasallecollegevancouver.myfinalapp

import android.util.Log
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

        // Take top 5 most played games to analyze genres (Store API has rate limits)
        val topGames = games.sortedByDescending { it.playtime_forever ?: 0 }.take(5)
        val genreCounts = mutableMapOf<String, Int>()
        var completed = 0

        for (game in topGames) {
            val appId = game.appid ?: continue
            RetrofitClient.storeInstance.getAppDetails(appId.toString()).enqueue(object : Callback<AppDetailsResponse> {
                override fun onResponse(call: Call<AppDetailsResponse>, response: Response<AppDetailsResponse>) {
                    val details = response.body()?.get(appId.toString())
                    if (details?.success == true) {
                        val genres = details.data?.genres?.mapNotNull { it.description }
                            ?.filter { it != "In-App Purchases" && it != "Multi-player" && it != "Free to Play" } ?: emptyList()
                        Log.d("SteamDebug", "Genres for ${game.name ?: appId}: $genres")
                        
                        genres.forEach { desc ->
                            genreCounts[desc] = genreCounts.getOrDefault(desc, 0) + 1
                        }
                    } else {
                        Log.d("SteamDebug", "Failed to fetch details for ${game.name ?: appId}")
                    }
                    checkAll()
                }

                override fun onFailure(call: Call<AppDetailsResponse>, t: Throwable) {
                    checkAll()
                }

                private fun checkAll() {
                    completed++
                    if (completed == topGames.size) {
                        val topGenres = genreCounts.entries
                            .sortedByDescending { it.value }
                            .take(3)
                            .map { it.key }
                        onResult(FullUserData(player, owned, recent, topGenres))
                    }
                }
            })
        }
    }
}
