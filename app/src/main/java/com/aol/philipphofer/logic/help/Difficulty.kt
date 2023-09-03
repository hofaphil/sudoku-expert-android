package com.aol.philipphofer.logic.help

import android.content.Context
import com.aol.philipphofer.R

enum class Difficulty(
    val number: Int,
    val text: Int,
    val freeFields: Int
) {

    BEGINNER(0, R.string.beginner, 42),
    ADVANCED(1, R.string.advanced, 49),
    EXPERT(2, R.string.expert, 56);

    fun getText(context: Context): String {
        return context.getString(text)
    }

    companion object {
        @JvmStatic
        fun getDifficulty(number: Int): Difficulty {
            return when (number) {
                0 -> Difficulty.BEGINNER
                2 -> Difficulty.EXPERT
                else -> Difficulty.ADVANCED
            }
        }
    }
}
