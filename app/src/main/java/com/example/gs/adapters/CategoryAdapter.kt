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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_category, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = categories[position]
        holder.textView.text = category

        // Update view based on selection state
        if (selectedCategories.contains(category)) {
            holder.itemView.setBackgroundResource(R.color.selected_category_background)
            holder.textView.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.cardBackground))
        }

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

    override fun getItemCount() = categories.size

    fun getSelectedCategories(): List<String> = selectedCategories.toList()
}