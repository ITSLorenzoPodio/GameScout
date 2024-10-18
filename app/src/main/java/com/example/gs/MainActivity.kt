package com.example.gs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

class MainActivity : AppCompatActivity() {

    private lateinit var cardContainer: FrameLayout
    private val games = listOf(
        Game("Dark Souls", R.drawable.game1, "Action RPG", "PS3, Xbox 360, PC", "User score: 89%"),
        Game("Halo Reach", R.drawable.game2, "First-person shooter", "Xbox 360, Xbox One, PC", "User score: 92%"),
        Game("Doom", R.drawable.game3, "First-person shooter", "PC, PS4, Xbox One, Switch", "User score: 85%")
    )
    private var currentIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Hide system bars
        hideSystemBars()

        cardContainer = findViewById(R.id.card_container)
        addNextCard()
    }

    private fun hideSystemBars() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, window.decorView).let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }

    private fun addNextCard() {
        if (currentIndex >= games.size) {
            // Reset or end the game
            return
        }

        val game = games[currentIndex]
        val cardView = LayoutInflater.from(this).inflate(R.layout.game_card_item, cardContainer, false) as SwipeableCardView

        cardView.findViewById<ImageView>(R.id.gameImage).setImageResource(game.imageResId)
        cardView.findViewById<TextView>(R.id.gameTitle).text = game.title
        cardView.findViewById<TextView>(R.id.gameGenre).text = game.genre
        cardView.findViewById<TextView>(R.id.gamePlatforms).text = game.platforms
        cardView.findViewById<TextView>(R.id.gameScore).text = game.score

        cardView.setOnSwipeListener(object : SwipeableCardView.OnSwipeListener {
            override fun onSwipeLeft() {
                removeTopCard()
            }

            override fun onSwipeRight() {
                removeTopCard()
            }
        })

        cardContainer.addView(cardView, 0)
        currentIndex++

        // Add the next card if there's room
        if (cardContainer.childCount < 3 && currentIndex < games.size) {
            addNextCard()
        }
    }

    private fun removeTopCard() {
        cardContainer.removeViewAt(cardContainer.childCount - 1)
        addNextCard()
    }
}

data class Game(val title: String, val imageResId: Int, val genre: String, val platforms: String, val score: String)