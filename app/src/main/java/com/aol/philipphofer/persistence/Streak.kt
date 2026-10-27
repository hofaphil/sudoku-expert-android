package com.aol.philipphofer.persistence

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

object Streak {

    /**
     * Consecutive days with at least one solved game, counted back from today.
     * Without a solved game today the streak still counts from yesterday, it is only lost after a full day without one.
     */
    fun days(records: List<GameRecord>, today: LocalDate, zone: ZoneId): Int {
        val solvedDays = records.filter { it.won }
            .map { Instant.ofEpochMilli(it.finishedAt).atZone(zone).toLocalDate() }
            .toSet()

        var day = if (today in solvedDays) today else today.minusDays(1)
        var streak = 0
        while (day in solvedDays) {
            streak++
            day = day.minusDays(1)
        }
        return streak
    }
}
