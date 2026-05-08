package com.lasallecollegevancouver.intentandcompanionobjects

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.lasallecollegevancouver.intentandcompanionobjects.FirstActivity.Companion.score

class SecondActivity : AppCompatActivity() {

    lateinit var numberTextView: TextView

    override fun onResume() {
        super.onResume()
        numberTextView.text = score.toString()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.second_layout)

        findViewById<Button>(R.id.goToThirdButton_id).setOnClickListener {
            val intent = Intent(this, ThirdActivity::class.java)
            startActivity(intent)
        }

        numberTextView = findViewById<TextView>(R.id.numberText2_id)
        findViewById<Button>(R.id.plusButton2_id).setOnClickListener {
            ++score
            numberTextView.text = score.toString()
        }
    }
}