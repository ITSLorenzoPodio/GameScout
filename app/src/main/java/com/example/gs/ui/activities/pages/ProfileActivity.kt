package com.example.gs.ui.activities.pages

import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.gs.R

class ProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        // Set up the toolbar
        val toolbar = findViewById<Toolbar>(R.id.aboutToolbar)
        setSupportActionBar(toolbar)

        // Enable the back arrow
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            title = "Profilo"
        }

        // Get username from intent
        val userName = intent.getStringExtra("USER_NAME") ?: "Utente"

        // Set username
        findViewById<TextView>(R.id.userName).text = userName

        // Set initial letter
        val initial = userName.firstOrNull()?.uppercase() ?: "U"
        findViewById<TextView>(R.id.initialLetter).text = initial
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}