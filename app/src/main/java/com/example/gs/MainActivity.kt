package com.example.gs

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // ViewPager
        val viewPager = findViewById<ViewPager2>(R.id.viewPager)
        // Disabilita lo swipe tra le pagine
        viewPager.isUserInputEnabled = false
        viewPager.adapter = PagesAdapter(this)

        // TabLayout
        val tabLayout = findViewById<TabLayout>(R.id.tabsLayout)
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            when (position) {
                0 -> tab.setIcon(R.drawable.baseline_home_24)
                1 -> tab.setIcon(R.drawable.baseline_manage_search_24)
                else -> tab.setIcon(R.drawable.baseline_accessible_forward_24)
            }
        }.attach()
    }
}