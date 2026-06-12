package com.lasallecollegevancouver.mycoolmemorygame

import android.os.Handler
import android.os.Looper
import android.graphics.Color
import android.graphics.Typeface
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.view.setMargins
import androidx.recyclerview.widget.RecyclerView
import com.lasallecollegevancouver.mycoolmemorygame.AppData.Companion.clickedTile1

class GameAdapter (val listener: listener) : RecyclerView.Adapter<TileViewHolder> ()
{
    override fun getItemCount(): Int = AppData.tilesArr.count()

    override fun onCreateViewHolder(parent: ViewGroup, index: Int): TileViewHolder
    {
        val root = LayoutInflater.from(parent.context)
            .inflate(R.layout.tile_layout,
                parent,
                false) as FrameLayout

        return TileViewHolder(root)
    }

    override fun onBindViewHolder(viewHolder: TileViewHolder, index: Int)
    {
        // this is the TextView tile we want to show on this tile
        val tile = AppData.tilesArr[index]

        val params = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT)

        var defaultTileColor: Int = Color.WHITE
        var unmatchTileColor: Int = Color.RED
        var matchTileColor: Int = Color.GREEN

        tile.layoutParams = params
        tile.setBackgroundColor(defaultTileColor)
        params.setMargins(5)
        tile.textSize = 36f
        tile.setTypeface(null, Typeface.BOLD)
        tile.gravity = Gravity.CENTER

        tile.SetStatus(Status.FLIPPED)

        Handler(Looper.getMainLooper()).postDelayed({
            tile.SetStatus(Status.HIDDEN)
        }, 3000)

        viewHolder.root.addView(tile)

        viewHolder.itemView.setOnClickListener {

            listener.GameWon()

            if(AppData.tilesArr[index].status == Status.FLIPPED
                || AppData.tilesArr[index].status == Status.MATCHED)
                return@setOnClickListener

            if(clickedTile1 == null)
            {
                clickedTile1 = AppData.tilesArr[index]

                clickedTile1?.SetStatus(Status.FLIPPED)

                AppData.playable = true
            }
            else
            {

                if(!AppData.playable)
                {
                    return@setOnClickListener
                }

                AppData.playable = false

                var clickedTile2: Tile = AppData.tilesArr[index]

                clickedTile2.SetStatus(Status.FLIPPED)

                if(clickedTile1?.num != clickedTile2.num)
                {
                    clickedTile1?.setBackgroundColor(unmatchTileColor)
                    clickedTile2.setBackgroundColor(unmatchTileColor)
                }

                Handler(Looper.getMainLooper()).postDelayed({

                    if(clickedTile1?.num == clickedTile2.num)
                    {
                        clickedTile1?.SetStatus(Status.MATCHED)
                        clickedTile2.SetStatus(Status.MATCHED)

                        clickedTile1?.setBackgroundColor(matchTileColor)
                        clickedTile2.setBackgroundColor(matchTileColor)

                        AppData.matchCount += 1
                    }
                    else
                    {
                        clickedTile1?.SetStatus(Status.HIDDEN)
                        clickedTile2.SetStatus(Status.HIDDEN)

                        clickedTile1?.setBackgroundColor(defaultTileColor)
                        clickedTile2.setBackgroundColor(defaultTileColor)
                    }

                    clickedTile1 = null
                    AppData.playable = true
                }, 1000)
            }
        }
    }
}
