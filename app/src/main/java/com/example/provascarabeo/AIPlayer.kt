package com.example.provascarabeo

import kotlin.random.Random

class AIPlayer(private val dictionary: Set<String>) {
    fun generateWord(startingLetter: Char, minLength: Int = 0, requiredLetters: List<Char> = listOf()): String? {
        val possibleWords = dictionary.filter {
            it.startsWith(startingLetter, ignoreCase = true) &&
                    it.length >= minLength &&
                    requiredLetters.all { letter -> it.contains(letter, ignoreCase = true) }
        }
        return if (possibleWords.isNotEmpty()) {
            possibleWords[Random.nextInt(possibleWords.size)]
        } else {
            null
        }
    }
}