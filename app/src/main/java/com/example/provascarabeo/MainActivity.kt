package com.example.provascarabeo

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val singlePlayerButton: Button = findViewById(R.id.singlePlayerButton)
        val multiplayerButton: Button = findViewById(R.id.multiplayerButton)
        val optionsButton: Button = findViewById(R.id.optionsButton)
        val statisticsButton: Button = findViewById(R.id.statisticsButton)

        singlePlayerButton.setOnClickListener {
            val intent = Intent(this, DifficultySelectionActivity::class.java)
            startActivity(intent)
        }

        multiplayerButton.setOnClickListener {
            val intent = Intent(this, MultiplayerGameActivity::class.java)
            startActivity(intent)
        }

        optionsButton.setOnClickListener {
            val intent = Intent(this, OptionsActivity::class.java)
            startActivity(intent)
        }

        statisticsButton.setOnClickListener {
            val intent = Intent(this, StatisticsActivity::class.java)
            startActivity(intent)
        }
    }
}