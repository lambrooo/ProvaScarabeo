package com.example.provascarabeo



import kotlin.random.Random

class AIPlayer(private val dictionary: List<String>) {
    fun generateWord(startingLetter: Char): String? {
        val possibleWords = dictionary.filter { it.startsWith(startingLetter, ignoreCase = true) }
        return if (possibleWords.isNotEmpty()) {
            possibleWords[Random.nextInt(possibleWords.size)]
        } else {
            null
        }
    }
}