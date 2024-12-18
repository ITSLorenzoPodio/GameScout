package com.example.gs.managers

import android.widget.TextView
import com.example.gs.R
import com.google.android.material.navigation.NavigationView

class UserProfileManager(
    private val navigationView: NavigationView
) {
    fun updateNavigationHeader(userName: String?, userEmail: String?) {
        val headerView = navigationView.getHeaderView(0)

        headerView?.let {
            val userNameTextView: TextView = it.findViewById(R.id.userName)
            val userEmailTextView: TextView = it.findViewById(R.id.userEmail)
            val initialTextView: TextView = it.findViewById(R.id.initialLetter)

            userName?.let { name ->
                userNameTextView.text = name
                initialTextView.text = name.firstOrNull()?.uppercase() ?: "U"
            }
            userEmail?.let { email ->
                userEmailTextView.text = email
            }
        }
    }
}