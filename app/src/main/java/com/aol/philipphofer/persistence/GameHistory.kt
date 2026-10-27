package com.aol.philipphofer.persistence

import java.io.File

/**
 * All finished games, one line per game. New games are appended, existing lines are never rewritten.
 */
class GameHistory(private val file: File) {

    private var cache: MutableList<GameRecord>? = null

    @Synchronized
    fun records(): List<GameRecord> = load().toList()

    @Synchronized
    fun add(record: GameRecord) {
        load().add(record)
        file.parentFile?.mkdirs()
        file.appendText(record.serialize() + "\n")
    }

    @Synchronized
    fun clear() {
        cache = mutableListOf()
        file.delete()
    }

    private fun load(): MutableList<GameRecord> =
        cache ?: (if (file.exists()) file.readLines().mapNotNull { GameRecord.parse(it) } else emptyList())
            .toMutableList()
            .also { cache = it }
}
