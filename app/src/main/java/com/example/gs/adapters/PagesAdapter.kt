package com.example.gs.adapters
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.gs.pages.CollectionFragment
import com.example.gs.pages.HomeFragment
import com.example.gs.pages.SearchFragment
import com.example.gs.pages.SettingsFragment

class PagesAdapter(
    private val fa: FragmentActivity,
    private val gamesList: List<HomeFragment.Game>? = null
) : FragmentStateAdapter(fa) {

    private val fragmentsMap = mutableMapOf<Int, Fragment>()

    override fun getItemCount(): Int = 4  // Changed from 3 to 4

    override fun createFragment(position: Int): Fragment {
        fragmentsMap[position]?.let { return it }

        val fragment = when (position) {
            0 -> {
                val homeFragment = HomeFragment()
                gamesList?.let { games ->
                    val bundle = Bundle().apply {
                        putParcelableArrayList("GAMES_LIST", ArrayList(games))
                    }
                    homeFragment.arguments = bundle
                }
                homeFragment
            }
            1 -> SearchFragment()
            2 -> CollectionFragment()
            else -> SettingsFragment()
        }

        fragmentsMap[position] = fragment
        return fragment
    }
}