package com.example.gs.Pages

import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.gs.R
import com.example.gs.SwipeableCardView
import kotlinx.parcelize.Parcelize

class HomeFragment : Fragment() {
    private lateinit var cardContainer: FrameLayout
    private lateinit var likeButton: Button
    private lateinit var skipButton: Button
    private var currentIndex = 0
    private val games = mutableListOf<Game>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cardContainer = view.findViewById(R.id.card_container)
        likeButton = view.findViewById(R.id.likeButton)
        skipButton = view.findViewById(R.id.skipButton)

        setupButtons()

        // Retrieve games from arguments
        val passedGames = arguments?.getParcelableArrayList<Game>("GAMES_LIST")
        if (passedGames != null) {
            games.addAll(passedGames)
            if (games.isNotEmpty()) {
                addNextCard()
            }
        }
    }

    private fun setupButtons() {
        likeButton.setOnClickListener {
            val topCard = cardContainer.getChildAt(cardContainer.childCount - 1) as? SwipeableCardView
            topCard?.swipeRight()
        }

        skipButton.setOnClickListener {
            val topCard = cardContainer.getChildAt(cardContainer.childCount - 1) as? SwipeableCardView
            topCard?.swipeLeft()
        }
    }

    private fun addNextCard() {
        if (currentIndex >= games.size) {
            return
        }

        // Only add a new card if we have less than 2 cards
        if (cardContainer.childCount >= 2) {
            return
        }

        val game = games[currentIndex]
        val cardView = LayoutInflater.from(requireContext())
            .inflate(R.layout.game_card_item, cardContainer, false) as SwipeableCardView

        Glide.with(requireContext())
            .load(game.imageUrl)
            .into(cardView.findViewById<ImageView>(R.id.gameImage))

        cardView.findViewById<TextView>(R.id.gameTitle).text = game.title
        cardView.findViewById<TextView>(R.id.gameGenre).text = game.genre
        cardView.findViewById<TextView>(R.id.gamePlatforms).text = game.platforms
        cardView.findViewById<TextView>(R.id.gameScore).text = game.userScore

        cardView.setOnSwipeListener(object : SwipeableCardView.OnSwipeListener {
            override fun onSwipeLeft() {
                println("Game skipped: ${game.title}")
                removeTopCard()
            }

            override fun onSwipeRight() {
                println("Game liked: ${game.title}")
                removeTopCard()
            }
        })

        cardContainer.addView(cardView, 0)
        currentIndex++

        // Add the next card if we still have room for one more
        if (cardContainer.childCount < 2 && currentIndex < games.size) {
            addNextCard()
        }
    }

    private fun removeTopCard() {
        if (cardContainer.childCount > 0) {
            cardContainer.removeViewAt(cardContainer.childCount - 1)
            // Add next card only if we have less than 2 cards now
            if (cardContainer.childCount < 2) {
                addNextCard()
            }
        }
    }

    @Parcelize
    data class Game(
        val id: Int = 0,
        val title: String = "",
        val imageUrl: String = "",
        val genre: String = "",
        val platforms: String = "",
        val userScore: String = "",
        val description: String = "",
        val price: Double = 0.0,
        val rating: Double = 0.0
    ) : Parcelable
}