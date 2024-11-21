package com.example.gs

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.viewpager2.widget.ViewPager2
import com.example.gs.Pages.HomeFragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Retrieve games list from intent
        val gamesList = intent.getParcelableArrayListExtra<HomeFragment.Game>("GAMES_LIST")

        // ViewPager
        val viewPager = findViewById<ViewPager2>(R.id.viewPager)
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
}