package com.example.provascarabeo

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AlertDialog

class OptionsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_options)

        val resetStatsButton: Button = findViewById(R.id.resetStatsButton)
        val changeThemeButton: Button = findViewById(R.id.changeThemeButton)

        resetStatsButton.setOnClickListener {
            resetStatistics()
        }

        changeThemeButton.setOnClickListener {
            changeTheme()
        }
    }

    private fun resetStatistics() {
        // Logica per resettare le statistiche
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Reset Statistiche")
        builder.setMessage("Sei sicuro di voler resettare tutte le statistiche?")
        builder.setPositiveButton("Sì") { _, _ ->
            // Resetta le statistiche qui
        }
        builder.setNegativeButton("No", null)
        builder.show()
    }

    private fun changeTheme() {
        // Logica per cambiare il tema
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Cambia Tema")
        builder.setMessage("Questa funzionalità non è ancora implementata.")
        builder.setPositiveButton("OK", null)
        builder.show()
    }
}