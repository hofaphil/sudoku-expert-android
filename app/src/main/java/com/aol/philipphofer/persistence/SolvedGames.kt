package com.aol.philipphofer.persistence

import com.aol.philipphofer.logic.help.Difficulty
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

/**
 * Solved games per day, for the chart of the last days.
 */
object SolvedGames {

    const val DAYS = 7

    // all difficulties when [difficulty] is null
    fun perDay(records: List<GameRecord>, difficulty: Difficulty?, zone: ZoneId): Map<LocalDate, Int> =
        records.filter { it.won && (difficulty == null || it.difficulty == difficulty) }
            .groupingBy { Instant.ofEpochMilli(it.finishedAt).atZone(zone).toLocalDate() }
            .eachCount()

    // the last days, oldest first, today last
    fun lastDays(today: LocalDate, count: Int = DAYS): List<LocalDate> =
        (count - 1 downTo 0).map { today.minusDays(it.toLong()) }
}
