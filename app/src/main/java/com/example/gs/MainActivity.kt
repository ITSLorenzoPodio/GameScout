package com.example.gs

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.gs.adapters.PagesAdapter
import com.example.gs.pages.HomeFragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

interface GameCollectionListener {
    fun onGameSaved(game: HomeFragment.Game, isLiked: Boolean)
}

class MainActivity : AppCompatActivity(), GameCollectionListener {
    private lateinit var viewPager: ViewPager2

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
            }
        }.attach()
    }

    fun onCategorySelected(category: String?) {
        println("MainActivity received category: $category")
        val pagerAdapter = viewPager.adapter as? PagesAdapter
        println("Got pager adapter: ${pagerAdapter != null}")

        val homeFragment = pagerAdapter?.getHomeFragment()
        println("Got home fragment: ${homeFragment != null}")

        homeFragment?.onCategorySelected(category)
        viewPager.currentItem = 0
    }

    fun onSearchSubmitted(searchQuery: String) {
        val pagerAdapter = viewPager.adapter as? PagesAdapter
        val homeFragment = pagerAdapter?.getHomeFragment()
        homeFragment?.onSearchSubmitted(searchQuery)

        // Switch to home tab
        viewPager.currentItem = 0
    }

    override fun onGameSaved(game: HomeFragment.Game, isLiked: Boolean) {
        try {
            val pagerAdapter = viewPager.adapter as? PagesAdapter
            pagerAdapter?.getCollectionFragment()?.addGame(game, isLiked)
        } catch (e: Exception) {
            println("Error saving game: ${e.message}")
        }
    }
}