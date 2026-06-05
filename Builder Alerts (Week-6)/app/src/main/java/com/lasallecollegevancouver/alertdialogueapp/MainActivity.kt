package com.lasallecollegevancouver.alertdialogueapp

import android.os.Bundle
import android.text.InputType
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.myButton_id).setOnClickListener {

            val builder = AlertDialog.Builder(this)
            builder.setTitle("My Alert")
            builder.setMessage("Please enter your password")

            val passwordInput = EditText(this)
            builder.setView(passwordInput)

            builder.setPositiveButton("Reset"){ a, b ->
                checkPassword(passwordInput.text.toString())
            }

            builder.setNegativeButton("Cancel"){ a, b -> }

            val dialogue = builder.create()
            dialogue.show()

        }
    }

    fun checkPassword(pass: String)
    {

    }
}