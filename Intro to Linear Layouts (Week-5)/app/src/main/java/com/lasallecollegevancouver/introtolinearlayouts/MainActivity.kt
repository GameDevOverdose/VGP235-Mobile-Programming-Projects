package com.lasallecollegevancouver.introtolinearlayouts

import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.marginTop

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val parentLayout = findViewById<LinearLayout>(R.id.targetLayout_id)

        for (i in 1..15)
        {
            val textView = TextView(this)

            textView.text = "Text View $i"
            textView.textSize = 18f
            textView.gravity = Gravity.CENTER

            val param = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT)

            param.setMargins(0, 50, 0, 0)

            textView.layoutParams = param
            parentLayout.addView(textView)
        }
    }
}