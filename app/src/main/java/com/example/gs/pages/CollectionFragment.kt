package com.example.gs.pages

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.gs.R

class CollectionFragment : Fragment() {
    private lateinit var likedGamesContainer: LinearLayout
    private lateinit var skippedGamesContainer: LinearLayout

    private val likedGames = mutableListOf<HomeFragment.Game>()
    private val skippedGames = mutableListOf<HomeFragment.Game>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_collection, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        likedGamesContainer = view.findViewById(R.id.likedGamesContainer)
        skippedGamesContainer = view.findViewById(R.id.skippedGamesContainer)

        // Load saved games if any
        loadSavedGames()
    }

    fun addGame(game: HomeFragment.Game, isLiked: Boolean) {
        if (isLiked) {
            if (!likedGames.contains(game)) {
                likedGames.add(game)
                addGameView(game, likedGamesContainer)
            }
        } else {
            if (!skippedGames.contains(game)) {
                skippedGames.add(game)
                addGameView(game, skippedGamesContainer)
            }
        }
        // Here you could also save the games to persistent storage
    }

    private fun addGameView(game: HomeFragment.Game, container: LinearLayout) {
        val gameView = LayoutInflater.from(context)
            .inflate(R.layout.collection_game_item, container, false)

        // Set game information
        gameView.findViewById<TextView>(R.id.gameTitle).text = game.title
        gameView.findViewById<TextView>(R.id.gameGenre).text = game.genre
        gameView.findViewById<TextView>(R.id.gamePlatforms).text = game.platforms
        gameView.findViewById<TextView>(R.id.gameScore).text = getString(R.string.score) + game.userScore
        gameView.findViewById<TextView>(R.id.gamePrice).text = game.currentPrice.toString() + getString(R.string.price)

        // Load game image
        Glide.with(requireContext())
            .load(game.imageUrl)
            .into(gameView.findViewById(R.id.gameImage))

        gameView.setOnClickListener {
            if (game.url.isNotEmpty()) {
                try {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(game.url))
                    startActivity(intent)
                } catch (e: Exception) {
                    Toast.makeText(context, "Could not open URL: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "No URL available", Toast.LENGTH_SHORT).show()
            }
        }

        // Add the view to the container
        container.addView(gameView)
    }

    private fun loadSavedGames() {
        // Here you would implement loading games from persistent storage
        // For now, we'll just clear and rebuild the views
        likedGamesContainer.removeAllViews()
        skippedGamesContainer.removeAllViews()

        likedGames.forEach { game ->
            addGameView(game, likedGamesContainer)
        }

        skippedGames.forEach { game ->
            addGameView(game, skippedGamesContainer)
        }
    }



    companion object {
        fun newInstance() = CollectionFragment()
    }
}