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

class GameAdapter(
    private val games: List<Game>,
    private val isRecommendation: Boolean = false,
    private val onSelectionChanged: (Game) -> Unit = {}
) :
    RecyclerView.Adapter<GameAdapter.GameViewHolder>() {

    class GameViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.gameTitle)
        val image: ImageView = view.findViewById(R.id.gameImage)
        val overlay: ImageView = view.findViewById(R.id.gameOverlay)
        val typeBadge: TextView = view.findViewById(R.id.recommendationType)
        val reviews: TextView = view.findViewById(R.id.gameReviews)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_game, parent, false)
        return GameViewHolder(view)
    }

    override fun onBindViewHolder(holder: GameViewHolder, position: Int) {
        val game = games[position]
        holder.title.text = game.name

        // Update Overlay based on state
        if (!isRecommendation) {
            updateOverlay(holder, game)
        } else {
            holder.overlay.visibility = View.GONE
        }

        holder.itemView.setOnClickListener {
            if (isRecommendation) {
                game.appid?.let { id ->
                    val url = "https://store.steampowered.com/app/$id"
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    holder.itemView.context.startActivity(intent)
                }
            } else {
                // Cycle state: 0 -> 1 -> 2 -> 0
                game.selectionState = (game.selectionState + 1) % 3
                updateOverlay(holder, game)
                onSelectionChanged(game)
            }
        }
        
        if (game.recommendationType != null) {
            holder.typeBadge.visibility = View.VISIBLE
            holder.typeBadge.text = game.recommendationType
            
            if (game.reviewCount > 0) {
                holder.reviews.visibility = View.VISIBLE
                holder.reviews.text = "${game.reviewScore}% (${game.reviewCount / 1000}k reviews)"
            } else {
                holder.reviews.visibility = View.GONE
            }
        } else {
            holder.typeBadge.visibility = View.GONE
            holder.reviews.visibility = View.GONE
        }
        
        if (game.appid != null && game.appid != 0) {
            val library2x = "https://cdn.akamai.steamstatic.com/steam/apps/${game.appid}/library_600x900_2x.jpg"
            val library1x = "https://cdn.akamai.steamstatic.com/steam/apps/${game.appid}/library_600x900.jpg"
            val capsuleVertical = "https://cdn.akamai.steamstatic.com/steam/apps/${game.appid}/capsule_231x350.jpg"

            holder.image.load(library2x) {
                crossfade(500)
                placeholder(R.drawable.image_placeholder)
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

    private fun updateOverlay(holder: GameViewHolder, game: Game) {
        when (game.selectionState) {
            1 -> {
                holder.overlay.visibility = View.VISIBLE
                holder.overlay.setImageResource(R.drawable.plus_game_overlay)
            }
            2 -> {
                holder.overlay.visibility = View.VISIBLE
                holder.overlay.setImageResource(R.drawable.minus_game_overlay)
            }
            else -> {
                holder.overlay.visibility = View.GONE
            }
        }
    }

    override fun getItemCount() = games.size
}
