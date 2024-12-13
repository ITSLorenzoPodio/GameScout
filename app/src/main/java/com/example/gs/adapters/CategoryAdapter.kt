package com.example.gs.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.gs.R

class CategoryAdapter(
    private val categories: List<String>,
    private val onCategoryToggled: (List<String>) -> Unit
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    private val selectedCategories = mutableSetOf<String>()

    class CategoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.categoryTextView)
    }

    // Crea una nuova istanza del ViewHolder per ogni elemento della lista
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_category, parent, false)
        return CategoryViewHolder(view)
    }

    // Popola il ViewHolder con i dati per ogni elemento della lista
    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = categories[position]
        holder.textView.text = category

        // aggiorna lo stato di selezione della categoria
        updateViewState(holder, category)

        // Quando l'utente clicca su una categoria, aggiorna lo stato di selezione
        holder.itemView.setOnClickListener {
            if (selectedCategories.contains(category)) {
                selectedCategories.remove(category)
            } else {
                selectedCategories.add(category)
            }
            notifyItemChanged(position)
            onCategoryToggled(selectedCategories.toList())
        }
    }

    // Aggiorna lo stato di selezione della categoria
    private fun updateViewState(holder: CategoryViewHolder, category: String) {
        val isSelected = selectedCategories.contains(category)

        // Setta il colore di sfondo in base alla selezione
        holder.itemView.setBackgroundResource(
            if (isSelected) R.color.selected_category_background
            else android.R.color.transparent
        )

        // Setta il  colore del testo in base alla selezione
        holder.textView.setTextColor(
            ContextCompat.getColor(
                holder.itemView.context,
                if (isSelected) R.color.cardBackground
                else R.color.white

            )
        )
    }

    // Restituisce il numero di elementi nella lista
    override fun getItemCount() = categories.size

    // Restituisce la lista di categorie selezionate
    fun getSelectedCategories(): List<String> = selectedCategories.toList()

    // Rimuove tutte le selezioni
    fun clearSelections() {
        selectedCategories.clear()
        notifyDataSetChanged()
    }
}