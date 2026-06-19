package com.lasallecollegevancouver.myfinalapp

object AppConfig {
    const val STEAM_API_KEY = "8D4656FFBAD8F36CF251F84C01D40848"
}

object AlgoConfig {
    const val minReviewCount = 1000
    const val minReviewScore = 70
    const val verifyImagesEnabled = true
    const val useLibrary2xFallback = false
    const val useLibrary1xFallback = true
    const val useCapsuleFallback = false
    const val useHeaderFallback = true
    const val useScrapedFallback = false
    const val strategiesPerGenre = 3
    const val hiddenGemScoreThreshold = 85
    const val blockbusterReviewThreshold = 5000
    const val maxGenresNoSelection = 3
    const val maxGenresWithSelection = 3

    // Strategy B & C Flags
    const val useSequentialRequests = true
    const val requestJitterMs = 200L
    const val useJsonEndpoint = false

    // Strategy A Flag (Consolidate 9 requests -> 3)
    const val useConsolidatedStrategy = true
    const val showRealTimeDna = true
}

object UIConfig {
    const val useGradient = true
    const val useShadows = true
}

object DataConfig {
    val adjacentTags = mapOf(
        "RPG" to listOf("Action RPG", "JRPG", "Open World RPG", "CRPG"),
        "Action" to listOf("Adventure", "Hack and Slash", "Shooter", "Platformer"),
        "Shooter" to listOf("FPS", "Third-Person Shooter", "Hero Shooter", "Tactical Shooter"),
        "Strategy" to listOf("Turn-Based Strategy", "RTS", "Grand Strategy", "Tower Defense"),
        "Indie" to listOf("Roguelike", "Metroidvania", "Puzzle", "Casual"),
        "Simulation" to listOf("Management", "Life Sim", "City Builder", "Farming Sim"),
        "Adventure" to listOf("Story Rich", "Point & Click", "Visual Novel", "Atmospheric"),
        "Horror" to listOf("Psychological Horror", "Survival Horror", "Atmospheric", "Dark"),
        "Platformer" to listOf("Precision Platformer", "2D Platformer", "3D Platformer", "Metroidvania"),
        "Survival" to listOf("Open World Survival Craft", "Survival Horror", "Crafting", "Exploration"),
        "Puzzle" to listOf("Logic", "Minimalist", "First-Person Puzzle"),
        "Racing" to listOf("Automobile Sim", "Arcade Racer", "Sim Racing"),
        "Sports" to listOf("Football", "Basketball", "Skating")
    )

    val tagBlacklist = arrayOf(
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
}
