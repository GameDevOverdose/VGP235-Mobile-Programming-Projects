package com.lasallecollegevancouver.myfinalapp

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class CategoryAdapter(
    private val categories: List<Category>,
    var topGenres: List<String> = emptyList(),
    var dnaSubtitle: String = "Based on your most played titles",
    private val isRecommendation: Boolean = false,
    private val useGrid: Boolean = isRecommendation,
    private val onGameSelectionChanged: (Game) -> Unit = {},
    private val onClearFilters: () -> Unit = {}
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_CATEGORY = 0
        private const val TYPE_GENRE_STATS = 1
    }

    class CategoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.categoryTitle)
        val recycler: RecyclerView = view.findViewById(R.id.horizontalRecycler)
    }

    class GenreStatsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val genreText: TextView = view.findViewById(R.id.genreText_id)
        val subtitle: TextView = view.findViewById(R.id.dnaSubtitle_id)
        val clearFiltersBtn: TextView = view.findViewById(R.id.clearFiltersBtn_id)
    }

    override fun getItemViewType(position: Int): Int {
        return if (position < categories.size) TYPE_CATEGORY else TYPE_GENRE_STATS
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_CATEGORY) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.games_layout, parent, false)
            CategoryViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_genre_stats, parent, false)
            GenreStatsViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is CategoryViewHolder) {
            val category = categories[position]
            holder.title.text = if (isRecommendation) "${category.title} Picks" else category.title
            holder.title.setTextColor(Color.WHITE)
            
            if (useGrid) {
                holder.recycler.layoutManager = GridLayoutManager(holder.itemView.context, 3)
            } else {
                holder.recycler.layoutManager =
                    LinearLayoutManager(holder.itemView.context, LinearLayoutManager.HORIZONTAL, false)
            }
            holder.recycler.adapter = GameAdapter(
                category.games,
                isRecommendation = isRecommendation,
                useGrid = useGrid,
                onSelectionChanged = onGameSelectionChanged
            )
        } else if (holder is GenreStatsViewHolder) {
            val text = topGenres.mapIndexed { index, genre -> "${index + 1}. $genre" }
                .joinToString("\n")
            holder.genreText.text = text
            holder.subtitle.text = dnaSubtitle

            val hasSelections = categories.any { cat -> cat.games.any { it.selectionState != 0 } }
            holder.clearFiltersBtn.visibility = if (hasSelections) View.VISIBLE else View.GONE
            holder.clearFiltersBtn.setOnClickListener {
                onClearFilters()
            }
        }
    }


    override fun getItemCount() = if (topGenres.isNotEmpty()) categories.size + 1 else categories.size
}
