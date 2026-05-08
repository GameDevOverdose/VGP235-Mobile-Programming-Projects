package com.lasallecollegevancouver.midterm

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class UserAccountActivity : AppCompatActivity() {

    fun SetAvatar(vararg imageViews: ImageView)
    {
        for(imageView in imageViews)
        {
            imageView.setOnClickListener {

                HomeActivity.avatarResId = when(imageView.id)
                {
                    R.id.avatarImageView1_id -> R.drawable.avatar_1
                    R.id.avatarImageView2_id -> R.drawable.avatar_2
                    R.id.avatarImageView3_id -> R.drawable.avatar_3
                    R.id.avatarImageView4_id -> R.drawable.avatar_4
                    R.id.avatarImageView5_id -> R.drawable.avatar_5
                    else -> R.drawable.avatar_1
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.user_account_layout)

        val saveButton: TextView = findViewById<TextView>(R.id.saveButton_id)

        val avatarImage1: ImageView = findViewById<ImageView>(R.id.avatarImageView1_id)
        val avatarImage2: ImageView = findViewById<ImageView>(R.id.avatarImageView2_id)
        val avatarImage3: ImageView = findViewById<ImageView>(R.id.avatarImageView3_id)
        val avatarImage4: ImageView = findViewById<ImageView>(R.id.avatarImageView4_id)
        val avatarImage5: ImageView = findViewById<ImageView>(R.id.avatarImageView5_id)

        SetAvatar(avatarImage1, avatarImage2, avatarImage3, avatarImage4, avatarImage5)

        val nameText: EditText = findViewById<EditText>(R.id.nameText_id)
        val occupationText: EditText = findViewById<EditText>(R.id.occupationText_id)
        val heightText: EditText = findViewById<EditText>(R.id.heightText_id)
        val weightText: EditText = findViewById<EditText>(R.id.weightText_id)
        val eyeColorText: EditText = findViewById<EditText>(R.id.eyeColorText_id)
        val hairColorText: EditText = findViewById<EditText>(R.id.hairColorText_id)
        val bodyTypeText: EditText = findViewById<EditText>(R.id.bodyTypeText_id)

        nameText.setText(HomeActivity.name ?: "")
        occupationText.setText(HomeActivity.occupation ?: "")

        if(HomeActivity.height != 0.0f) heightText.setText(HomeActivity.height.toString())
        if(HomeActivity.weight != 0.0f) weightText.setText(HomeActivity.weight.toString())

        eyeColorText.setText(HomeActivity.eyeColor ?: "")
        hairColorText.setText(HomeActivity.hairColor ?: "")
        bodyTypeText.setText(HomeActivity.bodyType ?: "")

        saveButton.setOnClickListener {

            HomeActivity.name = nameText.text.toString()
            HomeActivity.occupation = occupationText.text.toString()

            HomeActivity.height =
                heightText.text.toString().toFloatOrNull() ?: 0f

            HomeActivity.weight =
                weightText.text.toString().toFloatOrNull() ?: 0f

            HomeActivity.eyeColor = eyeColorText.text.toString()
            HomeActivity.hairColor = hairColorText.text.toString()
            HomeActivity.bodyType = bodyTypeText.text.toString()

            finish()
        }
    }
}