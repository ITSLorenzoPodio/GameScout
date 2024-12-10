package com.example.gs

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageButton
import android.widget.Toast
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
import com.google.firebase.auth.FirebaseAuth

interface GameCollectionListener {
    fun onGameSaved(game: HomeFragment.Game, isLiked: Boolean)
}

class MainActivity : AppCompatActivity(), GameCollectionListener, NavigationView.OnNavigationItemSelectedListener {
    lateinit var viewPager: ViewPager2
    private lateinit var pagesAdapter: PagesAdapter
    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize DrawerLayout
        drawerLayout = findViewById(R.id.drawerLayout)
        val navigationView = findViewById<NavigationView>(R.id.navigationView)
        navigationView.setNavigationItemSelectedListener(this)

        // Setup toolbar clicks
        findViewById<ImageButton>(R.id.profileButton).setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }


        // Retrieve games list from intent
        val gamesList = intent.getParcelableArrayListExtra<HomeFragment.Game>("GAMES_LIST")

        // ViewPager
        viewPager = findViewById(R.id.viewPager)
        viewPager.isUserInputEnabled = false
        pagesAdapter = PagesAdapter(this, gamesList)
        viewPager.adapter = pagesAdapter

        // TabLayout
        val tabLayout = findViewById<TabLayout>(R.id.tabsLayout)

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            when (position) {
                0 -> tab.setIcon(R.drawable.home_tab)
                1 -> tab.setIcon(R.drawable.search_tab)
                2 -> tab.setIcon(R.drawable.library_tab)
            }
        }.attach()

        tabLayout.setTabIconTint(ContextCompat.getColorStateList(this, R.color.tab_icon_color))
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_profile -> {
                // TODO
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

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
    fun onCategoriesSelected(categories: List<String>) {
        val homeFragment = supportFragmentManager.fragments
            .filterIsInstance<HomeFragment>()
            .firstOrNull()

        homeFragment?.setSelectedCategories(categories)
    }

    fun onSearchSubmitted(searchQuery: String) {
        val currentFragment = supportFragmentManager.findFragmentByTag("f0")
        if (currentFragment is HomeFragment) {
            currentFragment.onSearchSubmitted(searchQuery)
        }
        viewPager.currentItem = 0
    }

    override fun onGameSaved(game: HomeFragment.Game, isLiked: Boolean) {
        try {
            pagesAdapter.getCollectionFragment().addGame(game, isLiked)
        } catch (e: Exception) {
            println("Error saving game: ${e.message}")
            e.printStackTrace()
        }
    }
}