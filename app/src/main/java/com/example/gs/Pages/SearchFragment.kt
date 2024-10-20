package com.example.gs.Pages

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gs.R
import com.example.gs.adapters.CategoryAdapter
import com.example.gs.adapters.RecentSearchAdapter

class SearchFragment : Fragment() {

    private lateinit var searchView: SearchView
    private lateinit var categoriesRecyclerView: RecyclerView
    private lateinit var recentSearchesRecyclerView: RecyclerView

    private val categories = listOf("Azione", "Avventura", "RPG", "Strategia", "Sport", "Puzzle", "Simulazione", "FPS")
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

        loadRecentSearches()
        setupSearchView()
        setupCategoriesRecyclerView()
        setupRecentSearchesRecyclerView()
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
                    performSearch(it)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Implementa la ricerca in tempo reale se necessario
                return true
            }
        })
    }

    private fun setupCategoriesRecyclerView() {
        categoriesRecyclerView.layoutManager = LinearLayoutManager(context)
        categoriesRecyclerView.adapter = CategoryAdapter(categories) { category ->
            Toast.makeText(context, "Categoria selezionata: $category", Toast.LENGTH_SHORT).show()
            // Implementa la logica per mostrare i giochi della categoria selezionata
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
            searchView.setQuery(search, true)
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