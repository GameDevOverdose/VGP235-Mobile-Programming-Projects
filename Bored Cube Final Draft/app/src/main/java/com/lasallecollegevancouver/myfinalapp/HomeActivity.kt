package com.lasallecollegevancouver.myfinalapp

import android.content.Intent
import android.opengl.GLSurfaceView
import android.os.Bundle
import android.view.MotionEvent
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity

class HomeActivity : AppCompatActivity() {

    private lateinit var container: FrameLayout
    private lateinit var glView: GLSurfaceView
    private lateinit var renderer: CubeRenderer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        container = findViewById(R.id.cubeContainer)

        glView = GLSurfaceView(this)
        glView.setEGLContextClientVersion(2)

        renderer = CubeRenderer(this)

        glView.setRenderer(renderer)
        glView.renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY

        container.addView(glView)

        renderer.launcher = { game ->
            runOnUiThread {
                val intent = when (game) {
                    //"tictactoe" -> Intent(this, TicTacToeActivity::class.java)
                    //"minesweeper" -> Intent(this, MinesweeperActivity::class.java)
                    //"snake" -> Intent(this, SnakeActivity::class.java)
                    //"memory" -> Intent(this, MemoryActivity::class.java)
                    //"rps" -> Intent(this, RPSActivity::class.java)
                    //"ai" -> Intent(this, AIChatActivity::class.java)
                    else -> null
                }
                intent?.let { startActivity(it) }
            }
        }

        container.setOnTouchListener { _, event ->
            renderer.onTouch(event)
            true
        }
    }
}