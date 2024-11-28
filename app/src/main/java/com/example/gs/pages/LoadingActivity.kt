package com.example.gs.pages

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.gs.MainActivity
import com.example.gs.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class LoadingActivity : AppCompatActivity() {
    private lateinit var gamesList: List<HomeFragment.Game>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.loading_screen)
        fetchGamesFromFirebase()
    }

    private fun fetchGamesFromFirebase() {
        val database = FirebaseDatabase.getInstance("https://gamescout-e5aab-default-rtdb.europe-west1.firebasedatabase.app/")
        val gamesRef = database.getReference("games")

        gamesRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val tempGamesList = mutableListOf<HomeFragment.Game>()
                for (gameSnapshot in snapshot.children) {
                    val game = gameSnapshot.getValue(HomeFragment.Game::class.java)
                    game?.let { tempGamesList.add(it) }
                }

                // Pass games to MainActivity
                val intent = Intent(this@LoadingActivity, MainActivity::class.java)
                intent.putParcelableArrayListExtra("GAMES_LIST", ArrayList(tempGamesList))
                startActivity(intent)
                finish()
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error, possibly show a toast or retry
                startActivity(Intent(this@LoadingActivity, MainActivity::class.java))
                finish()
            }
        })
    }
}