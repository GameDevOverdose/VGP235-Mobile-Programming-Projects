package com.lasallecollegevancouver.intentandcompanionobjects

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import org.w3c.dom.Text

class FirstActivity : AppCompatActivity() {

    lateinit var numberTextView: TextView

    companion object
    {
        var score = 0
    }

    override fun onResume() {
        super.onResume()
        numberTextView.text = score.toString()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.first_layout)

        findViewById<Button>(R.id.goToSecondButton_id).setOnClickListener {
            val intent = Intent(this, SecondActivity::class.java)
            startActivity(intent)
        }

        numberTextView = findViewById<TextView>(R.id.numberText1_id)
        findViewById<Button>(R.id.plusButton1_id).setOnClickListener {
            ++score
            numberTextView.text = score.toString()
        }
    }
}