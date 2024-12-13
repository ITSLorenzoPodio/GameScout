package com.example.gs.pages

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.gs.MainActivity
import com.example.gs.R

class ThorActivity : AppCompatActivity() {
    private lateinit var viewPager: ViewPager2
    private lateinit var btnNext: Button

    // Lista di pagine del tutorial
    private val tutorialPages = listOf(
        TutorialPage(
            R.drawable.tutorial1,
            "Scopri giochi nuovi",
            "Scorri a destra per salvare i giochi che ti piacciono, a sinistra per quelli che non ti interessano. Un modo veloce e divertente per trovare il tuo prossimo gioco preferito!"
        ),
        TutorialPage(
            R.drawable.tutorial2,
            "Trova esattamente quello che cerchi",
            "Usa i filtri avanzati e la barra di ricerca per trovare giochi per genere, prezzo, piattaforma e molto altro ancora!"
        ),
        TutorialPage(
            R.drawable.tutorial3,
            "Organizza Le Tue Scoperte",
            "Accedi facilmente alla tua lista di giochi salvati e acquistali direttamente su Steam o Epic quando sei pronto!"
        )
    )

    // Pagina del tutorial
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_thor)

        viewPager = findViewById(R.id.viewPagerTutorial)
        btnNext = findViewById(R.id.btnNext)

        val tutorialAdapter = TutorialPagerAdapter(tutorialPages)
        viewPager.adapter = tutorialAdapter

        // Gestisci il pulsante "Avanti"
        btnNext.text = "Avanti"
        btnNext.setOnClickListener {
            if (viewPager.currentItem < tutorialPages.size - 1) {
                viewPager.currentItem++
            } else {
                startActivity(Intent(this, LoadingActivity::class.java))
                finish()
            }
        }

        // Gestisci il cambio di pagina nel ViewPager per aggiornare il testo del pulsante
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                btnNext.text = if (position == tutorialPages.size - 1) "Inizia" else "Avanti"
            }
        })
    }
}

// Classe per rappresentare una pagina del tutorial
data class TutorialPage(
    val imageResId: Int,
    val title: String,
    val description: String
)

// Adapter per il ViewPager del tutorial
class TutorialPagerAdapter(private val pages: List<TutorialPage>) :
    RecyclerView.Adapter<TutorialPagerAdapter.TutorialViewHolder>() {

    inner class TutorialViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imgTutorial: ImageView = itemView.findViewById(R.id.imgTutorial)
        private val txtTitle: TextView = itemView.findViewById(R.id.txtTitle)
        private val txtDescription: TextView = itemView.findViewById(R.id.txtDescription)

        fun bind(page: TutorialPage) {
            imgTutorial.setBackgroundResource(page.imageResId)
            txtTitle.text = page.title
            txtDescription.text = page.description
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TutorialViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_presentation, parent, false)
        return TutorialViewHolder(view)
    }

    override fun onBindViewHolder(holder: TutorialViewHolder, position: Int) {
        holder.bind(pages[position])
    }

    override fun getItemCount() = pages.size
}