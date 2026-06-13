package com.lasallecollegevancouver.myfinalapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

import coil.load

class GameAdapter(private val games: List<Game>) :
    RecyclerView.Adapter<GameAdapter.GameViewHolder>() {

    class GameViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.gameTitle)
        val image: ImageView = view.findViewById(R.id.gameImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_game, parent, false)
        return GameViewHolder(view)
    }

    override fun onBindViewHolder(holder: GameViewHolder, position: Int) {
        val game = games[position]
        holder.title.text = game.name
        
        if (game.appid != null && game.appid != 0) {
            val imageUrl = "https://cdn.akamai.steamstatic.com/steam/apps/${game.appid}/library_600x900_2x.jpg"
            holder.image.load(imageUrl) {
                crossfade(true)
                placeholder(android.R.drawable.progress_indeterminate_horizontal)
                error(android.R.drawable.stat_notify_error)
            }
        } else if (game.imageRes != 0) {
            holder.image.setImageResource(game.imageRes)
        }
    }

    override fun getItemCount() = games.size
}