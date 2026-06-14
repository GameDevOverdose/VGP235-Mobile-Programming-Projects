package com.lasallecollegevancouver.myfinalapp

import android.content.Intent
import android.net.Uri
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
        val typeBadge: TextView = view.findViewById(R.id.recommendationType)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_game, parent, false)
        return GameViewHolder(view)
    }

    override fun onBindViewHolder(holder: GameViewHolder, position: Int) {
        val game = games[position]
        holder.title.text = game.name

        holder.itemView.setOnClickListener {
            game.appid?.let { id ->
                val url = "https://store.steampowered.com/app/$id"
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                holder.itemView.context.startActivity(intent)
            }
        }
        
        if (game.recommendationType != null) {
            holder.typeBadge.visibility = View.VISIBLE
            holder.typeBadge.text = game.recommendationType
        } else {
            holder.typeBadge.visibility = View.GONE
        }
        
        if (game.appid != null && game.appid != 0) {
            val library2x = "https://cdn.akamai.steamstatic.com/steam/apps/${game.appid}/library_600x900_2x.jpg"
            val library1x = "https://cdn.akamai.steamstatic.com/steam/apps/${game.appid}/library_600x900.jpg"
            val capsuleVertical = "https://cdn.akamai.steamstatic.com/steam/apps/${game.appid}/capsule_231x350.jpg"

            holder.image.load(library2x) {
                crossfade(true)
                placeholder(android.R.drawable.progress_indeterminate_horizontal)
                error(R.drawable.missing_game_poster)
                
                listener(onError = { _, _ ->
                    holder.image.load(library1x) {
                        listener(onError = { _, _ ->
                            holder.image.load(capsuleVertical) {
                                error(R.drawable.missing_game_poster)
                            }
                        })
                    }
                })
            }
        } else if (game.imageRes != 0) {
            holder.image.setImageResource(game.imageRes)
        }
    }

    override fun getItemCount() = games.size
}
