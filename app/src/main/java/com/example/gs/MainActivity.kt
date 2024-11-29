package com.example.gs

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.example.gs.adapters.PagesAdapter
import com.example.gs.pages.CollectionFragment
import com.example.gs.pages.HomeFragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

interface GameCollectionListener {
    fun onGameSaved(game: HomeFragment.Game, isLiked: Boolean)
}

class MainActivity : AppCompatActivity(), GameCollectionListener {
    lateinit var viewPager: ViewPager2  // Changed from private to public

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Retrieve games list from intent
        val gamesList = intent.getParcelableArrayListExtra<HomeFragment.Game>("GAMES_LIST")

        // ViewPager
        viewPager = findViewById(R.id.viewPager)
        viewPager.isUserInputEnabled = false
        viewPager.adapter = PagesAdapter(this, gamesList)

        // TabLayout
        val tabLayout = findViewById<TabLayout>(R.id.tabsLayout)

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            when (position) {
                0 -> tab.setIcon(R.drawable.home_tab)
                1 -> tab.setIcon(R.drawable.search_tab)
                2 -> tab.setIcon(R.drawable.library_tab)
                3 -> tab.setIcon(R.drawable.baseline_settings_24)
            }
        }.attach()

// Add this after the TabLayoutMediator
        tabLayout.setTabIconTint(ContextCompat.getColorStateList(this, R.color.tab_icon_color))
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
            val pagerAdapter = viewPager.adapter as? PagesAdapter
            val collectionFragment = supportFragmentManager.findFragmentByTag("f2") as? CollectionFragment
            collectionFragment?.addGame(game, isLiked)
        } catch (e: Exception) {
            println("Error saving game: ${e.message}")
        }
    }
}