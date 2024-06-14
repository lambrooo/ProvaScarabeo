package com.example.provascarabeo

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.example.provascarabeo.R

class MainActivity : AppCompatActivity() {
    private lateinit var initialLetterTextView: TextView
    private lateinit var wordEditText: EditText
    private lateinit var submitWordButton: Button
    private lateinit var gameViewModel: GameViewModel
    private lateinit var aiPlayer: AIPlayer
    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initialLetterTextView = findViewById(R.id.initialLetterTextView)
        wordEditText = findViewById(R.id.wordEditText)
        submitWordButton = findViewById(R.id.submitWordButton)

        gameViewModel = ViewModelProvider(this).get(GameViewModel::class.java)
        gameViewModel.currentLetter.observe(this, { letter ->
            initialLetterTextView.text = letter
        })

        val dictionary = listOf("apple", "banana", "cherry", "date", "elderberry")
        aiPlayer = AIPlayer(dictionary)

        db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "wordgame-database"
        ).build()

        submitWordButton.setOnClickListener {
            val word = wordEditText.text.toString()
            if (word.isNotEmpty() && word.startsWith(initialLetterTextView.text.toString(), true)) {
                gameViewModel.updateLetter(word.last().toString().toUpperCase())
                wordEditText.text.clear()
                computerTurn()
            } else {
                wordEditText.error = "La parola deve iniziare con la lettera ${initialLetterTextView.text}"
            }
        }

        // Example of inserting a user
        val lifecycleScope:lifecycleScope?=null
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                db.userDao().insert(User(0, "Player1", 100))
            }
        }

        // Example of reading users
        lifecycleScope.launch {
            val users = withContext(Dispatchers.IO) {
                db.userDao().getAllUsers()
            }
            users.forEach {
                println("User: ${it.username}, Score: ${it.score}")
            }
        }
    }

    private fun computerTurn() {
        val startingLetter = initialLetterTextView.text.toString().first()
        val word = aiPlayer.generateWord(startingLetter)
        if (word != null) {
            gameViewModel.updateLetter(word.last().toString().toUpperCase())
        } else {
            initialLetterTextView.text = "A"
        }
    }
}
