package com.example.provascarabeo

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import java.io.BufferedReader
import java.io.InputStreamReader
import kotlin.random.Random

class SinglePlayerActivity : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var initialLetterTextView: TextView
    private lateinit var wordEditText: EditText
    private lateinit var submitWordButton: Button
    private lateinit var endGameButton: Button
    private lateinit var viewUsedWordsButton: Button
    private lateinit var scoreTextView: TextView
    private lateinit var timerTextView: TextView
    private lateinit var aiWordTextView: TextView
    private lateinit var aiThinkingProgressBar: ProgressBar
    private lateinit var turnTextView: TextView
    private lateinit var conditionsTextView: TextView
    private lateinit var gameViewModel: GameViewModel
    private lateinit var aiPlayer: AIPlayer
    private lateinit var db: AppDatabase
    private val scope = MainScope()
    private lateinit var dictionary: Set<String>
    private var playerScore = 0
    private var aiScore = 0
    private lateinit var countDownTimer: CountDownTimer
    private var isFirstTurn = true
    private var difficulty: String = "Facile"
    private var aiLoseProbability = 0.05
    private var minWordLength = 3
    private var requiredLetters = listOf<Char>()
    private val usedWords = mutableSetOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_single_player)

        sharedPreferences = getSharedPreferences("game_stats", Context.MODE_PRIVATE)

        initialLetterTextView = findViewById(R.id.initialLetterTextView)
        wordEditText = findViewById(R.id.wordEditText)
        submitWordButton = findViewById(R.id.submitWordButton)
        endGameButton = findViewById(R.id.endGameButton)
        viewUsedWordsButton = findViewById(R.id.viewUsedWordsButton)
        scoreTextView = findViewById(R.id.scoreTextView)
        timerTextView = findViewById(R.id.timerTextView)
        aiWordTextView = findViewById(R.id.aiWordTextView)
        aiThinkingProgressBar = findViewById(R.id.aiThinkingProgressBar)
        turnTextView = findViewById(R.id.turnTextView)
        conditionsTextView = findViewById(R.id.conditionsTextView)

        gameViewModel = ViewModelProvider(this).get(GameViewModel::class.java)
        gameViewModel.currentLetter.observe(this, Observer { letter ->
            initialLetterTextView.text = letter
        })

        // Carica il dizionario in un thread separato
        scope.launch {
            dictionary = loadDictionaryFromAssets(this@SinglePlayerActivity, "parole_italiane.txt")
            aiPlayer = AIPlayer(dictionary)
        }

        db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "wordgame-database"
        ).build()

        submitWordButton.setOnClickListener {
            val word = wordEditText.text.toString()
            if (word.isNotEmpty() && word.startsWith(initialLetterTextView.text.toString(), true)) {
                if (isValidWord(word)) {
                    playerScore += word.length
                    updateScore()
                    gameViewModel.updateLetter(word.last().toString().toUpperCase())
                    wordEditText.text.clear()
                    usedWords.add(word.toLowerCase())
                    countDownTimer.cancel()
                    Handler(Looper.getMainLooper()).postDelayed({
                        computerTurn()
                    }, 1000) // Ritardo di 1 secondo per simulare il turno dell'IA
                } else {
                    wordEditText.error = "La parola non è valida"
                }
            } else {
                wordEditText.error = "La parola deve iniziare con la lettera ${initialLetterTextView.text}"
            }
        }

        endGameButton.setOnClickListener {
            endGame()
        }

        viewUsedWordsButton.setOnClickListener {
            showUsedWords()
        }

        difficulty = intent.getStringExtra("DIFFICULTY") ?: "Facile"
        when (difficulty) {
            "Facile" -> {
                aiLoseProbability = 0.05
                minWordLength = 3
                requiredLetters = listOf()
            }
            "Medio" -> {
                aiLoseProbability = 0.03
                minWordLength = 5
                requiredLetters = generateRandomLetters(2)
            }
            "Difficile" -> {
                aiLoseProbability = 0.01
                minWordLength = 7
                requiredLetters = generateRandomLetters(3)
            }
        }

        updateConditionsTextView()
        startPlayerTurn()
    }

    private fun startPlayerTurn() {
        turnTextView.text = "Turno del Giocatore"
        turnTextView.textSize = 24f
        turnTextView.setTextColor(Color.BLACK)

        if (isFirstTurn) {
            // Imposta una lettera iniziale randomica per il primo turno
            val randomLetter = ('A'..'Z').random()
            gameViewModel.updateLetter(randomLetter.toString())
            isFirstTurn = false
        }

        countDownTimer = object : CountDownTimer(30000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timerTextView.text = "Tempo rimasto: ${millisUntilFinished / 1000} secondi"
                if (millisUntilFinished <= 10000) {
                    timerTextView.setTextColor(Color.RED)
                } else {
                    timerTextView.setTextColor(Color.BLACK)
                }
            }

            override fun onFinish() {
                endGame()
            }
        }.start()
    }

    private fun computerTurn() {
        turnTextView.text = "Turno dell'IA"
        turnTextView.textSize = 24f
        turnTextView.setTextColor(Color.RED)

        // Mostra l'animazione di pensiero dell'IA
        aiThinkingProgressBar.visibility = ProgressBar.VISIBLE

        // Incrementa la probabilità che l'IA perda il turno
        aiLoseProbability += when (difficulty) {
            "Facile" -> 0.10
            "Medio" -> 0.07
            "Difficile" -> 0.05
            else -> 0.05
        }

        // Verifica se l'IA perde il turno
        if (Random.nextDouble() < aiLoseProbability) {
            endGame()
            return
        }

        Handler(Looper.getMainLooper()).postDelayed({
            aiThinkingProgressBar.visibility = ProgressBar.GONE
            val startingLetter = initialLetterTextView.text.toString().first()
            val word = aiPlayer.generateWord(startingLetter, minWordLength, requiredLetters)
            if (word != null && !usedWords.contains(word.toLowerCase())) {
                aiScore += word.length
                updateScore()
                aiWordTextView.text = "Parola IA: $word"
                gameViewModel.updateLetter(word.last().toString().toUpperCase())
                usedWords.add(word.toLowerCase())
                startPlayerTurn()
            } else {
                endGame()
            }
        }, 5000) // Ritardo di 5 secondi per simulare il pensiero dell'IA
    }

    private fun updateScore() {
        scoreTextView.text = "Punteggio: Giocatore $playerScore - IA $aiScore"
    }

    private fun endGame() {
        val winner = when {
            playerScore > aiScore -> "Giocatore"
            aiScore > playerScore -> "IA"
            else -> "Pareggio"
        }
        val message = "Il gioco è finito!\nVincitore: $winner\nPunteggio finale: Giocatore $playerScore - IA $aiScore"
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Fine del Gioco")
        builder.setMessage(message)
        builder.setPositiveButton("OK") { _, _ ->
            updateStatistics(winner)
            finish()
        }
        builder.show()
    }

    private fun showUsedWords() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Parole Usate")
        builder.setMessage(usedWords.joinToString("\n"))
        builder.setPositiveButton("OK", null)
        builder.show()
    }

    private fun updateConditionsTextView() {
        val conditions = "Lunghezza minima: $minWordLength\nLettere richieste: ${requiredLetters.joinToString(", ")}"
        conditionsTextView.text = conditions
    }

    private fun generateRandomLetters(count: Int): List<Char> {
        val letters = listOf('A', 'E', 'I', 'O')
        return List(count) { letters.random() }
    }

    private fun updateStatistics(winner: String) {
        val editor = sharedPreferences.edit()
        val gamesPlayed = sharedPreferences.getInt("gamesPlayed", 0) + 1
        val gamesWon = sharedPreferences.getInt("gamesWon", 0)
        val gamesLost = sharedPreferences.getInt("gamesLost", 0)
        var longestWord = sharedPreferences.getString("longestWord", "") ?: ""
        var mostUsedWord = sharedPreferences.getString("mostUsedWord", "") ?: ""
        var mostUsedWordCount = sharedPreferences.getInt("mostUsedWordCount", 0)

        if (winner == "Giocatore") {
            editor.putInt("gamesWon", gamesWon + 1)
        } else if (winner == "IA") {
            editor.putInt("gamesLost", gamesLost + 1)
        }

        for (word in usedWords) {
            if (word.length > longestWord.length) {
                longestWord = word
            }
            val wordCount = usedWords.count { it == word }
            if (wordCount > mostUsedWordCount) {
                mostUsedWord = word
                mostUsedWordCount = wordCount
            }
        }

        editor.putInt("gamesPlayed", gamesPlayed)
        editor.putString("longestWord", longestWord)
        editor.putString("mostUsedWord", mostUsedWord)
        editor.putInt("mostUsedWordCount", mostUsedWordCount)
        editor.apply()
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
        countDownTimer.cancel()
    }

    private suspend fun loadDictionaryFromAssets(context: Context, fileName: String): Set<String> {
        return withContext(Dispatchers.IO) {
            val words = mutableSetOf<String>()
            try {
                val inputStream = context.assets.open(fileName)
                val reader = BufferedReader(InputStreamReader(inputStream))
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    words.add(line!!.trim().toLowerCase())
                }
                reader.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            words
        }
    }

    private fun isValidWord(word: String): Boolean {
        if (word.length < minWordLength) return false
        for (letter in requiredLetters) {
            if (!word.contains(letter, ignoreCase = true)) return false
        }
        return dictionary.contains(word.toLowerCase()) && !usedWords.contains(word.toLowerCase())
    }
}
