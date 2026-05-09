package com.lasallecollegevancouver.midterm

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class ExploreActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.explore_layout)

        val homeButton: TextView = findViewById<TextView>(R.id.homeButton_id)

        val avatarDrawables = intArrayOf(
            R.drawable.avatar_1,
            R.drawable.avatar_2,
            R.drawable.avatar_3,
            R.drawable.avatar_4,
            R.drawable.avatar_5
        )

        val names = arrayOf(
            "Natasha",
            "Layla",
            "Sara",
            "Olivia",
            "Elizabeth"
        )

        val occupations = arrayOf(
            "Software Developer",
            "Writer",
            "Musician",
            "Scientist",
            "Engineer",
            "Artist"
        )

        val eyeColors = arrayOf(
            "Brown",
            "Blue",
            "Green",
            "Hazel",
            "Gray",
            "Amber",
            "Other"
        )

        val hairColors = arrayOf(
            "Black",
            "Brown",
            "Blonde",
            "Red",
            "Auburn",
            "Gray",
            "White",
            "Other"
        )

        val bodyTypes = arrayOf(
            "Slim",
            "Athletic",
            "Average",
            "Muscular",
            "Curvy",
            "Heavy"
        )

        val avatarImageView: ImageView = findViewById<ImageView>(R.id.interestAvatar_id)
        val nameTextView: TextView = findViewById<TextView>(R.id.interestName_id)
        val ageTextView: TextView = findViewById<TextView>(R.id.interestAge_id)
        val weightTextView: TextView = findViewById<TextView>(R.id.interestWeight_id)
        val heightTextView: TextView = findViewById<TextView>(R.id.interestHeight_id)
        val eyeColorTextView: TextView = findViewById<TextView>(R.id.interestEyeColor_id)
        val hairColorTextView: TextView = findViewById<TextView>(R.id.interestHairColor_id)
        val occupationView: TextView = findViewById<TextView>(R.id.interestOccupationColor_id)
        val bodyTypeView: TextView = findViewById<TextView>(R.id.interestBodyTypeColor_id)
        val matchTextView: TextView = findViewById<TextView>(R.id.matchText_id)

        val avatar: Int = avatarDrawables.random()
        val name: String = names.random()
        val occupation: String = occupations.random()

        val height: Int = kotlin.random.Random.nextInt(140, 180)
        val weight: Int = kotlin.random.Random.nextInt(60, 100)
        val age: Int = kotlin.random.Random.nextInt(18, 37)

        val eyeColor: String = eyeColors.random()
        val hairColor: String = hairColors.random()
        val bodyType: String = bodyTypes.random()

        avatarImageView.setImageResource(avatar)
        nameTextView.text = name
        ageTextView.text = age.toString()
        heightTextView.text = height.toString()
        weightTextView.text = weight.toString()
        eyeColorTextView.text = eyeColor.toString()
        hairColorTextView.text = hairColor.toString()
        occupationView.text = occupation.toString()
        bodyTypeView.text = bodyType.toString()

        ///////////////////////////////////////////////////////////////////

        var score: Int = 0

        score += if (kotlin.math.abs(height - HomeActivity.height) <= 20) 1 else 0
        score += if (kotlin.math.abs(weight - HomeActivity.weight) <= 10) 1 else 0
        score += if (kotlin.math.abs(age - HomeActivity.age) <= 5) 1 else 0

        score += if (eyeColor == HomeActivity.eyeColor) 1 else 0
        score += if (hairColor == HomeActivity.hairColor) 1 else 0
        score += if (bodyType == HomeActivity.bodyType) 1 else 0

        // fate I guess
        score += if (kotlin.random.Random.nextInt(0, 2) == 1) 6 else 0

        if(score >= 6)
        {
            // it's a match
            matchTextView.text = "It's a match :D"
        }
        else
        {
            // it's not
            matchTextView.text = "It's not meant to be :("
        }

        homeButton.setOnClickListener {
            //val intent = Intent(this, HomeActivity::class.java)
            //intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            //startActivity(intent)
            finish()
        }
    }
}