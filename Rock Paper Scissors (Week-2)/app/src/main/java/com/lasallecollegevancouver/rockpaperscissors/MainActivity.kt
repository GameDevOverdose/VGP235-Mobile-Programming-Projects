package com.lasallecollegevancouver.rockpaperscissors

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    fun EnemySelection(imageIds: Array<Int>): Int
    {
        val enemyId: ImageView = findViewById<ImageView>(R.id.enemy_id)
        val selection: Int = (0..2).random()
        enemyId.setImageResource(imageIds[selection])

        return selection
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val playerId: ImageView = findViewById<ImageView>(R.id.player_id)
        val enemyId: ImageView = findViewById<ImageView>(R.id.enemy_id)
        playerId.setImageResource(0)
        enemyId.setImageResource(0)

        val winId: TextView = findViewById<TextView>(R.id.win_id)
        winId.text = ""

        val buttonIds = arrayOf(
            R.id.rockButton_id,
            R.id.paperButton_id,
            R.id.scissorsButton_id
        )

        val imageIds = arrayOf(
            R.drawable.rock,
            R.drawable.paper,
            R.drawable.scissors
        )

        val boxViews = arrayOf(
            findViewById<ImageView>(R.id.box1_id),
            findViewById<ImageView>(R.id.box2_id),
            findViewById<ImageView>(R.id.box3_id)
        )

        var playerSelection: Int = -1
        var enemySelection: Int = -1

        var currentMatchIndex = -1
        var playerWinCount = 0
        var enemyWinCount = 0
        var wonIndex = 0

        for (i in buttonIds.indices)
        {
            val textView = findViewById<TextView>(buttonIds[i])

            textView.setOnClickListener{
                playerSelection = i
                playerId.setImageResource(imageIds[playerSelection])
                enemySelection = EnemySelection(imageIds)

                if(currentMatchIndex == 4)
                {
                    currentMatchIndex = -1
                    winId.text = ""

                    for (i in boxViews.indices)
                    {
                        boxViews[i].setImageResource(0)
                    }
                }

                if(playerSelection == enemySelection)
                {
                    wonIndex = 0
                }
                else
                {
                    ++currentMatchIndex

                    if(enemySelection == 2 && playerSelection == 0)
                    {
                        wonIndex = 1
                    }
                    else if(enemySelection == 0 && playerSelection == 2)
                    {
                        wonIndex = -1
                    }
                    else
                    {
                        if(playerSelection > enemySelection)
                        {
                            wonIndex = 1
                        }
                        else
                        {
                            wonIndex = -1
                        }
                    }
                }

                if(wonIndex == 1)
                {
                    boxViews[currentMatchIndex].setImageResource(R.drawable.box_win)
                    ++playerWinCount
                }
                else if(wonIndex == -1)
                {
                    boxViews[currentMatchIndex].setImageResource(R.drawable.box_lose)
                    ++enemyWinCount
                }

                if(currentMatchIndex == 2)
                {
                    if(playerWinCount > enemyWinCount)
                    {
                        winId.text = "player won"
                    }
                    else
                    {
                        winId.text = "enemy won"
                    }

                    ++currentMatchIndex;
                }
            }
        }
    }
}