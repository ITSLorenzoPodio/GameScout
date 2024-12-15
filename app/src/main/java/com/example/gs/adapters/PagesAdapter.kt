package com.example.gs.adapters
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.gs.ui.fragments.CollectionFragment
import com.example.gs.ui.fragments.HomeFragment
import com.example.gs.ui.fragments.SearchFragment

// Adapter per gestire le pagine del ViewPager
class PagesAdapter(
    // costruttore della classe
    private val fa: FragmentActivity,
    // Lista di giochi da passare al fragment HomeFragment
    private val gamesList: List<HomeFragment.Game>? = null
) : FragmentStateAdapter(fa) {

    private val collectionFragment = CollectionFragment()

    fun getCollectionFragment(): CollectionFragment = collectionFragment

    // Numero di pagine
    override fun getItemCount(): Int = 3

    // Crea i fragment
    override fun createFragment(position: Int): Fragment {
        return when (position) {
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
            else -> collectionFragment
        }
    }
}