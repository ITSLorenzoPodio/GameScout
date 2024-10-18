package com.example.gs
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.gs.Pages.CollectionFragment
import com.example.gs.Pages.HomeFragment
import com.example.gs.Pages.SearchFragment

class PagesAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment = when (position) {
        0 -> HomeFragment()
        1 -> SearchFragment()
        else -> CollectionFragment()
    }
}