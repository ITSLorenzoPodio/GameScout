package com.example.gs.managers

import android.content.Intent
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.example.gs.R
import com.example.gs.ui.activities.pages.ProfileActivity
import com.example.gs.ui.activities.pages.AboutActivity
import com.example.gs.ui.activities.pages.AuthActivity
import com.example.gs.ui.activities.pages.SettingsActivity
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class NavigationManager(
    private val activity: AppCompatActivity,
    private val drawerLayout: DrawerLayout,
    private val navigationView: NavigationView
) {
    init {
        navigationView.setNavigationItemSelectedListener { item ->
            handleNavigationItemSelected(item)
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }
    }

    private fun handleNavigationItemSelected(item: MenuItem) {
        when (item.itemId) {
            R.id.nav_profile -> navigateToProfile()
            R.id.nav_settings -> navigateToSettings()
            R.id.nav_help -> navigateToAbout()
            R.id.nav_logout -> handleLogout()
        }
    }

    private fun navigateToProfile() {
        val intent = Intent(activity, ProfileActivity::class.java).apply {
            putExtra("USER_NAME", activity.intent.getStringExtra("USER_NAME"))
        }
        activity.startActivity(intent)
    }

    private fun navigateToSettings() {
        activity.startActivity(Intent(activity, SettingsActivity::class.java))
    }

    private fun navigateToAbout() {
        activity.startActivity(Intent(activity, AboutActivity::class.java))
    }

    private fun handleLogout() {
        FirebaseAuth.getInstance().signOut()

        val intent = Intent(activity, AuthActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_CLEAR_TASK or
                    Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        activity.startActivity(intent)
        activity.finish()
        Toast.makeText(activity, "Disconnesso con successo", Toast.LENGTH_SHORT).show()
    }

    fun handleBackPress(): Boolean {
        return if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        } else {
            false
        }
    }
}