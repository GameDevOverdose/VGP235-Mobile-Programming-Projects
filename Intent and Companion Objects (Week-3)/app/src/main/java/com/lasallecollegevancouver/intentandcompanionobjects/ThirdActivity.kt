package com.lasallecollegevancouver.intentandcompanionobjects

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.lasallecollegevancouver.intentandcompanionobjects.FirstActivity.Companion.score

class ThirdActivity : AppCompatActivity() {

    lateinit var numberTextView: TextView


    override fun onResume() {
        super.onResume()
        numberTextView.text = score.toString()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.third_layout)

        numberTextView = findViewById<TextView>(R.id.numberText3_id)
        findViewById<Button>(R.id.plusButton3_id).setOnClickListener {
            ++score
            numberTextView.text = score.toString()
        }
    }
}