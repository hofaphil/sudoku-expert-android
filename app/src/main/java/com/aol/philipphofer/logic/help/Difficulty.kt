package com.aol.philipphofer.logic.help

import android.content.Context
import com.aol.philipphofer.R

enum class Difficulty(
    val number: Int,
    val text: Int,
    val freeFields: Int,
    // filled stars of the difficulty
    val icon: Int
) {

    BEGINNER(0, R.string.beginner, 42, R.drawable.ic_beginner),
    ADVANCED(1, R.string.advanced, 49, R.drawable.ic_advanced),
    EXPERT(2, R.string.expert, 56, R.drawable.ic_expert);

    fun getText(context: Context): String {
        return context.getString(text)
    }

    // the next harder difficulty, null for the hardest
    fun next(): Difficulty? = entries.getOrNull(ordinal + 1)

    companion object {
        @JvmStatic
        fun getDifficulty(number: Int): Difficulty {
            return when (number) {
                0 -> BEGINNER
                2 -> EXPERT
                else -> ADVANCED
            }
        }
    }
}
