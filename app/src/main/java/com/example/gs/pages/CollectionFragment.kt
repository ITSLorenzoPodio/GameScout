package com.example.gs.pages

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
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
    private lateinit var showMoreLikedButton: Button
    private lateinit var showMoreSkippedButton: Button

    private val likedGames = mutableListOf<HomeFragment.Game>()
    private val skippedGames = mutableListOf<HomeFragment.Game>()

    private var isShowingAllLiked = false
    private var isShowingAllSkipped = false

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
        showMoreLikedButton = view.findViewById(R.id.showMoreLikedButton)
        showMoreSkippedButton = view.findViewById(R.id.showMoreSkippedButton)

        setupClickListeners()
        loadSavedGames()
    }

    private fun setupClickListeners() {
        showMoreLikedButton.setOnClickListener {
            isShowingAllLiked = !isShowingAllLiked
            updateLikedGamesDisplay()
            showMoreLikedButton.text = if (isShowingAllLiked) "Show less" else "Show more"
        }

        showMoreSkippedButton.setOnClickListener {
            isShowingAllSkipped = !isShowingAllSkipped
            updateSkippedGamesDisplay()
            showMoreSkippedButton.text = if (isShowingAllSkipped) "Show less" else "Show more"
        }
    }

    fun addGame(game: HomeFragment.Game, isLiked: Boolean) {
        if (isLiked) {
            if (!likedGames.contains(game)) {
                likedGames.add(game)
                updateLikedGamesDisplay()
            }
        } else {
            if (!skippedGames.contains(game)) {
                skippedGames.add(game)
                updateSkippedGamesDisplay()
            }
        }
    }

    private fun addGameView(game: HomeFragment.Game, container: LinearLayout) {
        val gameView = LayoutInflater.from(context)
            .inflate(R.layout.collection_game_item, container, false)

        gameView.findViewById<TextView>(R.id.gameTitle).text = game.title
        gameView.findViewById<TextView>(R.id.gameGenre).text = game.genre
        gameView.findViewById<TextView>(R.id.gamePlatforms).text = game.platforms
        gameView.findViewById<TextView>(R.id.gameScore).text =
            getString(R.string.score) + game.userScore
        gameView.findViewById<TextView>(R.id.gamePrice).text =
            game.currentPrice.toString() + getString(R.string.price)

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
            }
        }

        container.addView(gameView)
    }

    private fun updateLikedGamesDisplay() {
        likedGamesContainer.removeAllViews()
        val gamesToShow = if (isShowingAllLiked) likedGames else likedGames.take(3)
        gamesToShow.forEach { game ->
            addGameView(game, likedGamesContainer)
        }

        showMoreLikedButton.visibility =
            if (likedGames.size > 3) View.VISIBLE else View.GONE
    }

    private fun updateSkippedGamesDisplay() {
        skippedGamesContainer.removeAllViews()
        val gamesToShow = if (isShowingAllSkipped) skippedGames else skippedGames.take(3)
        gamesToShow.forEach { game ->
            addGameView(game, skippedGamesContainer)
        }

        showMoreSkippedButton.visibility =
            if (skippedGames.size > 3) View.VISIBLE else View.GONE
    }

    private fun loadSavedGames() {
        likedGamesContainer.removeAllViews()
        skippedGamesContainer.removeAllViews()

        updateLikedGamesDisplay()
        updateSkippedGamesDisplay()
    }

    companion object {
        fun newInstance() = CollectionFragment()
    }
}