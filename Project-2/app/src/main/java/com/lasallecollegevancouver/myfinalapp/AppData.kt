package com.lasallecollegevancouver.myfinalapp

// --- DATA MODELS ---

data class SteamResponse(val response: PlayerContainer)
data class PlayerContainer(val players: List<Player>)
data class Player(
    val steamid: String?,
    val personaname: String?,
    val avatarfull: String?,
    val profileurl: String?,
    val loccountrycode: String?
)

data class Category(
    val title: String,
    val games: List<Game>
)

data class OwnedGamesResponse(val response: OwnedGamesContainer)
data class OwnedGamesContainer(
    val game_count: Int?,
    val games: List<Game>?
)

data class Game(
    val appid: Int? = null,
    val name: String? = null,
    val playtime_forever: Int? = null,
    val img_icon_url: String? = null,
    val imageRes: Int = 0
)

data class RecentlyPlayedResponse(val response: RecentlyPlayedContainer)
data class RecentlyPlayedContainer(
    val total_count: Int?,
    val games: List<Game>?
)

data class VanityURLResponse(val response: VanityURLContainer)
data class VanityURLContainer(
    val steamid: String?,
    val success: Int
)

// A unified data class for the UI
data class FullUserData(
    val player: Player?,
    val ownedGames: OwnedGamesContainer?,
    val recentlyPlayed: List<Game>?
)
