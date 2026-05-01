package com.lasallecollegevancouver.kotlinprogrammingapp

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val die: ImageView = findViewById<ImageView>(R.id.die_id)
        val dieButton: Button = findViewById<Button>(R.id.dieButton_id)
        var dieValue: Int

        val images = arrayOf(   R.drawable.die,
                                R.drawable.die_1,
                                R.drawable.die_2,
                                R.drawable.die_3,
                                R.drawable.die_4,
                                R.drawable.die_5,
                                R.drawable.die_6)

        dieButton.setOnClickListener {
            dieValue = (1..6).random()
            die.setImageResource(images[dieValue])
        }

        val testText: TextView = findViewById<TextView>(R.id.test_id)
        var count = 0.0f
        val cycles = 1_000_000

        repeat(cycles)
        {
            if((1..6).random() == 5)
                ++count
        }

        testText.text = (count / cycles).toString()
    }
}