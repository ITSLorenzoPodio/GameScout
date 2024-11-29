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
import com.example.gs.R

class ThorActivity : AppCompatActivity() {
    private lateinit var viewPager: ViewPager2
    private lateinit var btnNext: Button

    // Dati per le schermate del tutorial
    private val tutorialPages = listOf(
        TutorialPage(
            R.drawable.foto_presentazione,
            "Fabio.",
            "Fabio è quel tipo di persona che si sveglia ogni mattina con una missione: rendere il mondo più surreale. Indossa cravatte sopra le magliette, un calzino di lana e uno di seta, e ha una collezione di cappelli che include uno fatto interamente di cucchiai. Si presenta con un monociclo persino per andare a prendere il pane. Ogni conversazione con lui finisce inevitabilmente con un monologo sul perché le patatine dovrebbero essere considerate \"il cibo dell'anima\". Una volta ha comprato 37 piccioni solo per metterli in fila e vedere \"se creano un pattern matematico\". È convinto che l'universo lo stia spiando e, sinceramente, potrebbe anche aver ragione."
        ),
        TutorialPage(
            R.drawable.podio_presentation,
            "Lorenzo Podio",
            "Lorenzo è un genio... o forse no, chi lo sa. Costruisce macchinari assurdi nel garage, tipo un tostapane che fa karaoke o un aspirapolvere che recita poesie mentre pulisce. Vive seguendo leggi tutte sue: ad esempio, ogni martedì è \"la giornata dell'inversione\", quindi cammina all'indietro e saluta con un \"arrivederci\" quando entra. Una volta ha organizzato una gara di rotolamento giù per una collina usando solo palloni da calcio sgonfi: nessuno sa chi abbia vinto, nemmeno lui. Se gli chiedi cosa fa nella vita, ti risponde sempre: \"Sto cercando di convincere i semafori a collaborare con me\"."
        ),
        TutorialPage(
            R.drawable.parisi_presentation,
            "Lorenzo Parisi",
            "Questo Lorenzo vive nel suo mondo parallelo. È il tipo che parla con i cartelli stradali e pretende che gli rispondano. Sostiene che \"ogni cosa ha un'anima\", quindi è stato visto fare complimenti a un tostapane e consolare un palloncino scoppiato. Una volta ha speso una giornata intera inseguendo una farfalla, convinto che fosse un messaggero cosmico. La sua casa è un museo di cose improbabili: pigne dipinte a mano, mollette per bucato trasformate in action figures, e un'antenna TV che usa come scultura. Dice sempre: \"Se la vita non ti sorprende, sorprendi la vita!\" E lo fa, ogni giorno."
        )
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_thor)

        viewPager = findViewById(R.id.viewPagerTutorial)
        btnNext = findViewById(R.id.btnNext)

        // Configurazione dell'adapter per il ViewPager
        val tutorialAdapter = TutorialPagerAdapter(tutorialPages)
        viewPager.adapter = tutorialAdapter

        // Configurazione bottone
        btnNext.text = "Avanti"
        btnNext.setOnClickListener {
            // Se non siamo all'ultima pagina, vai avanti
            if (viewPager.currentItem < tutorialPages.size - 1) {
                viewPager.currentItem++
            } else {
                // All'ultima pagina, vai a LoadingActivity
                startActivity(Intent(this, LoadingActivity::class.java))
                finish()
            }
        }

        // Aggiorna il testo del bottone in base alla pagina corrente
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

// Adapter per il ViewPager
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