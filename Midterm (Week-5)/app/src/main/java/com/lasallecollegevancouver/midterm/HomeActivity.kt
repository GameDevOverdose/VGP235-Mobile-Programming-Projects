package com.lasallecollegevancouver.midterm

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class HomeActivity : AppCompatActivity() {

    lateinit var nameTextView: TextView
    lateinit var avatarImageView: ImageView

    companion object
    {
        var name: String = ""
        var occupation: String = ""
        var avatarResId: Int = R.drawable.avatar_1

        // height, weight, age, eye color, hair color, body type

        var height: Float = 0.0f
        var weight: Float = 0.0f
        var age: Float = 0.0f
        var eyeColor: String = ""
        var hairColor: String = ""
        var bodyType: String = ""

        var keywords: String = ""
    }

    fun LoadUserData()
    {
        nameTextView.text = name
        avatarImageView.setImageResource(avatarResId)
    }

    override fun onResume() {
        super.onResume()

        LoadUserData()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.home_layout)

        val exploreButton: TextView = findViewById<TextView>(R.id.exploreButton_id)
        val userButton: TextView = findViewById<TextView>(R.id.userButton_id)

        nameTextView = findViewById<TextView>(R.id.name_id)
        avatarImageView = findViewById<ImageView>(R.id.avatar_id)

        LoadUserData()

        exploreButton.setOnClickListener {
            val intent = Intent(this, ExploreActivity::class.java)
            startActivity(intent)
        }

        userButton.setOnClickListener {
            val intent = Intent(this, UserAccountActivity::class.java)
            startActivity(intent)
        }
    }
}