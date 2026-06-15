package com.lasallecollegevancouver.myfinalapp

import android.text.Editable
import android.text.TextWatcher
import android.os.Bundle
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

    private val adjacentTags = mapOf(
        "RPG" to "Action RPG",
        "Action" to "Adventure",
        "Shooter" to "FPS",
        "Strategy" to "Turn-Based Strategy",
        "Indie" to "Roguelike",
        "Simulation" to "Management",
        "Adventure" to "Story Rich",
        "Horror" to "Psychological Horror",
        "Psychological Horror" to "Atmospheric",
        "Platformer" to "Precision Platformer",
        "Survival" to "Open World Survival Craft",
        "Co-op" to "Online Co-Op",
        "Puzzle" to "Logic",
        "Racing" to "Automobile Sim",
        "Sports" to "Football",
        "Sandbox" to "Open World"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)

        val steamIdInput = findViewById<EditText>(R.id.steamIdTextEdit_id)
        val recommendButton = findViewById<Button>(R.id.recommendGamesButton_id)
        val recyclerView = findViewById<RecyclerView>(R.id.playerDataRv_id)

        recyclerView.layoutManager = LinearLayoutManager(this)

        steamIdInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val steamId = s.toString().trim()
                if (steamId.length == 17) {
                    updateUI(steamId)
                } else {
                    findViewById<View>(R.id.playerHeaderLinearLayout_id).visibility = View.GONE
                    findViewById<RecyclerView>(R.id.playerDataRv_id).visibility = View.GONE
                    findViewById<Button>(R.id.recommendGamesButton_id).visibility = View.GONE
                }
            }
        })

        recommendButton.setOnClickListener {
            toggleRecommendations()
        }

        // Initial load with a known Steam ID
        updateUI("76561198314066783")
    }

    private fun toggleRecommendations() {
        val data = currentUserData ?: return
        val recommendButton = findViewById<Button>(R.id.recommendGamesButton_id)
        val topGenresText = findViewById<TextView>(R.id.topGenresTextView_id)

        if (isShowingRecommendations) {
            isShowingRecommendations = false
            recommendButton.text = "Start Recommendation"
            topGenresText.visibility = View.VISIBLE
            showProfileCategories(data)
        } else {
            isShowingRecommendations = true
            recommendButton.text = "Back to Profile"
            topGenresText.visibility = View.GONE
            
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
        recyclerView.adapter = CategoryAdapter(emptyList())

        val ownedAppIds = data.ownedGames?.games?.mapNotNull { it.appid }?.toSet() ?: emptySet()

        val selectedGenres = if (plusTags.isEmpty() && minusTags.isEmpty()) {
            // Default logic: Pick 3 random genres from top 6, maintaining original relative order
            val randomSubset = data.topGenres.take(6).shuffled().take(3).toSet()
            data.topGenres.filter { it in randomSubset }
        } else {
            // Filtered logic:
            // 1. Start with a random mix of top genres (base variety)
            val initialDna = data.topGenres.take(6).shuffled().take(4).toMutableList()
            // 2. Add plus tags with high priority
            val enhancedDna = (plusTags.take(8) + initialDna).distinct()
            // 3. Remove minus tags and limit
            enhancedDna.filter { tag ->
                minusTags.none { it.equals(tag, ignoreCase = true) }
            }.take(6)
        }

        if (selectedGenres.isEmpty()) {
            progressBar.visibility = View.GONE
            Toast.makeText(this, "No matching genres found with your filters!", Toast.LENGTH_LONG).show()
            return
        }

        // Collect all necessary search terms
        val searchTerms = mutableSetOf<String>()
        selectedGenres.forEach { genre ->
            searchTerms.add(genre)
            adjacentTags[genre]?.let { searchTerms.add(it) }
        }

        val resultsMap = mutableMapOf<String, List<Game>>()
        var completedCalls = 0

        recyclerView.adapter = CategoryAdapter(emptyList(), isRecommendation = true)

        /**
         * FEATURE: IMAGE VERIFICATION
         * If enabled, we verify that a game has a valid library poster on Steam servers before picking it.
         * To disable this feature, simply set 'verifyImagesEnabled' to false.
         */
        val verifyImagesEnabled = true

        fun findPickWithImage(candidates: List<Game>): Game? {
            if (verifyImagesEnabled) {
                // Return the first candidate that actually has an image
                val verified = candidates.firstOrNull { repository.hasLibraryImage(it.appid) }
                if (verified != null) return verified
            }
            // Fallback to the first one if feature is disabled or no verified images found
            return candidates.firstOrNull()
        }

        fun processCuratedList() {
            val allSeenIds = ownedAppIds.toMutableSet()
            val recommendations = mutableListOf<Category>()

            selectedGenres.forEach { genre ->
                val adjacentTag = adjacentTags[genre] ?: genre
                val genreGames = resultsMap[genre] ?: emptyList()
                val adjacentGames = resultsMap[adjacentTag] ?: emptyList()

                val curatedGames = mutableListOf<Game>()

                // 1. Popular Pick: Randomly pick one from the top 10 most relevant hits
                val popularCandidates = genreGames
                    .filter { it.appid !in allSeenIds }
                    .take(10)
                    .shuffled()
                
                findPickWithImage(popularCandidates)?.let {
                    it.recommendationType = getString(R.string.recommendation_type_popular)
                    curatedGames.add(it)
                    it.appid?.let { id -> allSeenIds.add(id) }
                }

                // 2. Adjacent Pick: Randomly pick one from the top 15 related matches
                val adjacentCandidates = adjacentGames
                    .filter { it.appid !in allSeenIds }
                    .take(15)
                    .shuffled()

                findPickWithImage(adjacentCandidates)?.let {
                    it.recommendationType = getString(R.string.recommendation_type_adjacent)
                    curatedGames.add(it)
                    it.appid?.let { id -> allSeenIds.add(id) }
                }

                // 3. Niche Pick (Hidden Gem): Randomly pick from all matching titles
                val nicheCandidates = genreGames
                    .filter { it.appid !in allSeenIds }
                    .shuffled()

                findPickWithImage(nicheCandidates)?.let {
                    it.recommendationType = getString(R.string.recommendation_type_niche)
                    curatedGames.add(it)
                    it.appid?.let { id -> allSeenIds.add(id) }
                }
                
                // Fallback for Niche: Pick randomly from a deeper pool
                if (curatedGames.size < 3) {
                    val fallbackCandidates = genreGames
                        .filter { it.appid !in allSeenIds }
                        .drop(5)
                        .take(20)
                        .shuffled()

                    findPickWithImage(fallbackCandidates)?.let {
                        it.recommendationType = getString(R.string.recommendation_type_niche)
                        curatedGames.add(it)
                        it.appid?.let { id -> allSeenIds.add(id) }
                    }
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

        searchTerms.forEach { term ->
            repository.searchGamesByGenre(term) { games ->
                resultsMap[term] = games
                completedCalls++
                if (completedCalls == searchTerms.size) {
                    // Image verification involves network calls, so we run curation in a background thread
                    Thread {
                        processCuratedList()
                    }.start()
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
        val progressBar = findViewById<ProgressBar>(R.id.progressBar_id)
        val recommendButton = findViewById<Button>(R.id.recommendGamesButton_id)
        val playerHeader = findViewById<View>(R.id.playerHeaderLinearLayout_id)
        val recyclerView = findViewById<RecyclerView>(R.id.playerDataRv_id)
        
        progressBar.visibility = View.VISIBLE

        repository.loadFullUserData(steamId) { data ->
            currentUserData = data
            isShowingRecommendations = false
            recommendButton.text = "Start Recommendation"
            progressBar.visibility = View.GONE

            if (data.player == null) {
                Toast.makeText(this, "User not found or profile is private", Toast.LENGTH_SHORT).show()
                playerHeader.visibility = View.GONE
                recyclerView.visibility = View.GONE
                recommendButton.visibility = View.GONE
                return@loadFullUserData
            }

            // Show elements now that we have data
            playerHeader.visibility = View.VISIBLE
            recyclerView.visibility = View.VISIBLE
            recommendButton.visibility = View.VISIBLE

            // Update Profile Info
            findViewById<TextView>(R.id.nameTextView_id).text = "Name: ${data.player.personaname ?: "N/A"}"
            findViewById<TextView>(R.id.countryTextView_id).text = "Country: ${data.player.loccountrycode ?: "N/A"}"
            findViewById<ImageView>(R.id.avatarImageView_id).load(data.player.avatarfull) {
                crossfade(true)
                placeholder(android.R.drawable.progress_indeterminate_horizontal)
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

            // Update Top Genres text in header
            val topGenres = data.topGenres
            findViewById<TextView>(R.id.topGenresTextView_id).text = "Top Genres: ${topGenres.ifEmpty { listOf("N/A") }.joinToString(", ")}"

            // Show initial profile categories
            showProfileCategories(data)
        }
    }
}
