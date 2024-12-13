package com.example.gs.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.gs.R
import com.example.gs.pages.HomeFragment
import com.google.firebase.database.core.view.View

class SearchResultsAdapter(private var games: List<HomeFragment.Game>) :
    RecyclerView.Adapter<SearchResultsAdapter.GameViewHolder>() {

    class GameViewHolder(itemView: android.view.View) : RecyclerView.ViewHolder(itemView) {
        val gameImage: ImageView = itemView.findViewById(R.id.gameImage)
        val gameTitle: TextView = itemView.findViewById(R.id.gameTitle)
        val gameGenre: TextView = itemView.findViewById(R.id.gameGenre)
        val gamePlatforms: TextView = itemView.findViewById(R.id.gamePlatforms)
        val gameScore: TextView = itemView.findViewById(R.id.gameScore)
        val gamePrice: TextView = itemView.findViewById(R.id.gamePrice)

        fun bind(game: HomeFragment.Game) {
            gameTitle.text = game.title
            gameGenre.text = game.genre
            gamePlatforms.text = game.platforms
            gameScore.text = "Score: ${game.userScore}"
            gamePrice.text = "$${game.currentPrice}"

            // Carica l'immagine con Glide o altra libreria di caricamento immagini
            Glide.with(itemView.context)
                .load(game.imageUrl)
                .into(gameImage)
        }
    }

    fun updateGames(newGames: List<HomeFragment.Game>) {
        games = newGames
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.collection_game_item, parent, false)
        return GameViewHolder(view)
    }

    override fun onBindViewHolder(holder: GameViewHolder, position: Int) {
        holder.bind(games[position])
    }

    override fun getItemCount() = games.size
}