package com.example.gs.pages

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.gs.MainActivity
import com.example.gs.R
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class LoadingActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var gamesList: List<HomeFragment.Game>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.loading_screen)

        auth = FirebaseAuth.getInstance()

        // Controlla se l'utente è già autenticato
        if (auth.currentUser != null) {
            // Se l'utente è autenticato, procedi con il caricamento dei dati
            fetchGamesFromFirebase(auth.currentUser?.email ?: "")
        } else {
            // Se l'utente non è autenticato, vai alla pagina di login
            startActivity(Intent(this, AuthActivity::class.java))
            finish()
        }
    }

    private fun fetchGamesFromFirebase(userEmail: String) {
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

                // Va direttamente al MainActivity con i dati caricati
                val intent = Intent(this@LoadingActivity, MainActivity::class.java).apply {
                    putParcelableArrayListExtra("GAMES_LIST", ArrayList(tempGamesList))
                    putExtra("USER_EMAIL", userEmail)
                    putExtra("USER_NAME", userName)
                }

                startActivity(intent)
                finish()
            }

            override fun onCancelled(error: DatabaseError) {
                // In caso di errore, passa comunque al MainActivity
                val intent = Intent(this@LoadingActivity, MainActivity::class.java).apply {
                    putExtra("USER_EMAIL", userEmail)
                    putExtra("USER_NAME", userEmail.split("@")[0].capitalize())
                }
                startActivity(intent)
                finish()
            }
        })
    }
}