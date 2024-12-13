package com.example.gs.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gs.R

// Adapter per la lista della cronologia ricerche
class RecentSearchAdapter(
    private val recentSearches: List<String>,
    private val onSearchClick: (String) -> Unit
) : RecyclerView.Adapter<RecentSearchAdapter.RecentSearchViewHolder>() {

    // ViewHolder per ogni elemento della lista
    class RecentSearchViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.recentSearchTextView)
    }

    // Crea una nuova istanza del ViewHolder per ogni elemento della lista
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecentSearchViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recent_search, parent, false)
        return RecentSearchViewHolder(view)
    }

    // Popola il ViewHolder con i dati per ogni elemento della lista
    override fun onBindViewHolder(holder: RecentSearchViewHolder, position: Int) {
        val search = recentSearches[position]
        holder.textView.text = search
        holder.itemView.setOnClickListener { onSearchClick(search) }
    }

    // Restituisce il numero di elementi nella lista
    override fun getItemCount() = recentSearches.size
}