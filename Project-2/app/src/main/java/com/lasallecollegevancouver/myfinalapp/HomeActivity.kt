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
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView_id)
        val recommendButton = findViewById<Button>(R.id.recommendGamesButton_id)

        if (isShowingRecommendations) {
            isShowingRecommendations = false
            recommendButton.text = "Start Recommendation"
            showProfileCategories(data)
        } else {
            isShowingRecommendations = true
            recommendButton.text = "Back to Profile"
            // Part 1: Empty the recycler view
            recyclerView.adapter = CategoryAdapter(emptyList(), emptyList())
        }
    }

    private fun generateRecommendations(data: FullUserData): List<Category> {
        val ownedAppIds = data.ownedGames?.games?.mapNotNull { it.appid }?.toSet() ?: emptySet()
        val recommendations = mutableListOf<Category>()

        val pool = mapOf(
            "Action" to listOf(Game(1245620, "Elden Ring"), Game(1091500, "Cyberpunk 2077"), Game(582010, "Monster Hunter: World")),
            "Shooter" to listOf(Game(782330, "DOOM Eternal"), Game(1240440, "Halo Infinite"), Game(1085660, "Destiny 2")),
            "RPG" to listOf(Game(1086940, "Baldur's Gate 3"), Game(292030, "The Witcher 3: Wild Hunt"), Game(1340710, "Persona 5 Royal")),
            "Strategy" to listOf(Game(289070, "Sid Meier’s Civ VI"), Game(281990, "Stellaris"), Game(268500, "XCOM 2")),
            "Adventure" to listOf(Game(1174180, "Red Dead Redemption 2"), Game(1151640, "Horizon Zero Dawn"), Game(1151640, "God of War")),
            "Indie" to listOf(Game(367520, "Hollow Knight"), Game(753640, "Outer Wilds"), Game(413150, "Stardew Valley")),
            "Third-Person Shooter" to listOf(Game(1085660, "Destiny 2"), Game(1238810, "Battlefield V"), Game(1238840, "Battlefield 1")),
            "Roguelike" to listOf(Game(1145360, "Hades"), Game(553850, "Dead Cells"), Game(632360, "Risk of Rain 2"))
        )

        data.topGenres.take(3).forEach { genre ->
            val genreKey = pool.keys.find { it.equals(genre, ignoreCase = true) }
            val gamesForGenre = if (genreKey != null) pool[genreKey] else pool["Action"]

            val filtered = gamesForGenre?.filter { it.appid !in ownedAppIds }?.take(3) ?: emptyList()
            if (filtered.isNotEmpty()) {
                recommendations.add(Category("Because you like $genre", filtered))
            }
        }

        if (recommendations.isEmpty()) {
            recommendations.add(Category("Recommended for You", listOf(
                Game(1245620, "Elden Ring"),
                Game(1086940, "Baldur's Gate 3"),
                Game(1174180, "Red Dead Redemption 2")
            ).filter { it.appid !in ownedAppIds }))
        }

        return recommendations
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
