package com.example.provascarabeo

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.example.provascarabeo.R

class MultiplayerGameActivity : AppCompatActivity() {
    private lateinit var initialLetterTextView: TextView
    private lateinit var wordEditText: EditText
    private lateinit var submitWordButton: Button
    private var currentPlayer = "A"
    private val gameId = "game1" // Example game ID

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_multiplayer_game)

        initialLetterTextView = findViewById(R.id.initialLetterTextView)
        wordEditText = findViewById(R.id.wordEditText)
        submitWordButton = findViewById(R.id.submitWordButton)

        submitWordButton.setOnClickListener {
            val word = wordEditText.text.toString()
            if (word.isNotEmpty() && word.startsWith(initialLetterTextView.text.toString(), true)) {
                updateGame(word)
                wordEditText.text.clear()
            } else {
                wordEditText.error = "La parola deve iniziare con la lettera ${initialLetterTextView.text}"
            }
        }

        listenForGameUpdates()
    }

    private fun updateGame(word: String) {
        val nextLetter = word.last().toString().toUpperCase()
        FirebaseUtils.firebaseDatabase.child("games").child(gameId).setValue(nextLetter)
    }

    private fun listenForGameUpdates() {
        FirebaseUtils.firebaseDatabase.child("games").child(gameId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val nextLetter = snapshot.getValue(String::class.java)
                    if (nextLetter != null) {
                        initialLetterTextView.text = nextLetter
                        currentPlayer = if (currentPlayer == "A") "B" else "A"
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle error
                }
            })
    }
}