package com.aol.philipphofer.persistence

import com.aol.philipphofer.logic.help.Difficulty

/**
 * Totals of the games before the game history existed, they can not be split into single games.
 *
 * @param solved won games, with and without the timer
 * @param timed won games played with the timer
 * @param totalTime seconds of all timed games
 * @param bestTime fastest timed game, 0 if there is none
 */
data class LegacyStatistics(
    val solved: Int = 0,
    val timed: Int = 0,
    val totalTime: Int = 0,
    val bestTime: Int = 0
) {
    // the totals of two difficulties together
    operator fun plus(other: LegacyStatistics) = LegacyStatistics(
        solved + other.solved,
        timed + other.timed,
        totalTime + other.totalTime,
        listOf(bestTime, other.bestTime).filter { it > 0 }.minOrNull() ?: 0
    )
}

/**
 * Statistics of one difficulty, or of all of them together: the legacy totals plus the recorded games.
 * Times are in seconds, 0 means there is no timed game yet.
 */
data class DifficultyStatistics(
    val solved: Int,
    val lost: Int,
    val bestTime: Int,
    val averageTime: Int,
    // seconds of all timed games together
    val totalTime: Int
) {
    companion object {
        // all difficulties when [difficulty] is null
        fun of(difficulty: Difficulty?, legacy: LegacyStatistics, records: List<GameRecord>): DifficultyStatistics {
            val games = records.filter { difficulty == null || it.difficulty == difficulty }
            val times = games.filter { it.won }.mapNotNull { it.time }

            val timed = legacy.timed + times.size
            val totalTime = legacy.totalTime + times.sum()
            val bestTime = (times + legacy.bestTime).filter { it > 0 }.minOrNull() ?: 0

            return DifficultyStatistics(
                solved = legacy.solved + games.count { it.won },
                lost = games.count { !it.won },
                bestTime = bestTime,
                averageTime = if (timed == 0) 0 else totalTime / timed,
                totalTime = totalTime
            )
        }
    }
}
