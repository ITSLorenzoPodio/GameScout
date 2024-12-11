package com.example.gs.pages

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.gs.MainActivity
import com.example.gs.R
import com.google.android.material.navigation.NavigationView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class LoadingActivity : AppCompatActivity() {
    private lateinit var gamesList: List<HomeFragment.Game>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.loading_screen)

        // Get the email from the previous activity
        val userEmail = intent.getStringExtra("USER_EMAIL")
        // Verifica se l'utente è nuovo
        val isNewUser = intent.getBooleanExtra("IS_NEW_USER", false)

        // Se l'email è disponibile, procedi con il caricamento
        userEmail?.let { email ->
            fetchGamesFromFirebase(email, isNewUser)
        }
    }

    private fun fetchGamesFromFirebase(userEmail: String, isNewUser: Boolean) {
        val database = FirebaseDatabase.getInstance("https://gamescout-e5aab-default-rtdb.europe-west1.firebasedatabase.app/")
        val gamesRef = database.getReference("games")

        gamesRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val tempGamesList = mutableListOf<HomeFragment.Game>()
                for (gameSnapshot in snapshot.children) {
                    val game = gameSnapshot.getValue(HomeFragment.Game::class.java)
                    game?.let { tempGamesList.add(it) }
                }

                // Prepara i dati dell'utente
                val userName = userEmail.split("@")[0]
                    .replace(".", " ")
                    .split(" ")
                    .joinToString(" ") { it.capitalize() }

                // Decidi dove navigare in base a isNewUser
                val nextActivity = if (isNewUser) {
                    ThorActivity::class.java
                } else {
                    MainActivity::class.java
                }

                // Passa i dati all'activity successiva
                val intent = Intent(this@LoadingActivity, nextActivity).apply {
                    putParcelableArrayListExtra("GAMES_LIST", ArrayList(tempGamesList))
                    putExtra("USER_EMAIL", userEmail)
                    putExtra("USER_NAME", userName)
                }

                startActivity(intent)
                finish()
            }

            override fun onCancelled(error: DatabaseError) {
                // In caso di errore, passa comunque alla prossima activity
                val nextActivity = if (isNewUser) {
                    ThorActivity::class.java
                } else {
                    MainActivity::class.java
                }

                val intent = Intent(this@LoadingActivity, nextActivity).apply {
                    putExtra("USER_EMAIL", userEmail)
                    putExtra("USER_NAME", userEmail.split("@")[0].capitalize())
                }
                startActivity(intent)
                finish()
            }
        })
    }
}