package com.lasallecollegevancouver.midterm

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ExploreActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.explore_layout)

        val homeButton: TextView = findViewById<TextView>(R.id.homeButton_id)

        homeButton.setOnClickListener {
            //val intent = Intent(this, HomeActivity::class.java)
            //intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            //startActivity(intent)
            finish()
        }
    }
}