package com.example.gs.ui.activities

import android.os.Build
import android.os.Bundle
import android.widget.ImageButton
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.viewpager2.widget.ViewPager2
import com.example.gs.R
import com.example.gs.managers.NavigationManager
import com.example.gs.managers.UserProfileManager
import com.example.gs.managers.ViewPagerSetup
import com.example.gs.model.Game
import com.example.gs.ui.fragments.HomeFragment
import com.google.android.material.navigation.NavigationView

interface GameCollectionListener {
    fun onGameSaved(game: Game, isLiked: Boolean)
}

class MainActivity : AppCompatActivity(), GameCollectionListener {
    private lateinit var viewPager: ViewPager2
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var navigationManager: NavigationManager
    private lateinit var userProfileManager: UserProfileManager
    private lateinit var viewPagerSetup: ViewPagerSetup

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initializeViews()
        setupManagers()
        setupUI()
    }

    private fun initializeViews() {
        drawerLayout = findViewById(R.id.drawerLayout)
        navigationView = findViewById(R.id.navigationView)
        viewPager = findViewById(R.id.viewPager)
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun setupManagers() {
        navigationManager = NavigationManager(this, drawerLayout, navigationView)
        userProfileManager = UserProfileManager(navigationView)

        val gamesList = intent.getParcelableArrayListExtra(
            "GAMES_LIST",
            Game::class.java
        )

        viewPagerSetup = ViewPagerSetup(
            this,
            viewPager,
            findViewById(R.id.tabsLayout),
            gamesList
        )
    }

    private fun setupUI() {
        findViewById<ImageButton>(R.id.menuButton).setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        userProfileManager.updateNavigationHeader(
            intent.getStringExtra("USER_NAME"),
            intent.getStringExtra("USER_EMAIL")
        )

        viewPagerSetup.setup()
    }

    override fun onBackPressed() {
        if (!navigationManager.handleBackPress()) {
            super.onBackPressedDispatcher.onBackPressed()
        }
    }

    fun onCategoriesSelected(categories: List<String>) {
        supportFragmentManager.fragments
            .filterIsInstance<HomeFragment>()
            .firstOrNull()?.setSelectedCategories(categories)
    }

    fun onSearchSubmitted(searchQuery: String) {
        supportFragmentManager.findFragmentByTag("f0")?.let { fragment ->
            if (fragment is HomeFragment) {
                fragment.onSearchSubmitted(searchQuery)
            }
        }
        viewPager.currentItem = 0
    }

    override fun onGameSaved(game: Game, isLiked: Boolean) {
        try {
            viewPagerSetup.getPagesAdapter().getCollectionFragment().addGame(game, isLiked)
        } catch (e: Exception) {
            println("Errore salvando il gioco: ${e.message}")
        }
    }
}