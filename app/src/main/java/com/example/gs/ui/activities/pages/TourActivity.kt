package com.example.gs.ui.activities.pages

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.gs.R
import com.example.gs.adapters.TutorialPagerAdapter
import com.example.gs.model.TutorialPage
import com.example.gs.ui.activities.LoadingActivity

class TourActivity : AppCompatActivity() {
    private lateinit var viewPager: ViewPager2
    private lateinit var btnNext: Button

    private val firstTitle = "Scopri giochi nuovi"
    private val firstDescription = "Scorri a destra per salvare i giochi che ti piacciono, a sinistra per quelli che non ti interessano. Un modo veloce e divertente per trovare il tuo prossimo gioco preferito!"
    private val secondTitle = "Trova esattamente quello che cerchi"
    private val secondDescription = "Usa i filtri avanzati e la barra di ricerca per trovare giochi per genere, prezzo, piattaforma e molto altro ancora!"
    private val thirdTitle = "Organizza Le Tue Scoperte"
    private val thirdDescription = "Accedi facilmente alla tua lista di giochi salvati e acquistali direttamente su Steam o Epic quando sei pronto!"

    private val tutorialPages = listOf(
        TutorialPage(
            R.drawable.tutorial1,
            firstTitle,
            firstDescription
        ),
        TutorialPage(
            R.drawable.tutorial2,
            secondTitle,
            secondDescription
        ),
        TutorialPage(
            R.drawable.tutorial3,
            thirdTitle,
            thirdDescription
        )
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tour)

        viewPager = findViewById(R.id.viewPagerTutorial)
        btnNext = findViewById(R.id.btnNext)

        val tutorialAdapter = TutorialPagerAdapter(tutorialPages)
        viewPager.adapter = tutorialAdapter

        btnNext.text = "Avanti"
        btnNext.setOnClickListener {
            if (viewPager.currentItem < tutorialPages.size - 1) {
                viewPager.currentItem++
            } else {
                startActivity(Intent(this, LoadingActivity::class.java))
                finish()
            }
        }

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                btnNext.text = if (position == tutorialPages.size - 1) "Inizia" else "Avanti"
            }
        })
    }
}