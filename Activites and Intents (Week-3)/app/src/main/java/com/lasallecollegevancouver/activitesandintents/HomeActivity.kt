package com.lasallecollegevancouver.activitesandintents

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class HomeActivity : AppCompatActivity() {

    lateinit var dataText: EditText

    override fun onResume() {
        super.onResume()

        dataText.text.clear()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.home_layout)

        val loginButton = findViewById<Button>(R.id.loginButton_id)
        dataText = findViewById<EditText>(R.id.nameEditText_id)

        loginButton.setOnClickListener {
            val intent = Intent(this, AuthActivity::class.java)

            val textData = dataText.text.toString()
            intent.putExtra("key", textData)

            startActivity(intent)
        }
    }
}