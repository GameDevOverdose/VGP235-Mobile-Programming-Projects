package com.lasallecollegevancouver.myfinalapp

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
        "Sandbox" to "Open World",
        "RPG" to "Story Rich"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)

        val searchButton = findViewById<Button>(R.id.searchButton_id)
        val steamIdInput = findViewById<EditText>(R.id.steamIdTextEdit_id)
        val recommendButton = findViewById<Button>(R.id.recommendGamesButton_id)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView_id)

        recyclerView.layoutManager = LinearLayoutManager(this)

        searchButton.setOnClickListener {
            val steamId = steamIdInput.text.toString().trim()
            if (steamId.isNotEmpty()) {
                updateUI(steamId)
            } else {
                Toast.makeText(this, "Please enter a Steam ID", Toast.LENGTH_SHORT).show()
            }
        }

        recommendButton.setOnClickListener {
            toggleRecommendations()
        }

        // Initial load with a known Steam ID
        updateUI("76561198314066783")
    }

    private fun toggleRecommendations() {
        val data = currentUserData ?: return
        val recommendButton = findViewById<Button>(R.id.recommendGamesButton_id)

        if (isShowingRecommendations) {
            isShowingRecommendations = false
            recommendButton.text = "Start Recommendation"
            showProfileCategories(data)
        } else {
            isShowingRecommendations = true
            recommendButton.text = "Back to Profile"
            fetchDynamicRecommendations(data)
        }
    }

    private fun fetchDynamicRecommendations(data: FullUserData) {
        val progressBar = findViewById<ProgressBar>(R.id.progressBar_id)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView_id)
        progressBar.visibility = View.VISIBLE
        recyclerView.adapter = CategoryAdapter(emptyList())

        val ownedAppIds = data.ownedGames?.games?.mapNotNull { it.appid }?.toSet() ?: emptySet()
        val topGenres = data.topGenres.take(3)
        
        if (topGenres.isEmpty()) {
            progressBar.visibility = View.GONE
            return
        }

        // Collect all necessary search terms
        val searchTerms = mutableSetOf<String>()
        topGenres.forEach { genre ->
            searchTerms.add(genre)
            searchTerms.add(adjacentTags[genre] ?: genre)
        }

        val resultsMap = mutableMapOf<String, List<Game>>()
        var completedCalls = 0

        fun processCuratedList() {
            val allSeenIds = ownedAppIds.toMutableSet()
            val recommendations = mutableListOf<Category>()

            topGenres.forEach { genre ->
                val adjacentTag = adjacentTags[genre] ?: genre
                val genreGames = resultsMap[genre] ?: emptyList()
                val adjacentGames = resultsMap[adjacentTag] ?: emptyList()

                val curatedGames = mutableListOf<Game>()

                // 1. Popular Pick: The most "Relevant" hit that is also a major seller
                genreGames
                    .filter { it.appid !in allSeenIds }
                    .take(10) // Only look at the most relevant results
                    .maxByOrNull { it.reviewCount }
                    ?.let {
                        it.recommendationType = "Popular"
                        curatedGames.add(it)
                        it.appid?.let { id -> allSeenIds.add(id) }
                    }

                // 2. Adjacent Pick: Broadening the DNA within the top relevance bracket
                adjacentGames
                    .filter { it.appid !in allSeenIds }
                    .take(15)
                    .maxByOrNull { it.reviewCount }
                    ?.let {
                        it.recommendationType = "Adjacent"
                        curatedGames.add(it)
                        it.appid?.let { id -> allSeenIds.add(id) }
                    }

                // 3. Niche Pick (Hidden Gem): High-score "Cult Classics" with lower review volume
                // We look for games with elite scores (90%+) but under 15k reviews
                genreGames
                    .filter { it.appid !in allSeenIds && it.reviewCount in 500..15000 && it.reviewScore >= 90 }
                    .maxByOrNull { it.reviewScore }
                    ?.let {
                        it.recommendationType = "Niche"
                        curatedGames.add(it)
                        it.appid?.let { id -> allSeenIds.add(id) }
                    }
                
                // Fallback for Niche: If no elite gem found in Top Sellers, 
                // we'll take the highest rated game that's further down the results
                if (curatedGames.size < 3) {
                    genreGames
                        .filter { it.appid !in allSeenIds && it.reviewCount > 100 }
                        .sortedByDescending { it.reviewScore }
                        .drop(5) // Ensure we skip the obvious ones
                        .firstOrNull()
                        ?.let {
                            it.recommendationType = "Niche"
                            curatedGames.add(it)
                            it.appid?.let { id -> allSeenIds.add(id) }
                        }
                }

                if (curatedGames.isNotEmpty()) {
                    recommendations.add(Category("$genre DNA", curatedGames))
                }
            }

            progressBar.visibility = View.GONE
            recyclerView.adapter = CategoryAdapter(recommendations)
        }

        searchTerms.forEach { term ->
            repository.searchGamesByGenre(term) { games ->
                resultsMap[term] = games
                completedCalls++
                if (completedCalls == searchTerms.size) {
                    processCuratedList()
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
        findViewById<RecyclerView>(R.id.recyclerView_id).adapter = CategoryAdapter(categories, data.topGenres)
    }

    private fun updateUI(steamId: String) {
        val progressBar = findViewById<ProgressBar>(R.id.progressBar_id)
        val recommendButton = findViewById<Button>(R.id.recommendGamesButton_id)
        progressBar.visibility = View.VISIBLE

        repository.loadFullUserData(steamId) { data ->
            currentUserData = data
            isShowingRecommendations = false
            recommendButton.text = "Start Recommendation"
            progressBar.visibility = View.GONE

            if (data.player == null) {
                Toast.makeText(this, "User not found or profile is private", Toast.LENGTH_SHORT).show()
                findViewById<TextView>(R.id.nameTextView_id).text = "Name: N/A"
                findViewById<TextView>(R.id.countryTextView_id).text = "Country: N/A"
                findViewById<TextView>(R.id.gamesOwnedTextView_id).text = "Games Owned: 0"
                findViewById<TextView>(R.id.hoursPlayedTextView_id).text = "Total Hours: 0h"
                findViewById<TextView>(R.id.recentlyPlayedTextView_id).text = "Recent: None"
                findViewById<TextView>(R.id.topGenresTextView_id).text = "Genres: N/A"
                findViewById<RecyclerView>(R.id.recyclerView_id).adapter = CategoryAdapter(emptyList())
                return@loadFullUserData
            }

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
