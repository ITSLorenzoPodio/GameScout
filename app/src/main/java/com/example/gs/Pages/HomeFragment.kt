package com.example.gs.Pages

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.gs.R
import com.example.gs.SwipeableCardView

class HomeFragment : Fragment() {
    private lateinit var cardContainer: FrameLayout
    private val games = listOf(
        // Lista originale
        Game("Dark Souls", R.drawable.game1, "Action RPG", "PS3, Xbox 360, PC", "User score: 89%"),
        Game("Halo Reach", R.drawable.game2, "First-person shooter", "Xbox 360, Xbox One, PC", "User score: 92%"),
        Game("Doom", R.drawable.game3, "First-person shooter", "PC, PS4, Xbox One, Switch", "User score: 85%"),
        Game("The Witcher 3", R.drawable.game4, "Action RPG", "PS4, PS5, Xbox One, Xbox Series X/S, PC, Switch", "User score: 93%"),
        Game("God of War", R.drawable.game5, "Action-Adventure", "PS4, PS5, PC", "User score: 94%"),
        Game("Elden Ring", R.drawable.game6, "Action RPG", "PS4, PS5, Xbox One, Xbox Series X/S, PC", "User score: 96%"),
        Game("Red Dead Redemption 2", R.drawable.game7, "Action-Adventure", "PS4, Xbox One, PC", "User score: 97%"),
        Game("Super Mario Odyssey", R.drawable.game8, "Platform", "Nintendo Switch", "User score: 97%"),
        Game("The Last of Us Part II", R.drawable.game9, "Action-Adventure", "PS4, PS5", "User score: 93%"),
        Game("Persona 5 Royal", R.drawable.game10, "JRPG", "PS4, PS5, Xbox Series X/S, Switch, PC", "User score: 95%"),
        Game("Cyberpunk 2077", R.drawable.game11, "Action RPG", "PS4, PS5, Xbox One, Xbox Series X/S, PC", "User score: 86%"),
        Game("Hades", R.drawable.game12, "Roguelike Action", "PS4, PS5, Xbox One, Xbox Series X/S, Switch, PC", "User score: 93%"),
        Game("Ghost of Tsushima", R.drawable.game13, "Action-Adventure", "PS4, PS5", "User score: 92%"),
        Game("Monster Hunter: World", R.drawable.game14, "Action RPG", "PS4, Xbox One, PC", "User score: 90%"),
        Game("Final Fantasy VII Remake", R.drawable.game15, "Action RPG", "PS4, PS5, PC", "User score: 87%"),
        Game("Resident Evil Village", R.drawable.game16, "Survival Horror", "PS4, PS5, Xbox One, Xbox Series X/S, PC", "User score: 84%"),
        Game("Horizon Forbidden West", R.drawable.game17, "Action RPG", "PS4, PS5", "User score: 88%"),
        Game("Spider-Man: Miles Morales", R.drawable.game18, "Action-Adventure", "PS4, PS5, PC", "User score: 85%"),
        Game("Sekiro: Shadows Die Twice", R.drawable.game19, "Action-Adventure", "PS4, Xbox One, PC", "User score: 91%"),
        Game("Death Stranding", R.drawable.game20, "Action", "PS4, PS5, PC", "User score: 82%"),
        Game("It Takes Two", R.drawable.game21, "Co-op Adventure", "PS4, PS5, Xbox One, Xbox Series X/S, PC", "User score: 89%"),
        Game("Deathloop", R.drawable.game22, "First-person shooter", "PS5, PC", "User score: 88%"),
        Game("Hollow Knight", R.drawable.game23, "Metroidvania", "PS4, Xbox One, Switch, PC", "User score: 90%")
    )
    private var currentIndex = 0

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
        addNextCard()
    }

    private fun addNextCard() {
        if (currentIndex >= games.size) {
            // Reset or end the game
            return
        }

        val game = games[currentIndex]
        val cardView = LayoutInflater.from(requireContext())
            .inflate(R.layout.game_card_item, cardContainer, false) as SwipeableCardView

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

    data class Game(
        val title: String,
        val imageResId: Int,
        val genre: String,
        val platforms: String,
        val score: String
    )
}