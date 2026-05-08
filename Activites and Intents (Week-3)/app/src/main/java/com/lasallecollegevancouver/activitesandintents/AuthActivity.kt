package com.lasallecollegevancouver.activitesandintents

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class AuthActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.auth_layout)

        val backButton = findViewById<Button>(R.id.backButton_id)
        val textView = findViewById<TextView>(R.id.textView_id)

        textView.text = intent.getStringExtra("key") ?: ""

        backButton.setOnClickListener {
            finish()
        }
    }
}