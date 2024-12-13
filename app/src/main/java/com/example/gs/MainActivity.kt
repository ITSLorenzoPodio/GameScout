package com.example.gs

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.viewpager2.widget.ViewPager2
import com.example.gs.adapters.PagesAdapter
import com.example.gs.pages.HomeFragment
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.example.gs.SettingsActivity
import com.example.gs.pages.AboutActivity
import com.example.gs.pages.AuthActivity
import com.example.gs.pages.ProfileActivity
import com.google.firebase.auth.FirebaseAuth

interface GameCollectionListener {
    fun onGameSaved(game: HomeFragment.Game, isLiked: Boolean)
}

class MainActivity : AppCompatActivity(), GameCollectionListener, NavigationView.OnNavigationItemSelectedListener {
    lateinit var viewPager: ViewPager2
    private lateinit var pagesAdapter: PagesAdapter
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView

    // Perché val gamesList = intent.getParcelableArrayListExtra<HomeFragment.Game>("GAMES_LIST") è deprecato
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Menu a sinistra
        drawerLayout = findViewById(R.id.drawerLayout)
        navigationView = findViewById(R.id.navigationView)
        navigationView.setNavigationItemSelectedListener(this)

        // Quando clicchi il tasto menu in alto a sinistra si apre il menu laterale
        findViewById<ImageButton>(R.id.menuButton).setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        // Aggiorna l'header nel menu laterale con i dati dell'utente ("Immagine", Nome e Email)
        updateNavigationHeader()

        // val gamesList = intent.getParcelableArrayListExtra<HomeFragment.Game>("GAMES_LIST") deprecato
        // Prendo la lista di giochi dall'intent (LoadingActivity)
        val gamesList = intent.getParcelableArrayListExtra("GAMES_LIST", HomeFragment.Game::class.java)

        // ViewPager per le pagine side by side
        viewPager = findViewById(R.id.viewPager)
        // Non permettiamo lo swipe tra le pagine
        viewPager.isUserInputEnabled = false
        // Passiamo la lista al pages adapter
        // I dati devono essere passati al Fragment prima che venga creato e mostrato
        // Usando il Bundle, i dati sopravvivono alle ricreazioni del Fragment
        pagesAdapter = PagesAdapter(this, gamesList)
        viewPager.adapter = pagesAdapter

        // TabLayout (La hotbar) per navigare tra le pagine
        val tabLayout = findViewById<TabLayout>(R.id.tabsLayout)

        // Imposta il TabLayout con il ViewPager per navigare tra le pagine
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            when (position) {
                0 -> tab.setIcon(R.drawable.home_tab)
                1 -> tab.setIcon(R.drawable.search_tab)
                2 -> tab.setIcon(R.drawable.library_tab)
            }
        }.attach()

        // Imposta colori per le icone del TabLayout
        tabLayout.setTabIconTint(ContextCompat.getColorStateList(this, R.color.tab_icon_color))
    }

    // Aggiorna l'header nel menu laterale con i dati dell'utente ("Immagine", Nome e Email)
    private fun updateNavigationHeader() {
        val headerView = navigationView.getHeaderView(0)

        // Recupera i dati dell'utente dall'intent
        val userEmail = intent.getStringExtra("USER_EMAIL")
        val userName = intent.getStringExtra("USER_NAME")

        // Imposta i dati nell'header del menu laterale
        headerView?.let {
            val userNameTextView: TextView = it.findViewById(R.id.userName)
            val userEmailTextView: TextView = it.findViewById(R.id.userEmail)
            val initialTextView: TextView = it.findViewById(R.id.initialLetter)

            // let è una funzione che viene eseguita solo se userName non è nullo
            // Imposta il nome dell'utente
            userName?.let { name ->
                userNameTextView.text = name
                // Imposta l'iniziale maiuscola del nome utente
                initialTextView.text = name.firstOrNull()?.uppercase() ?: "U"
            }
            // Imposta l'email dell'utente
            userEmail?.let { email -> userEmailTextView.text = email }
        }
    }

    // Quando clicchiamo su un'opzione del menu laterale si apre una nuova activity
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_profile -> {
                val intent = Intent(this, ProfileActivity::class.java)
                // Passiamo il nome utente dall'intent originale al ProfileActivity
                intent.putExtra("USER_NAME", getIntent().getStringExtra("USER_NAME"))
                startActivity(intent)
            }
            R.id.nav_settings -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_help -> {
                val intent = Intent(this, AboutActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_logout -> {
                // Esci dall'account Firebase
                FirebaseAuth.getInstance().signOut()

                // Crea un intent per tornare alla schermata di login
                val intent = Intent(this, AuthActivity::class.java)

                // Aggiungi flag per pulire lo stack delle activity
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or
                        Intent.FLAG_ACTIVITY_CLEAR_TASK or
                        Intent.FLAG_ACTIVITY_NEW_TASK)

                // Avvia l'activity di login
                startActivity(intent)

                // Chiudi l'activity corrente
                finish()

                // (Opzionale) Mostra un toast per confermare il logout
                Toast.makeText(this, "Disconnesso con successo", Toast.LENGTH_SHORT).show()
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    // Quando premiamo indietro si chiude il menu laterale
    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressedDispatcher.onBackPressed()
        }
    }

    //
    fun onCategoriesSelected(categories: List<String>) {
        val homeFragment = supportFragmentManager.fragments
            .filterIsInstance<HomeFragment>()
            .firstOrNull()

        homeFragment?.setSelectedCategories(categories)
    }

    //
    fun onSearchSubmitted(searchQuery: String) {
        val currentFragment = supportFragmentManager.findFragmentByTag("f0")
        if (currentFragment is HomeFragment) {
            currentFragment.onSearchSubmitted(searchQuery)
        }
        viewPager.currentItem = 0
    }

    // Quando aggiungiamo un gioco alla collezione
    override fun onGameSaved(game: HomeFragment.Game, isLiked: Boolean) {
        try {
            pagesAdapter.getCollectionFragment().addGame(game, isLiked)
        } catch (e: Exception) {
            println("Error saving game: ${e.message}")
        }
    }
}