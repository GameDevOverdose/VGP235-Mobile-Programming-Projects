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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)

        val searchButton = findViewById<Button>(R.id.searchButton_id)
        val steamIdInput = findViewById<EditText>(R.id.steamIdTextEdit_id)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView_id)

        recyclerView.layoutManager = LinearLayoutManager(this)

        // Set initial dummy data to avoid empty screen
        recyclerView.adapter = CategoryAdapter(getDummyData())

        searchButton.setOnClickListener {
            val steamId = steamIdInput.text.toString().trim()
            if (steamId.isNotEmpty()) {
                updateUI(steamId)
            } else {
                Toast.makeText(this, "Please enter a Steam ID", Toast.LENGTH_SHORT).show()
            }
        }

        // Initial load with a known Steam ID
        updateUI("76561198314066783")
    }

    fun getDummyData(): List<Category> {
        return listOf(
            Category(
                "Featured",
                listOf(
                    Game(name = "Hades", imageRes = android.R.drawable.ic_menu_gallery),
                    Game(name = "Celeste", imageRes = android.R.drawable.ic_menu_gallery)
                )
            )
        )
    }

    private fun updateUI(steamId: String) {
        val progressBar = findViewById<ProgressBar>(R.id.progressBar_id)
        progressBar.visibility = View.VISIBLE

        repository.loadFullUserData(steamId) { data ->
            progressBar.visibility = View.GONE

            if (data.player == null) {
                Toast.makeText(this, "User not found or profile is private", Toast.LENGTH_SHORT).show()
                // Clear UI or show defaults
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

            // --- Update RecyclerView with Real Data ---
            val categories = mutableListOf<Category>()

            // 1. Recently Played Category
            data.recentlyPlayed?.let {
                if (it.isNotEmpty()) {
                    categories.add(Category("Recently Played", it))
                }
            }

            // 2. Most Played Games Category
            owned?.games?.let { games ->
                val mostPlayed = games.sortedByDescending { it.playtime_forever ?: 0 }.take(10)
                if (mostPlayed.isNotEmpty()) {
                    categories.add(Category("Most Played", mostPlayed))
                }
            }

            // 3. All Owned Games Category
            owned?.games?.let {
                if (it.isNotEmpty()) {
                    categories.add(Category("Library", it))
                }
            }

            // --- Update Top Genres from API data ---
            val topGenres = data.topGenres
            findViewById<TextView>(R.id.topGenresTextView_id).text = "Top Genres: ${topGenres.ifEmpty { listOf("N/A") }.joinToString(", ")}"

            // Refresh the adapter with the new categories and top genres
            findViewById<RecyclerView>(R.id.recyclerView_id).adapter = CategoryAdapter(categories, topGenres)
        }
    }
}
