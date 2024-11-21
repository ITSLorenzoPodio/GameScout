package com.example.gs
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.gs.Pages.CollectionFragment
import com.example.gs.Pages.HomeFragment
import com.example.gs.Pages.SearchFragment

class PagesAdapter(
    fa: FragmentActivity,
    private val gamesList: List<HomeFragment.Game>? = null
) : FragmentStateAdapter(fa) {
    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment = when (position) {
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
        else -> CollectionFragment()
    }
}