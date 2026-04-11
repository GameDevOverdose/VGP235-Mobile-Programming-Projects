package com.lasallecollegevancouver.a1standroidapp

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

// activity ~ page ~ scene/level
class HomeActivity : AppCompatActivity()
{
    // start
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.home_layout)

        val welcomeTextView: TextView = findViewById<TextView>(R.id.welcome_id)
        val nameTextView: TextView = findViewById<TextView>(R.id.name_id)
        val detailTextView: TextView = findViewById<TextView>(R.id.detail_id)
        val descriptionTextView: TextView = findViewById<TextView>(R.id.description_id)

        nameTextView.visibility = View.GONE
        detailTextView.visibility = View.GONE
        descriptionTextView.visibility = View.GONE

        val iconView1: ImageView = findViewById<ImageView>(R.id.icon1_id)
        val iconView2: ImageView = findViewById<ImageView>(R.id.icon2_id)
        val iconView3: ImageView = findViewById<ImageView>(R.id.icon3_id)
        val iconView4: ImageView = findViewById<ImageView>(R.id.icon4_id)

        val imageView: ImageView = findViewById<ImageView>(R.id.image_id)
        imageView.visibility = View.GONE

        iconView1.setOnClickListener {
            imageView.setImageResource(R.drawable.icon_1)
            imageView.visibility = View.VISIBLE

            welcomeTextView.visibility = View.GONE
            nameTextView.visibility = View.VISIBLE
            detailTextView.visibility = View.VISIBLE
            descriptionTextView.visibility = View.VISIBLE

            nameTextView.text = "Lara Croft"
            detailTextView.text = "27 F"
            descriptionTextView.text = "Archaeologist. Adventurer. Occasional tomb trespasser (for science). I’ve got a PhD in ancient civilizations and a minor in narrowly escaping death traps. I travel a lot—if you’re into spontaneous trips to remote jungles or crumbling ruins, we’ll get along great."
        }

        iconView2.setOnClickListener {
            imageView.setImageResource(R.drawable.icon_2)
            imageView.visibility = View.VISIBLE

            welcomeTextView.visibility = View.GONE
            nameTextView.visibility = View.VISIBLE
            detailTextView.visibility = View.VISIBLE
            descriptionTextView.visibility = View.VISIBLE

            nameTextView.text = "Samus Aran"
            detailTextView.text = "27 F"
            descriptionTextView.text = "Looking for someone who respects personal space (a lot of it), isn’t intimidated by a woman with an arm cannon, and can handle long-distance—like, really long distance. Bonus if you’re cool with the occasional alien encounter and don’t ask too many questions about my past."
        }

        iconView3.setOnClickListener {
            imageView.setImageResource(R.drawable.icon_3)
            imageView.visibility = View.VISIBLE

            welcomeTextView.visibility = View.GONE
            nameTextView.visibility = View.VISIBLE
            detailTextView.visibility = View.VISIBLE
            descriptionTextView.visibility = View.VISIBLE

            nameTextView.text = "Chun Li"
            detailTextView.text = "27 F"
            descriptionTextView.text = "Might punch you in your sleep oWo >o>"
        }

        iconView4.setOnClickListener {
            imageView.setImageResource(R.drawable.icon_4)
            imageView.visibility = View.VISIBLE

            welcomeTextView.visibility = View.GONE
            nameTextView.visibility = View.VISIBLE
            detailTextView.visibility = View.VISIBLE
            descriptionTextView.visibility = View.VISIBLE

            nameTextView.text = "Jill Valentine"
            detailTextView.text = "27 F"
            descriptionTextView.text = "Looking for someone who can stay calm when things go sideways, doesn’t scare easy, and won’t ask why I always check every corner twice. Bonus points if you can mix a decent cocktail—we might need it after surviving the night."
        }
    }
}