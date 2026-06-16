package com.lasallecollegevancouver.myfinalapp

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.transition.Fade
import android.transition.Transition
import android.transition.TransitionManager
import android.transition.TransitionSet
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load

class HomeActivity : AppCompatActivity() {

    private val repository = SteamRepository("8D4656FFBAD8F36CF251F84C01D40848")
    private var currentUserData: FullUserData? = null
    private var isShowingRecommendations = false
    private var isSearchBarAtTop = false

    // --- ALGORITHM CONFIGURATION ---
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
    }

    // --- UI CONFIGURATION ---
    private object UIConfig {
        const val useGradient = false
        const val useShadows = false
    }

    private val adjacentTags = mapOf(
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)

        val rootLayout = findViewById<ConstraintLayout>(R.id.root)
        val searchBarCard = findViewById<View>(R.id.searchBarCard_id)
        val steamIdInput = findViewById<EditText>(R.id.steamIdTextEdit_id)
        val clearSearchButton = findViewById<ImageView>(R.id.clearSearch_id)
        val recommendButton = findViewById<Button>(R.id.recommendGamesButton_id)
        val recyclerView = findViewById<RecyclerView>(R.id.playerDataRv_id)

        recyclerView.layoutManager = LinearLayoutManager(this)

        clearSearchButton.setOnClickListener {
            steamIdInput.text.clear()
        }

        steamIdInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val steamId = s.toString().trim()
                
                clearSearchButton.visibility = if (steamId.isNotEmpty()) View.VISIBLE else View.GONE

                if (steamId.isNotEmpty() && !isSearchBarAtTop) {
                    animateSearchBarToTop(rootLayout, searchBarCard)
                } else if (steamId.isEmpty() && isSearchBarAtTop) {
                    animateSearchBarToCenter(rootLayout, searchBarCard)
                }

                if (steamId.length == 17) {
                    hideKeyboard()
                    updateUI(steamId)
                } else {
                    findViewById<View>(R.id.playerHeaderCard_id).visibility = View.GONE
                    findViewById<RecyclerView>(R.id.playerDataRv_id).visibility = View.GONE
                    findViewById<Button>(R.id.recommendGamesButton_id).visibility = View.GONE
                }
            }
        })

        recommendButton.setOnClickListener {
            toggleRecommendations()
        }

        applyUIConfig(rootLayout, searchBarCard)

        // Search bar starts in center because of XML constraints.
        // We don't call updateUI here so the user can see the splash-like centered search.
        //updateUI("76561198314066783")
    }

    private fun applyUIConfig(root: View, searchBar: View) {
        if (!UIConfig.useGradient) {
            root.setBackgroundColor(android.graphics.Color.parseColor("#292e37"))
        }
        
        if (!UIConfig.useShadows) {
            (searchBar as? com.google.android.material.card.MaterialCardView)?.cardElevation = 0f
            findViewById<com.google.android.material.card.MaterialCardView>(R.id.playerHeaderCard_id).cardElevation = 0f
        }
    }

    private fun animateSearchBarToTop(root: ConstraintLayout, card: View) {
        isSearchBarAtTop = true
        val constraintSet = ConstraintSet()
        constraintSet.clone(root)
        
        // Clear center constraints
        constraintSet.clear(card.id, ConstraintSet.BOTTOM)
        
        // Set top constraint with margin
        constraintSet.connect(card.id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 150) // Adjust 150 for top padding
        
        TransitionManager.beginDelayedTransition(root)
        constraintSet.applyTo(root)
    }

    private fun animateSearchBarToCenter(root: ConstraintLayout, card: View) {
        isSearchBarAtTop = false
        val constraintSet = ConstraintSet()
        constraintSet.clone(root)
        
        // Restore center constraints
        constraintSet.connect(card.id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 0)
        constraintSet.connect(card.id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0)
        constraintSet.setVerticalBias(card.id, 0.5f)
        
        TransitionManager.beginDelayedTransition(root)
        constraintSet.applyTo(root)
    }

    private fun hideKeyboard() {
        val view = this.currentFocus
        if (view != null) {
            val imm = getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    private fun toggleRecommendations() {
        val data = currentUserData ?: return
        val recommendButton = findViewById<Button>(R.id.recommendGamesButton_id)
        val rootLayout = findViewById<ConstraintLayout>(R.id.root)

        val transition = TransitionSet().apply {
            addTransition(Fade())
            duration = 400
        }
        TransitionManager.beginDelayedTransition(rootLayout, transition)

        if (isShowingRecommendations) {
            isShowingRecommendations = false
            recommendButton.text = "Start Recommendation"
            showProfileCategories(data)
        } else {
            isShowingRecommendations = true
            recommendButton.text = "Back to Profile"
            
            // Collect selections
            val allGames = (data.ownedGames?.games ?: emptyList()) + (data.recentlyPlayed ?: emptyList())
            val plusGames = allGames.filter { it.selectionState == 1 }.distinctBy { it.appid }
            val minusGames = allGames.filter { it.selectionState == 2 }.distinctBy { it.appid }

            if (plusGames.isEmpty() && minusGames.isEmpty()) {
                fetchDynamicRecommendations(data, emptyList(), emptyList())
            } else {
                val progressBar = findViewById<ProgressBar>(R.id.progressBar_id)
                progressBar.visibility = View.VISIBLE
                
                // Fetch tags for selected games
                repository.fetchGenresForSelectedGames(plusGames + minusGames) { tagsMap: Map<Int, List<String>> ->
                    val plusTags = plusGames.flatMap { tagsMap[it.appid] ?: emptyList<String>() }.toSet()
                    val minusTags = minusGames.flatMap { tagsMap[it.appid] ?: emptyList<String>() }.toSet()
                    fetchDynamicRecommendations(data, plusTags.toList(), minusTags.toList())
                }
            }
        }
    }

    private fun fetchDynamicRecommendations(data: FullUserData, plusTags: List<String>, minusTags: List<String>) {
        val progressBar = findViewById<ProgressBar>(R.id.progressBar_id)
        val recyclerView = findViewById<RecyclerView>(R.id.playerDataRv_id)
        progressBar.visibility = View.VISIBLE
        recyclerView.adapter = CategoryAdapter(emptyList(), isRecommendation = true)

        val ownedAppIds = data.ownedGames?.games?.mapNotNull { it.appid }?.toSet() ?: emptySet()

        val selectedGenres = if (plusTags.isEmpty() && minusTags.isEmpty()) {
            val randomSubset = data.topGenres.take(6).shuffled().take(AlgoConfig.maxGenresNoSelection).toSet()
            data.topGenres.filter { it in randomSubset }
        } else {
            val initialDna = data.topGenres.take(6).shuffled().take(4).toMutableList()
            val enhancedDna = (plusTags.take(8) + initialDna).distinct()
            enhancedDna.filter { tag ->
                minusTags.none { it.equals(tag, ignoreCase = true) }
            }.take(AlgoConfig.maxGenresWithSelection)
        }

        if (selectedGenres.isEmpty()) {
            progressBar.visibility = View.GONE
            Toast.makeText(this, "No matching genres found!", Toast.LENGTH_LONG).show()
            return
        }

        val resultsRelevance = mutableMapOf<String, List<Game>>()
        val resultsPopularity = mutableMapOf<String, List<Game>>()
        val resultsDiscovery = mutableMapOf<String, List<Game>>()
        var completedCalls = 0
        val totalExpectedCalls = selectedGenres.size * 3

        fun processCuratedList() {
            val allSeenIds = ownedAppIds.toMutableSet()
            val failedImageIds = mutableSetOf<Int>()
            val recommendations = mutableListOf<Category>()

            selectedGenres.forEach { genre ->
                val relevancePool = resultsRelevance[genre] ?: emptyList()
                val popularityPool = resultsPopularity[genre] ?: emptyList()
                val discoveryPool = resultsDiscovery[genre] ?: emptyList()
                val curatedGames = mutableListOf<Game>()

                // Helper to pick a single game matching criteria
                fun tryPickOne(pool: List<Game>, type: String, sMin: Int = 0, cMin: Int = 0, sMax: Int = 100, cMax: Int = Int.MAX_VALUE): Boolean {
                    val candidates = pool.filter { 
                        it.appid !in allSeenIds && it.appid !in failedImageIds &&
                        it.reviewScore in sMin..sMax && it.reviewCount in cMin..cMax
                    }.shuffled()

                    for (game in candidates) {
                        val hasValidImage = !AlgoConfig.verifyImagesEnabled || 
                                           (AlgoConfig.useLibrary2xFallback && repository.hasLibraryImage(game.appid)) || 
                                           (AlgoConfig.useLibrary1xFallback && repository.hasLibraryImage(game.appid)) || 
                                           (AlgoConfig.useCapsuleFallback && repository.hasLibraryImage(game.appid)) || 
                                           AlgoConfig.useHeaderFallback || 
                                           (AlgoConfig.useScrapedFallback && game.fallbackImageUrl != null)

                        if (hasValidImage) {
                            game.appid?.let { allSeenIds.add(it) }
                            game.recommendationType = type
                            val gameWithStats = game.copy()
                            curatedGames.add(gameWithStats)
                            return true
                        } else {
                            game.appid?.let { failedImageIds.add(it) }
                        }
                    }
                    return false
                }

                // Helper to fill remaining slots with less restriction
                fun fillRemaining(pool: List<Game>, type: String, sMin: Int = 0, cMin: Int = 0) {
                    val candidates = pool.filter { 
                        it.appid !in allSeenIds && it.appid !in failedImageIds &&
                        it.reviewScore >= sMin && it.reviewCount >= cMin
                    }.distinctBy { it.appid }.shuffled()

                    for (game in candidates) {
                        if (curatedGames.size >= AlgoConfig.strategiesPerGenre) break
                        
                        val hasValidImage = !AlgoConfig.verifyImagesEnabled || 
                                           (AlgoConfig.useLibrary2xFallback && repository.hasLibraryImage(game.appid)) || 
                                           (AlgoConfig.useLibrary1xFallback && repository.hasLibraryImage(game.appid)) || 
                                           (AlgoConfig.useCapsuleFallback && repository.hasLibraryImage(game.appid)) ||
                                           AlgoConfig.useHeaderFallback || 
                                           (AlgoConfig.useScrapedFallback && game.fallbackImageUrl != null)

                        if (hasValidImage) {
                            game.appid?.let { allSeenIds.add(it) }
                            game.recommendationType = type
                            val gameWithStats = game.copy()
                            curatedGames.add(gameWithStats)
                        } else {
                            game.appid?.let { failedImageIds.add(it) }
                        }
                    }
                }

                // 1. Primary Strategy Pass (Try to get diverse types)
                val strategyFunctions = listOf(
                    { tryPickOne(popularityPool, "Blockbuster", cMin = AlgoConfig.blockbusterReviewThreshold) },
                    { tryPickOne(relevancePool, "Genre Staple", sMin = AlgoConfig.minReviewScore, cMin = AlgoConfig.minReviewCount) },
                    { tryPickOne(relevancePool, "Hidden Gem", sMin = AlgoConfig.hiddenGemScoreThreshold, cMax = AlgoConfig.blockbusterReviewThreshold) },
                    { tryPickOne(popularityPool, "Cult Favorite", sMin = 90) },
                    { tryPickOne(relevancePool, "Rising Star", cMin = 500, cMax = 2000) },
                    { tryPickOne(discoveryPool, "Discovery", sMin = AlgoConfig.minReviewScore, cMin = AlgoConfig.minReviewCount) }
                ).shuffled()

                for (strategy in strategyFunctions) {
                    if (curatedGames.size >= AlgoConfig.strategiesPerGenre) break
                    strategy()
                }

                // 2. Safety Fallback: Relaxed Quality (Ensures density if strategies fail)
                if (curatedGames.size < AlgoConfig.strategiesPerGenre) {
                    fillRemaining(relevancePool + popularityPool + discoveryPool, "Highly Rated", sMin = AlgoConfig.minReviewScore, cMin = AlgoConfig.minReviewCount)
                }

                // 3. Desperation Fallback: Lower Score Threshold
                if (curatedGames.size < AlgoConfig.strategiesPerGenre) {
                    fillRemaining(relevancePool + popularityPool + discoveryPool, "Community Pick", sMin = 60, cMin = 100)
                }

                // 4. Final Fallback: Absolute Any (Last resort to guarantee 3 picks)
                if (curatedGames.size < AlgoConfig.strategiesPerGenre) {
                    fillRemaining(relevancePool + popularityPool + discoveryPool, "Wildcard", sMin = 0, cMin = 0)
                }

                if (curatedGames.isNotEmpty()) {
                    recommendations.add(Category(genre, curatedGames))
                }
            }

            runOnUiThread {
                progressBar.visibility = View.GONE
                recyclerView.adapter = CategoryAdapter(recommendations, isRecommendation = true)
            }
        }

        selectedGenres.forEach { genre ->
            repository.searchGamesByGenre(genre, sortBy = "relevance") { games ->
                synchronized(resultsRelevance) {
                    resultsRelevance[genre] = games
                    completedCalls++
                    if (completedCalls == totalExpectedCalls) Thread { processCuratedList() }.start()
                }
            }
            repository.searchGamesByGenre(genre, sortBy = "Reviews_DESC") { games ->
                synchronized(resultsRelevance) {
                    resultsPopularity[genre] = games
                    completedCalls++
                    if (completedCalls == totalExpectedCalls) Thread { processCuratedList() }.start()
                }
            }
            
            // Discovery search using adjacent tags
            val adjacent = adjacentTags[genre] ?: emptyList()
            val discoveryTag = adjacent.shuffled().firstOrNull() ?: genre
            repository.searchGamesByGenre(discoveryTag, sortBy = "relevance") { games ->
                synchronized(resultsRelevance) {
                    resultsDiscovery[genre] = games
                    completedCalls++
                    if (completedCalls == totalExpectedCalls) Thread { processCuratedList() }.start()
                }
            }
        }
    }

    private fun showProfileCategories(data: FullUserData) {
        val categories = mutableListOf<Category>()
        data.ownedGames?.let { owned ->
            val mostPlayed = owned.games?.sortedByDescending { it.playtime_forever ?: 0 }?.take(10)
            if (!mostPlayed.isNullOrEmpty()) {
                categories.add(Category("Most Played", mostPlayed))
            }
            owned.games?.let {
                categories.add(Category("Library", it))
            }
        }
        findViewById<RecyclerView>(R.id.playerDataRv_id).adapter = CategoryAdapter(categories, data.topGenres)
    }

    private fun updateUI(steamId: String) {
        val rootLayout = findViewById<ConstraintLayout>(R.id.root)
        val progressBar = findViewById<ProgressBar>(R.id.progressBar_id)
        val recommendButton = findViewById<Button>(R.id.recommendGamesButton_id)
        val playerHeaderCard = findViewById<View>(R.id.playerHeaderCard_id)
        val recyclerView = findViewById<RecyclerView>(R.id.playerDataRv_id)
        
        progressBar.visibility = View.VISIBLE

        repository.loadFullUserData(steamId) { data ->
            runOnUiThread {
                progressBar.visibility = View.GONE
                currentUserData = data
                isShowingRecommendations = false
                recommendButton.text = "Start Recommendation"

                if (data.player == null) {
                    Toast.makeText(this@HomeActivity, "User not found or profile is private", Toast.LENGTH_SHORT).show()
                    playerHeaderCard.visibility = View.GONE
                    recyclerView.visibility = View.GONE
                    recommendButton.visibility = View.GONE
                    return@runOnUiThread
                }

                val transition = TransitionSet().apply {
                    addTransition(Fade())
                    duration = 500
                }
                TransitionManager.beginDelayedTransition(rootLayout, transition)

                // Show elements now that we have data
                playerHeaderCard.visibility = View.VISIBLE
                recyclerView.visibility = View.VISIBLE
                recommendButton.visibility = View.VISIBLE

                // Update Profile Info
                findViewById<TextView>(R.id.nameTextView_id).text = "Name: ${data.player.personaname ?: "N/A"}"
                findViewById<TextView>(R.id.countryTextView_id).text = "Country: ${data.player.loccountrycode ?: "N/A"}"
                findViewById<ImageView>(R.id.avatarImageView_id).load(data.player.avatarfull) {
                    crossfade(500)
                    placeholder(R.drawable.image_placeholder)
                    error(android.R.drawable.stat_notify_error)
                }

                // Update Games Stats
                val owned = data.ownedGames
                findViewById<TextView>(R.id.gamesOwnedTextView_id).text = "Games Owned: ${owned?.game_count ?: 0}"
                
                val totalHours = (owned?.games?.sumOf { it.playtime_forever ?: 0 } ?: 0) / 60
                findViewById<TextView>(R.id.hoursPlayedTextView_id).text = "Total Hours: ${totalHours}h"

                // Update Recent Activity Summary Text
                val recentNames = data.recentlyPlayed?.joinToString(", ") { it.name ?: "" }
                findViewById<TextView>(R.id.recentlyPlayedTextView_id).text = "Recent: ${recentNames?.ifEmpty { "None" } ?: "None"}"

                // Show initial profile categories
                showProfileCategories(data)
            }
        }
    }
}
