package com.example.gs.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gs.R

class RecentSearchAdapter(
    private val recentSearches: List<String>,
    private val onSearchClick: (String) -> Unit
) : RecyclerView.Adapter<RecentSearchAdapter.RecentSearchViewHolder>() {

    class RecentSearchViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.recentSearchTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecentSearchViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recent_search, parent, false)
        return RecentSearchViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecentSearchViewHolder, position: Int) {
        val search = recentSearches[position]
        holder.textView.text = search
        holder.itemView.setOnClickListener { onSearchClick(search) }
    }

    override fun getItemCount() = recentSearches.size
}