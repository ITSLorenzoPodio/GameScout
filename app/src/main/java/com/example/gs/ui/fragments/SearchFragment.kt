package com.example.gs.ui.fragments

import android.content.Context
import android.graphics.Color
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
import com.bumptech.glide.Glide
import com.example.gs.ui.activities.MainActivity
import com.example.gs.R
import com.example.gs.adapters.CategoryAdapter
import com.example.gs.adapters.RecentSearchAdapter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class SearchResultsAdapter(
    private var games: List<HomeFragment.Game>,
    private val onItemClick: (HomeFragment.Game) -> Unit
) : RecyclerView.Adapter<SearchResultsAdapter.GameViewHolder>() {

    class GameViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val gameImage: ImageView = itemView.findViewById(R.id.gameImage)
        val gameTitle: TextView = itemView.findViewById(R.id.gameTitle)
        val gameGenre: TextView = itemView.findViewById(R.id.gameGenre)
        val gamePlatforms: TextView = itemView.findViewById(R.id.gamePlatforms)
        val gameScore: TextView = itemView.findViewById(R.id.gameScore)
        val gamePrice: TextView = itemView.findViewById(R.id.gamePrice)

        fun bind(game: HomeFragment.Game, onItemClick: (HomeFragment.Game) -> Unit) {
            gameTitle.text = game.title
            gameGenre.text = game.genre
            gamePlatforms.text = game.platforms
            gameScore.text = "Score: ${game.userScore}"
            gamePrice.text = "$${String.format("%.2f", game.currentPrice)}"

            Glide.with(itemView.context)
                .load(game.imageUrl)
                .into(gameImage)

            itemView.setOnClickListener { onItemClick(game) }
        }
    }

    fun updateGames(newGames: List<HomeFragment.Game>) {
        games = newGames
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.collection_game_item, parent, false)
        return GameViewHolder(view)
    }

    override fun onBindViewHolder(holder: GameViewHolder, position: Int) {
        holder.bind(games[position], onItemClick)
    }

    override fun getItemCount() = games.size
}

class SearchFragment : Fragment() {
    private lateinit var searchView: SearchView
    private lateinit var categoriesRecyclerView: RecyclerView
    private lateinit var recentSearchesRecyclerView: RecyclerView
    private lateinit var searchResultsRecyclerView: RecyclerView

    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var searchResultsAdapter: SearchResultsAdapter

    private val categories = listOf(
        "Action", "Adventure", "Roguelike", "Survival",
        "Racing", "Simulation", "First-Person Shooter", "Tactical"
    )
    private val recentSearches = mutableListOf<String>()

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

        // Inizializzazione view
        searchView = view.findViewById(R.id.searchView)
        categoriesRecyclerView = view.findViewById(R.id.categoriesRecyclerView)
        recentSearchesRecyclerView = view.findViewById(R.id.recentSearchesRecyclerView)
        searchResultsRecyclerView = view.findViewById(R.id.searchResultsRecyclerView)

        // Setup componenti
        setupSearchViewAppearance()
        loadRecentSearches()
        setupSearchView()
        setupCategoriesRecyclerView()
        setupRecentSearchesRecyclerView()
        setupSearchResultsRecyclerView()
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
                    searchGames(it)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }
        })
    }

    private fun setupCategoriesRecyclerView() {
        categoriesRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        categoryAdapter = CategoryAdapter(categories) { selectedCategories ->
            (activity as? MainActivity)?.onCategoriesSelected(selectedCategories)
        }
        categoriesRecyclerView.adapter = categoryAdapter
    }

    private fun setupRecentSearchesRecyclerView() {
        recentSearchesRecyclerView.layoutManager = LinearLayoutManager(context)
        updateRecentSearchesAdapter()
    }

    private fun setupSearchResultsRecyclerView() {
        searchResultsRecyclerView.layoutManager = LinearLayoutManager(context)
        searchResultsAdapter = SearchResultsAdapter(emptyList()) { game ->
            // Gestisci click sul gioco, es. apri dettagli
            Toast.makeText(context, "Selezionato: ${game.title}", Toast.LENGTH_SHORT).show()
        }
        searchResultsRecyclerView.adapter = searchResultsAdapter
    }

    private fun searchGames(query: String) {
        gamesRef.orderByChild("title")
            .startAt(query)
            .endAt(query + "\uf8ff")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val games = snapshot.children.mapNotNull { it.getValue(HomeFragment.Game::class.java) }

                    if (games.isNotEmpty()) {
                        searchResultsRecyclerView.visibility = View.VISIBLE
                        categoriesRecyclerView.visibility = View.GONE
                        recentSearchesRecyclerView.visibility = View.GONE
                        searchResultsAdapter.updateGames(games)
                    } else {
                        Toast.makeText(context, "Nessun gioco trovato", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context, "Ricerca fallita", Toast.LENGTH_SHORT).show()
                }
            })
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
            searchGames(search)
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
}