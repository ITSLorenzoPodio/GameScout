package com.example.gs.ui.fragments

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gs.R
import com.example.gs.adapters.CategoryAdapter
import com.example.gs.adapters.SearchResultsAdapter
import com.example.gs.model.Game
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class SearchFragment : Fragment() {
    private lateinit var searchView: SearchView
    private lateinit var categoriesRecyclerView: RecyclerView
    private lateinit var searchResultsRecyclerView: RecyclerView

    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var searchResultsAdapter: SearchResultsAdapter

    private var currentSearchQuery: String = ""
    private var selectedCategories: List<String> = emptyList()
    private var allGames: List<Game> = emptyList()

    private val categories = listOf(
        "Action", "Adventure", "RPG", "Strategy",
        "Sports", "Racing", "Simulation", "Puzzle",
        "Platform", "Fighting", "Shooter", "Horror",
        "MMO", "Card Game", "Battle Royale", "Tactical",
        "Roguelike", "Survival", "First-Person Shooter", "MOBA"
    )

    private val database = FirebaseDatabase.getInstance()
    private val gamesRef = database.getReference("games")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_seach, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        searchView = view.findViewById(R.id.searchView)
        categoriesRecyclerView = view.findViewById(R.id.categoriesRecyclerView)
        searchResultsRecyclerView = view.findViewById(R.id.searchResultsRecyclerView)

        setupSearchViewAppearance()
        setupSearchView()
        setupCategoriesRecyclerView()
        setupSearchResultsRecyclerView()
        loadAllGames()

        searchResultsRecyclerView.visibility = View.GONE
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
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                currentSearchQuery = query ?: ""
                filterGames()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                currentSearchQuery = newText ?: ""
                if (currentSearchQuery.isEmpty()) {
                    searchResultsRecyclerView.visibility = View.GONE
                } else {
                    searchResultsRecyclerView.visibility = View.VISIBLE
                    filterGames()
                }
                return true
            }
        })

        // When search view is focused/unfocused
        searchView.setOnQueryTextFocusChangeListener { _, hasFocus ->
            if (!hasFocus && currentSearchQuery.isEmpty()) {
                searchResultsRecyclerView.visibility = View.GONE
            }
        }
    }

    private fun setupCategoriesRecyclerView() {
        categoriesRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        categoryAdapter = CategoryAdapter(categories) { selectedCats ->
            selectedCategories = selectedCats
            if (currentSearchQuery.isNotEmpty()) {
                filterGames()
            }
        }
        categoriesRecyclerView.adapter = categoryAdapter
    }

    private fun setupSearchResultsRecyclerView() {
        searchResultsRecyclerView.layoutManager = LinearLayoutManager(context)
        searchResultsAdapter = SearchResultsAdapter(emptyList()) { game ->
            if (game.url.isNotEmpty()) {
                try {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(game.url))
                    startActivity(intent)
                } catch (e: Exception) {
                    Toast.makeText(context, "Could not open URL: ${e.message}", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
        searchResultsRecyclerView.adapter = searchResultsAdapter
    }

    private fun loadAllGames() {
        gamesRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                allGames = snapshot.children.mapNotNull { it.getValue(Game::class.java) }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Caricamento giochi fallito", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun filterGames() {
        if (currentSearchQuery.isEmpty()) {
            searchResultsAdapter.updateGames(emptyList())
            return
        }

        val filteredGames = allGames.filter { game ->
            val matchesSearch = game.title.contains(currentSearchQuery, ignoreCase = true)

            val matchesCategories = if (selectedCategories.isEmpty()) {
                true
            } else {
                selectedCategories.any { category ->
                    game.genre.contains(category, ignoreCase = true)
                }
            }

            matchesSearch && matchesCategories
        }

        searchResultsAdapter.updateGames(filteredGames)
    }
}