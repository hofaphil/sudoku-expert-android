package com.aol.philipphofer.persistence

import com.aol.philipphofer.logic.help.Difficulty

/**
 * One finished game, won or lost.
 *
 * @param finishedAt when the game ended, milliseconds since the epoch
 * @param time seconds needed to solve it, also with the timer hidden; null for lost games
 */
data class GameRecord(
    val finishedAt: Long,
    val difficulty: Difficulty,
    val won: Boolean,
    val time: Int?
) {

    // one line of the history file: finishedAt;difficulty;won;time
    fun serialize(): String =
        listOf(finishedAt, difficulty.number, if (won) 1 else 0, time ?: "").joinToString(SEPARATOR)

    companion object {
        private const val SEPARATOR = ";"

        // null for a broken line, e.g. cut off by a crash while writing
        fun parse(line: String): GameRecord? {
            val parts = line.trim().split(SEPARATOR)
            if (parts.size != 4) return null

            val finishedAt = parts[0].toLongOrNull() ?: return null
            val difficulty = Difficulty.entries.firstOrNull { it.number == parts[1].toIntOrNull() }
                ?: return null
            val won = when (parts[2]) {
                "1" -> true
                "0" -> false
                else -> return null
            }
            val time = if (parts[3].isEmpty()) null else parts[3].toIntOrNull() ?: return null

            return GameRecord(finishedAt, difficulty, won, time)
        }
    }
}
