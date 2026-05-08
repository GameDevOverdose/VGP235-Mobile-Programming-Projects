package com.lasallecollegevancouver.loginapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SigninActivity : AppCompatActivity() {

    lateinit var errorTextView: TextView

    override fun onResume() {
        super.onResume()

        errorTextView.text = ""
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.signin_layout)

        val nameTextView = findViewById<TextView>(R.id.nameText2_id)
        val passwordTextView = findViewById<TextView>(R.id.passwordText2_id)
        val passwordVerifyTextView = findViewById<TextView>(R.id.passwordVerifyText_id)
        errorTextView = findViewById<TextView>(R.id.errorText2_id)

        findViewById<Button>(R.id.signinButton_id).setOnClickListener{

            if(passwordTextView.text.toString() == passwordVerifyTextView.text.toString())
            {
                LoginActivity.name = nameTextView.text.toString()
                LoginActivity.password = passwordTextView.text.toString()

                val intent = Intent(this, WelcomeActivity::class.java)
                startActivity(intent)
            }
            else
            {
                errorTextView.text = "Password is not a match"
            }
        }
    }
}