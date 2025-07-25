package com.aol.philipphofer.persistence

import android.content.Context
import android.content.SharedPreferences
import com.aol.philipphofer.R
import com.aol.philipphofer.logic.Position
import com.aol.philipphofer.logic.help.Difficulty
import com.aol.philipphofer.logic.help.NumberSerializer
import com.aol.philipphofer.logic.sudoku.Number
import com.aol.philipphofer.logic.sudoku.Sudoku

open class Data private constructor(context: Context) {

    private val data: SharedPreferences = context.getSharedPreferences(NAME, Context.MODE_PRIVATE)
    private val editor: SharedPreferences.Editor = data.edit()

    var loadmode: Boolean
        get() = loadBoolean(LOAD_MODE, false)
        set(value) = saveBoolean(LOAD_MODE, value)

    companion object Constants {
        const val NAME = "data"

        // for game loading
        const val SUDOKU_FIELD_NAME = "sudokufield"
        const val LOAD_MODE = "loadmode"

        // for settings
        const val SETTINGS_MARK_LINES = "marklines";
        const val SETTINGS_MARK_NUMBERS = "marknumbers"
        const val SETTINGS_MARK_ERRORS = "markerrors"
        const val SETTINGS_CHECK_NOTES = "checknotes"
        const val SETTINGS_SHOW_TIME = "showtime"

        // this functionality does not exist anymore, but some people might have bought this
        const val SETTINGS_SUPPORTER = "supporter"

        // for actual game
        const val GAME_DIFFICULTY = "difficulty"
        const val GAME_ERRORS = "errors"
        const val GAME_TIME = "time"
        const val GAME_SHOW_ERRORS = "main_show_errors"
        const val GAME_SHOW_TIME = "main_show_time"

        // for statistics
        const val STATISTICS_BESTTIME = "besttime"
        const val STATISTICS_TIMEOVERALL = "timeoverall"
        const val STATISTICS_TIMESPLAYED = "timesplayed"


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
        for (block in 0..8)
            for (row in 0..2)
                for (col in 0..2) {
                    val position = Position(block, row, col)
                    editor.putString(
                        SUDOKU_FIELD_NAME + position,
                        NumberSerializer.numberToString(sudoku.getNumber(position))
                    )
                }
        saveInt(GAME_ERRORS, sudoku.overallErrors)
    }

    fun loadSudoku(): Sudoku {
        val sudoku = Sudoku()
        for (block in 0..8)
            for (row in 0..2)
                for (col in 0..2) {
                    val position = Position(block, row, col)
                    val numberString = data.getString(SUDOKU_FIELD_NAME + position, "")

                    sudoku.blocks[block].numbers[row][col] =
                        NumberSerializer.numberFromString(numberString)
                }

        sudoku.overallErrors = loadInt(GAME_ERRORS)
        return sudoku
    }

    fun addTime(time: Int, difficulty: Difficulty) {
        // number of times played
        val timesPlayed = loadInt(STATISTICS_TIMESPLAYED + difficulty.number) + 1
        saveInt(STATISTICS_TIMESPLAYED + difficulty.number, timesPlayed)

        // time overall played
        val timeOverall = loadInt(STATISTICS_TIMEOVERALL + difficulty.number) + time
        saveInt(STATISTICS_TIMEOVERALL + difficulty.number, timeOverall)

        // save best-time
        val oldBestTime = loadInt(STATISTICS_BESTTIME + difficulty.number)
        if (oldBestTime > time || oldBestTime == 0)
            saveInt(STATISTICS_BESTTIME + difficulty.number, time)
    }

    fun drop() {
        val supporter = loadBoolean(SETTINGS_SUPPORTER, false)
        editor.clear()
        saveBoolean(SETTINGS_SUPPORTER, supporter)
    }
}