package com.lasallecollegevancouver.mycoolmemorygame

import android.content.Context

class AppData
{
    companion object
    {
        var gridSize: Int = 4

        var tilesArr: ArrayList<Tile> = ArrayList()

        var clickedTile1: Tile? = null
        var playable: Boolean = false
        var matchCount: Int = 0

        fun createTiles(context: Context)
        {
            for (i in 1..(gridSize * gridSize))
            {
                var num = i
                num %= (gridSize * gridSize) / 2

                val tile = Tile(context, num)
                tilesArr.add(tile)
            }

            //tilesArr.shuffle()
        }
    }
}
