package com.aol.philipphofer.persistence

import android.content.Context
import android.content.SharedPreferences
import com.aol.philipphofer.logic.Position
import com.aol.philipphofer.logic.help.Difficulty
import com.aol.philipphofer.logic.help.NumberSerializer
import com.aol.philipphofer.logic.sudoku.Number
import com.aol.philipphofer.logic.sudoku.Sudoku
import java.io.File
import java.time.LocalDate
import java.time.ZoneId

open class Data private constructor(context: Context) {

    // the singleton lives as long as the app, whichever context reaches it first
    private val appContext: Context = context.applicationContext ?: context

    private val data: SharedPreferences = appContext.getSharedPreferences(NAME, Context.MODE_PRIVATE)
    private val editor: SharedPreferences.Editor = data.edit()
    private val history = GameHistory(File(appContext.filesDir, HISTORY_FILE))

    var loadmode: Boolean
        get() = loadBoolean(LOAD_MODE, false)
        set(value) = saveBoolean(LOAD_MODE, value)

    companion object Constants {
        const val NAME = "data"

        // for game loading
        const val SUDOKU_FIELD_NAME = "sudokufield"
        const val LOAD_MODE = "loadmode"

        // a game shared by a friend, waiting on the home screen until it is played or dismissed
        const val SHARED_FIELD_NAME = "sharedfield"
        const val SHARED_DIFFICULTY = "shareddifficulty"
        const val SHARED_PENDING = "sharedpending"

        // for settings
        const val SETTINGS_MARK_LINES = "marklines";
        const val SETTINGS_MARK_NUMBERS = "marknumbers"
        const val SETTINGS_MARK_ERRORS = "markerrors"
        const val SETTINGS_CHECK_NOTES = "checknotes"
        const val SETTINGS_SHOW_TIME = "showtime"
        const val SETTINGS_HIDE_COMPLETED = "hidecompleted"

        // this functionality does not exist anymore, but some people might have bought this
        const val SETTINGS_SUPPORTER = "supporter"

        // for actual game
        const val GAME_DIFFICULTY = "difficulty"
        const val GAME_ERRORS = "errors"
        const val GAME_TIME = "time"
        const val GAME_SHOW_ERRORS = "main_show_errors"
        const val GAME_SHOW_TIME = "main_show_time"

        // for statistics: every finished game in the history file, older games as legacy totals
        const val HISTORY_FILE = "game_history.csv"
        const val STATISTICS_VERSION = "statistics_version"
        private const val STATISTICS_VERSION_HISTORY = 1

        // the legacy totals per difficulty, written once by the migration
        const val LEGACY_SOLVED = "legacy_solved"
        const val LEGACY_TIMED = "legacy_timed"
        const val LEGACY_TOTAL_TIME = "legacy_totaltime"
        const val LEGACY_BEST_TIME = "legacy_besttime"

        // statistics before the history, per difficulty, only read by the migration
        const val OLD_BEST_TIME = "besttime"
        const val OLD_TIME_OVERALL = "timeoverall"
        const val OLD_TIMES_PLAYED = "timesplayed"
        const val OLD_UNTIMED = "untimed"


        @Volatile
        @JvmStatic
        private var instance: Data? = null

        fun instance(context: Context) =
            instance ?: synchronized(this) {
                instance ?: Data(context).also { instance = it }
            }
    }

    fun saveInt(key: String, value: Int) {
        editor.putInt(key, value).apply()
    }

    fun loadInt(key: String): Int {
        return data.getInt(key, 0)
    }

    fun saveBoolean(key: String, bool: Boolean) {
        editor.putBoolean(key, bool).apply()
    }

    fun loadBoolean(key: String): Boolean {
        return loadBoolean(key, true)
    }

    fun loadBoolean(key: String, defValue: Boolean): Boolean {
        return data.getBoolean(key, defValue)
    }

    fun saveGameNumber(number: Number, position: Position) {
        editor.putString(SUDOKU_FIELD_NAME + position, NumberSerializer.numberToString(number))
            .apply()
    }

    fun saveSudoku(sudoku: Sudoku) {
        saveFields(SUDOKU_FIELD_NAME, sudoku)
        saveInt(GAME_ERRORS, sudoku.overallErrors)
    }

    fun loadSudoku(): Sudoku {
        val sudoku = loadFields(SUDOKU_FIELD_NAME)
        sudoku.overallErrors = loadInt(GAME_ERRORS)
        return sudoku
    }

