package com.lasallecollegevancouver.myfragmentapp

import android.os.Bundle
import android.widget.Button
import android.widget.FrameLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity(), FragmentListener
{
    override fun removeButtonClicked()
    {
        supportFragmentManager.findFragmentByTag("MyFragTag")?.let {
            supportFragmentManager
                .beginTransaction()
                .remove(it)
                .commit()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val addFragButton: Button = findViewById<Button>(R.id.addFragmentButton_id)

        if(supportFragmentManager.findFragmentByTag("MyFragTag") == null)
        {
            addFragButton.setOnClickListener {
                supportFragmentManager
                    .beginTransaction()
                    .add(R.id.container_id,
                        MyFragment.instance(),
                        "MyFragTag")
                    .commit()
            }
        }
        }
}
