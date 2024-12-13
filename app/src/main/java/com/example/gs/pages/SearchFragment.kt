package com.example.gs.pages

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.RecyclerView
import com.example.gs.MainActivity
import com.example.gs.R
import com.example.gs.adapters.CategoryAdapter
import com.example.gs.adapters.RecentSearchAdapter

class SearchFragment : Fragment() {
    private lateinit var searchView: SearchView
    private lateinit var categoriesRecyclerView: RecyclerView
    private lateinit var recentSearchesRecyclerView: RecyclerView
    private lateinit var categoryAdapter: CategoryAdapter

    private val categories = listOf(
        "Action", "Adventure", "Roguelike", "Survival",
        "Racing", "Simulation", "First-Person Shooter", "Tactical"
    )
    private val recentSearches = mutableListOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_seach, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        searchView = view.findViewById(R.id.searchView)
        categoriesRecyclerView = view.findViewById(R.id.categoriesRecyclerView)
        recentSearchesRecyclerView = view.findViewById(R.id.recentSearchesRecyclerView)

        setupSearchViewAppearance()
        loadRecentSearches()
        setupSearchView()
        setupCategoriesRecyclerView()
        setupRecentSearchesRecyclerView()
    }

    private fun setupSearchViewAppearance() {
        val searchEditText = searchView.findViewById<EditText>(androidx.appcompat.R.id.search_src_text)
        searchEditText.setTextColor(Color.WHITE)
        searchEditText.setHintTextColor(Color.WHITE)

        val searchIcon = searchView.findViewById<ImageView>(androidx.appcompat.R.id.search_mag_icon)
        searchIcon.setColorFilter(Color.WHITE)

        val closeIcon = searchView.findViewById<ImageView>(androidx.appcompat.R.id.search_close_btn)
        closeIcon.setColorFilter(Color.WHITE)
    }

    private fun setupSearchView() {
        searchView.setOnQueryTextFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                categoriesRecyclerView.visibility = View.GONE
                recentSearchesRecyclerView.visibility = View.VISIBLE
            } else {
                categoriesRecyclerView.visibility = View.VISIBLE
                recentSearchesRecyclerView.visibility = View.GONE
            }
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    addRecentSearch(it)
                    (activity as? MainActivity)?.onSearchSubmitted(it)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }
        })
    }

    private fun setupCategoriesRecyclerView() {
        categoryAdapter = CategoryAdapter(categories) { selectedCategories ->
            (activity as? MainActivity)?.onCategoriesSelected(selectedCategories)
        }
        categoriesRecyclerView.adapter = categoryAdapter
    }

    private fun setupRecentSearchesRecyclerView() {
        updateRecentSearchesAdapter()
    }

    private fun addRecentSearch(search: String) {
        recentSearches.remove(search)
        recentSearches.add(0, search)
        if (recentSearches.size > 5) {
            recentSearches.removeAt(recentSearches.lastIndex)
        }
        updateRecentSearchesAdapter()
        saveRecentSearches()
    }

    private fun updateRecentSearchesAdapter() {
        recentSearchesRecyclerView.adapter = RecentSearchAdapter(recentSearches) { search ->
            searchView.setQuery(search, false)
            (activity as? MainActivity)?.onSearchSubmitted(search)
        }
    }

    private fun saveRecentSearches() {
        context?.getSharedPreferences("SearchPrefs", Context.MODE_PRIVATE)?.edit()?.apply {
            putStringSet("recent_searches", recentSearches.toSet())
            apply()
        }
    }

    private fun loadRecentSearches() {
        context?.getSharedPreferences("SearchPrefs", Context.MODE_PRIVATE)?.let { prefs ->
            recentSearches.clear()
            recentSearches.addAll(prefs.getStringSet("recent_searches", setOf()) ?: setOf())
        }
    }

    companion object {
        fun newInstance() = SearchFragment()
    }
}