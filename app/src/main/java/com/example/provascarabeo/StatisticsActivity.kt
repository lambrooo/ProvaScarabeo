package com.example.provascarabeo

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class StatisticsActivity : AppCompatActivity() {
    private lateinit var gamesPlayedTextView: TextView
    private lateinit var gamesWonTextView: TextView
    private lateinit var gamesLostTextView: TextView
    private lateinit var winPercentageTextView: TextView
    private lateinit var longestWordTextView: TextView
    private lateinit var mostUsedWordTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_statistics)

        gamesPlayedTextView = findViewById(R.id.gamesPlayedTextView)
        gamesWonTextView = findViewById(R.id.gamesWonTextView)
        gamesLostTextView = findViewById(R.id.gamesLostTextView)
        winPercentageTextView = findViewById(R.id.winPercentageTextView)
        longestWordTextView = findViewById(R.id.longestWordTextView)
        mostUsedWordTextView = findViewById(R.id.mostUsedWordTextView)

        // Carica le statistiche salvate e aggiornare le TextView
        loadStatistics()
    }

    private fun loadStatistics() {
        // Logica per caricare le statistiche salvate
        val gamesPlayed = 10 // Esempio di valore
        val gamesWon = 6 // Esempio di valore
        val gamesLost = 4 // Esempio di valore
        val winPercentage = (gamesWon.toDouble() / gamesPlayed * 100).toInt()
        val longestWord = "supercalifragilistichespiralidoso" // Esempio di valore
        val mostUsedWord = "ciao" // Esempio di valore
        val mostUsedWordCount = 5 // Esempio di valore

        gamesPlayedTextView.text = "Partite giocate: $gamesPlayed"
        gamesWonTextView.text = "Partite vinte: $gamesWon"
        gamesLostTextView.text = "Partite perse: $gamesLost"
        winPercentageTextView.text = "Percentuale vittoria: $winPercentage%"
        longestWordTextView.text = "Parola più lunga: $longestWord"
        mostUsedWordTextView.text = "Parola più usata: $mostUsedWord ($mostUsedWordCount volte)"
    }
}