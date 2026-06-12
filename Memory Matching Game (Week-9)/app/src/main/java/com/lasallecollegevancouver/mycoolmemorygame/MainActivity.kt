package com.lasallecollegevancouver.mycoolmemorygame

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

interface listener
{
    fun GameWon()
}

class MainActivity : AppCompatActivity(), listener {

    lateinit var winTextView: TextView

    override fun GameWon()
    {
        if(AppData.matchCount == AppData.gridSize)
        {
            winTextView.text = "Game Won"
        }
    }

    lateinit var restartButton: Button
    lateinit var gameViewRv: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        AppData.createTiles(this)
        cacheOutlets()

        winTextView = findViewById<TextView>(R.id.winTextView_id)
        winTextView.text = ""
    }
}