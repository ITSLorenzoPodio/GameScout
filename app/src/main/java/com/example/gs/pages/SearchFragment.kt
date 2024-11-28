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
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gs.MainActivity
import com.example.gs.R
import com.example.gs.adapters.CategoryAdapter
import com.example.gs.adapters.RecentSearchAdapter

class SearchFragment : Fragment() {

    private lateinit var searchView: SearchView
    private lateinit var categoriesRecyclerView: RecyclerView
    private lateinit var recentSearchesRecyclerView: RecyclerView

    private val categories = listOf("Action", "Adventure", "Roguelike", "Survival", "Racing", "Simulation", "First-Person Shooter", "Tactical")
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

        setupSearchViewAppearance() // Aggiungi questa linea
        loadRecentSearches()
        setupSearchView()
        setupCategoriesRecyclerView()
        setupRecentSearchesRecyclerView()
    }

    // Aggiungi questo metodo per personalizzare l'aspetto della SearchView
    private fun setupSearchViewAppearance() {
        // Trova l'EditText nella SearchView
        val searchEditText = searchView.findViewById<EditText>(androidx.appcompat.R.id.search_src_text)
        searchEditText.setTextColor(Color.WHITE) // Colore del testo
        searchEditText.setHintTextColor(Color.WHITE) // Colore dell'hint

        // Cambia il colore dell'icona di ricerca
        val searchIcon = searchView.findViewById<ImageView>(androidx.appcompat.R.id.search_mag_icon)
        searchIcon.setColorFilter(Color.WHITE)

        // Cambia il colore dell'icona di chiusura (X)
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
                // Optional: implement real-time search
                return true
            }
        })
    }

    private fun setupCategoriesRecyclerView() {
        val spanCount = 2 // Number of columns
        categoriesRecyclerView.layoutManager = GridLayoutManager(context, spanCount)
        categoriesRecyclerView.adapter = CategoryAdapter(categories) { category ->
            (activity as? MainActivity)?.onCategorySelected(category)
        }
    }

    private fun setupRecentSearchesRecyclerView() {
        recentSearchesRecyclerView.layoutManager = LinearLayoutManager(context)
        updateRecentSearchesAdapter()
    }

    private fun addRecentSearch(search: String) {
        recentSearches.remove(search) // Rimuovi se già presente
        recentSearches.add(0, search) // Aggiungi all'inizio
        if (recentSearches.size > 5) {
            recentSearches.removeAt(recentSearches.lastIndex)
        }
        updateRecentSearchesAdapter()
        saveRecentSearches()
    }

    private fun updateRecentSearchesAdapter() {
        recentSearchesRecyclerView.adapter = RecentSearchAdapter(recentSearches) { search ->
            searchView.setQuery(search, false) // Don't submit automatically
            (activity as? MainActivity)?.onSearchSubmitted(search)
        }
    }

    private fun performSearch(query: String) {
        Toast.makeText(context, "Ricerca avviata per: $query", Toast.LENGTH_SHORT).show()
        // Implementa la logica per effettuare la ricerca effettiva
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
}