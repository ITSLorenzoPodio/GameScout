package com.example.gs.pages

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.gs.R

class AboutActivity : AppCompatActivity() {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_about)

            // imposta la toolbar
            val toolbar = findViewById<Toolbar>(R.id.aboutToolbar)
            setSupportActionBar(toolbar)

            // back arrow
            supportActionBar?.apply {
                setDisplayHomeAsUpEnabled(true)
                setDisplayShowHomeEnabled(true)
                title = "Riconoscimenti"
            }
        }

        override fun onOptionsItemSelected(item: MenuItem): Boolean {
            if (item.itemId == android.R.id.home) {
                finish()
                return true
            }
            return super.onOptionsItemSelected(item)
        }
    }