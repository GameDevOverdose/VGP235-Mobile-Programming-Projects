package com.lasallecollegevancouver.outletfromlayout

import android.os.Bundle
import android.widget.Button
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.home_activity)

        val container: FrameLayout = findViewById<FrameLayout>(R.id.container_id)

        val myLayout: LinearLayout = layoutInflater.inflate(R.layout.my_layout,
            container, false) as LinearLayout

        container.addView(myLayout)

        val myButton = myLayout.findViewById<Button>(R.id.myButton_id)
        val myTextView = myLayout.findViewById<TextView>(R.id.mytextView_id)

        myButton.setOnClickListener {
            myTextView.text = "Button Clicked"
        }
    }
}