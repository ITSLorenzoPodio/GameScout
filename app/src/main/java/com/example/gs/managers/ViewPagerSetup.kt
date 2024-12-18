package com.example.gs.managers

import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.example.gs.R
import com.example.gs.adapters.PagesAdapter
import com.example.gs.model.Game
import com.example.gs.ui.fragments.HomeFragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class ViewPagerSetup(
    private val activity: AppCompatActivity,
    private val viewPager: ViewPager2,
    private val tabLayout: TabLayout,
    private val gamesList: ArrayList<Game>?
) {
    private lateinit var pagesAdapter: PagesAdapter

    fun setup() {
        setupViewPager()
        setupTabLayout()
    }

    private fun setupViewPager() {
        viewPager.isUserInputEnabled = false
        pagesAdapter = PagesAdapter(activity, gamesList)
        viewPager.adapter = pagesAdapter
    }

    private fun setupTabLayout() {
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            when (position) {
                0 -> tab.setIcon(R.drawable.home_tab)
                1 -> tab.setIcon(R.drawable.search_tab)
                2 -> tab.setIcon(R.drawable.library_tab)
            }
        }.attach()

        tabLayout.setTabIconTint(
            ContextCompat.getColorStateList(activity, R.color.tab_icon_color)
        )
    }

    fun getPagesAdapter() = pagesAdapter
}