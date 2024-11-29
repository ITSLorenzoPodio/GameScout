package com.example.gs.pages

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.gs.GameCollectionListener
import com.example.gs.MainActivity
import com.example.gs.R
import com.example.gs.SwipeableCardView
import kotlinx.parcelize.Parcelize

interface CategorySelectionListener {
    fun onCategorySelected(category: String?)  // nullable to allow clearing the filter
}

class HomeFragment : Fragment(), CategorySelectionListener {
    private lateinit var cardContainer: FrameLayout
    private lateinit var likeButton: Button
    private lateinit var skipButton: Button
    private var currentIndex = 0
    private val allGames = mutableListOf<Game>()
    private val filteredGames = mutableListOf<Game>()
    private var gameCollectionListener: GameCollectionListener? = null
    private var currentCategory: String? = null
    private var selectedCategories: List<String> = emptyList()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is GameCollectionListener) {
            gameCollectionListener = context
        }
    }

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
            allGames.addAll(passedGames)
            filteredGames.addAll(passedGames)
            if (filteredGames.isNotEmpty()) {
                addNextCard()
            }
        }
    }

    override fun onCategorySelected(category: String?) {
        println("HomeFragment received category: $category")
        println("Total games before filtering: ${allGames.size}")

        currentCategory = category
        cardContainer.removeAllViews()
        currentIndex = 0

        filteredGames.clear()
        if (category == null) {
            filteredGames.addAll(allGames)
        } else {
            filteredGames.addAll(allGames.filter { game ->
                // Split genres and trim whitespace
                val gameGenres = game.genre.split(",").map { it.trim() }
                // Check if any of the game's genres match the selected category
                gameGenres.any { it.equals(category, ignoreCase = true) }
            })
        }

        println("Filtered games count: ${filteredGames.size}")

        if (filteredGames.isNotEmpty()) {
            addNextCard()
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

    fun setSelectedCategories(categories: List<String>) {
        selectedCategories = categories
        filterGames()
    }

    private fun filterGames() {
        filteredGames.clear()

        if (selectedCategories.isEmpty()) {
            filteredGames.addAll(allGames)
        } else {
            filteredGames.addAll(allGames.filter { game ->
                val gameGenres = game.genre.split(",").map { it.trim() }
                // Check if any of the game's genres match any of the selected categories
                selectedCategories.any { category ->
                    gameGenres.any { it.equals(category, ignoreCase = true) }
                }
            })
        }

        // Reset and refresh the card stack
        cardContainer.removeAllViews()
        currentIndex = 0
        if (filteredGames.isNotEmpty()) {
            addNextCard()
        }
    }

    fun onSearchSubmitted(searchQuery: String) {
        println("HomeFragment received search query: $searchQuery")

        cardContainer.removeAllViews()
        currentIndex = 0

        filteredGames.clear()
        if (searchQuery.isBlank()) {
            filteredGames.addAll(allGames)
        } else {
            filteredGames.addAll(allGames.filter { game ->
                game.title.contains(searchQuery, ignoreCase = true)
            })
        }

        println("Filtered games count: ${filteredGames.size}")

        if (filteredGames.isNotEmpty()) {
            addNextCard()
        }
    }

    private fun addNextCard() {
        if (currentIndex >= filteredGames.size) {
            return
        }

        if (cardContainer.childCount >= 3) {
            return
        }

        val game = filteredGames[currentIndex]
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
                gameCollectionListener?.onGameSaved(game, false)
                removeTopCard()
            }

            override fun onSwipeRight() {
                println("Game liked: ${game.title}")
                gameCollectionListener?.onGameSaved(game, true)
                removeTopCard()
            }
        })

        cardContainer.addView(cardView, 0)
        currentIndex++

        if (cardContainer.childCount < 2 && currentIndex < filteredGames.size) {
            addNextCard()
        }
    }

    private fun removeTopCard() {
        if (cardContainer.childCount > 0) {
            cardContainer.removeViewAt(cardContainer.childCount - 1)
            if (cardContainer.childCount < 2) {
                addNextCard()
            }
        }
    }

    override fun onDetach() {
        super.onDetach()
        gameCollectionListener = null
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