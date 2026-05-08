package com.lasallecollegevancouver.loginapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {

    lateinit var nameTextView: TextView
    lateinit var passwordTextView: TextView
    lateinit var errorTextView: TextView

    companion object
    {
        var name: String? = null
        var password: String? = null
    }

    override fun onResume() {
        super.onResume()

        nameTextView.text = ""
        passwordTextView.text = ""
        errorTextView.text = ""
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.login_layout)

        nameTextView = findViewById<TextView>(R.id.nameText1_id)
        passwordTextView = findViewById<TextView>(R.id.passwordText1_id)
        errorTextView = findViewById<TextView>(R.id.errorText1_id)

        findViewById<Button>(R.id.loginButton1_id).setOnClickListener{
            if(nameTextView.text.toString() == name && passwordTextView.text.toString() == password)
            {
                //Login Successful
                val intent = Intent(this, WelcomeActivity::class.java)
                startActivity(intent)
            }
            else
            {
                errorTextView.text = "Name or Password is Incorrect"
            }
        }

        findViewById<TextView>(R.id.registerButton1_id).setOnClickListener{
            val intent = Intent(this, SigninActivity::class.java)
            startActivity(intent)
        }

    }
}