    fun saveSharedGame(sudoku: Sudoku, difficulty: Difficulty) {
        saveFields(SHARED_FIELD_NAME, sudoku)
        saveInt(SHARED_DIFFICULTY, difficulty.number)
        saveBoolean(SHARED_PENDING, true)
    }

    // the difficulty of the waiting shared game, null when there is none
    fun sharedGameDifficulty(): Difficulty? =
        if (loadBoolean(SHARED_PENDING, false)) Difficulty.getDifficulty(loadInt(SHARED_DIFFICULTY)) else null

    fun loadSharedGame(): Sudoku = loadFields(SHARED_FIELD_NAME)

    fun dismissSharedGame() = saveBoolean(SHARED_PENDING, false)

    // the 81 fields of a sudoku, each under the prefix and its position
    private fun saveFields(prefix: String, sudoku: Sudoku) {
        for (block in 0..8)
            for (row in 0..2)
                for (col in 0..2) {
                    val position = Position(block, row, col)
                    editor.putString(prefix + position, NumberSerializer.numberToString(sudoku.getNumber(position)))
                }
    }

    private fun loadFields(prefix: String): Sudoku {
        val sudoku = Sudoku()
        for (block in 0..8)
            for (row in 0..2)
                for (col in 0..2) {
                    val position = Position(block, row, col)
                    sudoku.blocks[block].numbers[row][col] =
                        NumberSerializer.numberFromString(data.getString(prefix + position, ""))
                }
        return sudoku
    }

    // the settings that apply from the next game on are fixed when a game starts
    fun applyNextGameSettings() {
        saveBoolean(GAME_SHOW_ERRORS, loadBoolean(SETTINGS_MARK_ERRORS))
        saveBoolean(GAME_SHOW_TIME, loadBoolean(SETTINGS_SHOW_TIME))
    }

    fun addGame(record: GameRecord) {
        migrateStatistics()
        history.add(record)
    }

    // the statistics of all difficulties together when [difficulty] is null
    fun statistics(difficulty: Difficulty?): DifficultyStatistics {
        migrateStatistics()
        val legacy = if (difficulty != null) loadLegacyStatistics(difficulty)
        else Difficulty.entries.map { loadLegacyStatistics(it) }.reduce(LegacyStatistics::plus)
        return DifficultyStatistics.of(difficulty, legacy, history.records())
    }

    fun gameHistory(): List<GameRecord> = history.records()

    fun streak(): Int = Streak.days(history.records(), LocalDate.now(), ZoneId.systemDefault())

    fun solvedPerDay(difficulty: Difficulty?): Map<LocalDate, Int> =
        SolvedGames.perDay(history.records(), difficulty, ZoneId.systemDefault())

    private fun loadLegacyStatistics(difficulty: Difficulty) = LegacyStatistics(
        solved = loadInt(LEGACY_SOLVED + difficulty.number),
        timed = loadInt(LEGACY_TIMED + difficulty.number),
        totalTime = loadInt(LEGACY_TOTAL_TIME + difficulty.number),
        bestTime = loadInt(LEGACY_BEST_TIME + difficulty.number)
    )

    /**
     * The statistics used to be totals per difficulty. They can not be split into single games,
     * so they are kept as legacy totals that the statistics add to the recorded games.
     */
    private fun migrateStatistics() {
        if (loadInt(STATISTICS_VERSION) >= STATISTICS_VERSION_HISTORY) return

        for (difficulty in Difficulty.entries) {
            val n = difficulty.number
            // only won games were counted, the untimed ones have no time
            val solved = loadInt(OLD_TIMES_PLAYED + n)
            saveInt(LEGACY_SOLVED + n, solved)
            saveInt(LEGACY_TIMED + n, (solved - loadInt(OLD_UNTIMED + n)).coerceAtLeast(0))
            saveInt(LEGACY_TOTAL_TIME + n, loadInt(OLD_TIME_OVERALL + n))
            saveInt(LEGACY_BEST_TIME + n, loadInt(OLD_BEST_TIME + n))

            for (key in listOf(OLD_TIMES_PLAYED, OLD_UNTIMED, OLD_TIME_OVERALL, OLD_BEST_TIME))
                editor.remove(key + n)
        }
        saveInt(STATISTICS_VERSION, STATISTICS_VERSION_HISTORY)
    }

    fun drop() {
        val supporter = loadBoolean(SETTINGS_SUPPORTER, false)
        editor.clear()
        history.clear()
        saveBoolean(SETTINGS_SUPPORTER, supporter)
    }
}