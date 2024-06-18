package com.example.provascarabeo

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class StatisticsActivity : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var gamesPlayedTextView: TextView
    private lateinit var gamesWonTextView: TextView
    private lateinit var gamesLostTextView: TextView
    private lateinit var winPercentageTextView: TextView
    private lateinit var longestWordTextView: TextView
    private lateinit var mostUsedWordTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_statistics)

        sharedPreferences = getSharedPreferences("game_stats", Context.MODE_PRIVATE)

        gamesPlayedTextView = findViewById(R.id.gamesPlayedTextView)
        gamesWonTextView = findViewById(R.id.gamesWonTextView)
        gamesLostTextView = findViewById(R.id.gamesLostTextView)
        winPercentageTextView = findViewById(R.id.winPercentageTextView)
        longestWordTextView = findViewById(R.id.longestWordTextView)
        mostUsedWordTextView = findViewById(R.id.mostUsedWordTextView)

        loadStatistics()
    }

    private fun loadStatistics() {
        val gamesPlayed = sharedPreferences.getInt("gamesPlayed", 0)
        val gamesWon = sharedPreferences.getInt("gamesWon", 0)
        val gamesLost = sharedPreferences.getInt("gamesLost", 0)
        val winPercentage = if (gamesPlayed > 0) (gamesWon.toDouble() / gamesPlayed * 100).toInt() else 0
        val longestWord = sharedPreferences.getString("longestWord", "N/A") ?: "N/A"
        val mostUsedWord = sharedPreferences.getString("mostUsedWord", "N/A") ?: "N/A"
        val mostUsedWordCount = sharedPreferences.getInt("mostUsedWordCount", 0)

        gamesPlayedTextView.text = "Partite giocate: $gamesPlayed"
        gamesWonTextView.text = "Partite vinte: $gamesWon"
        gamesLostTextView.text = "Partite perse: $gamesLost"
        winPercentageTextView.text = "Percentuale vittoria: $winPercentage%"
        longestWordTextView.text = "Parola più lunga: $longestWord"
        mostUsedWordTextView.text = "Parola più usata: $mostUsedWord ($mostUsedWordCount volte)"
    }
}
