package com.aol.philipphofer.logic

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.widget.RelativeLayout
import com.aol.philipphofer.R
import com.aol.philipphofer.gui.Keyboard
import com.aol.philipphofer.gui.StatusBar
import com.aol.philipphofer.gui.custom.CustomActivity
import com.aol.philipphofer.gui.custom.CustomToast
import com.aol.philipphofer.gui.dialog.EndCardDialog
import com.aol.philipphofer.gui.sudoku.SudokuField
import com.aol.philipphofer.gui.sudoku.SudokuGrid
import com.aol.philipphofer.logic.ShareClass.share
import com.aol.philipphofer.logic.Timer.TimerListener
import com.aol.philipphofer.logic.help.Difficulty
import com.aol.philipphofer.logic.help.Difficulty.Companion.getDifficulty
import com.aol.philipphofer.logic.sudoku.Sudoku
import com.aol.philipphofer.persistence.Data
import kotlin.math.abs

class MainActivity : CustomActivity(), TimerListener {

    lateinit var game: Sudoku

    lateinit var statusBar: StatusBar
    lateinit var sudokuGrid: SudokuGrid
    private lateinit var keyboard: Keyboard
    private lateinit var endCardDialog: EndCardDialog

    var selected: Position? = null
    private var isNotes = false

    var pause = false
    lateinit var timer: Timer
    private var t = Thread()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        sudokuGrid = findViewById(R.id.sudokuGrid)
        statusBar = findViewById(R.id.statusBar)
        keyboard = findViewById(R.id.keyboard)
        endCardDialog = EndCardDialog(this)

        timer = Timer(this)
        timer.startTimer(0)

        val observer = sudokuGrid.viewTreeObserver
        observer.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                init()
                sudokuGrid.viewTreeObserver.removeGlobalOnLayoutListener(this)
            }
        })
    }

    private fun init() {
        val params = sudokuGrid.layoutParams
        val mainLayout = findViewById<RelativeLayout>(R.id.mainLayout)

        val width = mainLayout.width
        val height = mainLayout.height -
                statusBar.height -
                (statusBar.layoutParams as RelativeLayout.LayoutParams).bottomMargin -
                (statusBar.layoutParams as RelativeLayout.LayoutParams).topMargin -
                keyboard.height -
                (keyboard.layoutParams as RelativeLayout.LayoutParams).bottomMargin -
                (keyboard.layoutParams as RelativeLayout.LayoutParams).topMargin

        if (height < width) {
            params.width = height
        } else {
            val paramsKeyboard = keyboard.layoutParams as RelativeLayout.LayoutParams
            paramsKeyboard.bottomMargin = (abs(width - height) / 1.5).toInt()
            keyboard.layoutParams = paramsKeyboard
        }

        sudokuGrid.layoutParams = params
    }

    override fun onStart() {
        super.onStart()
        game = Sudoku()

        // Load a game from data
        if (data.loadmode.also { LOAD_MODE = it }) {
            game = data.loadSudoku()
            sudokuGrid.init(game.blocks)
            statusBar.activate(true)
            keyboard.activate(true)
            sudokuGrid.changeBackground(SudokuGrid.BackgroundMode.VISIBLE)
        }
    }

    public override fun onResume() {
        super.onResume()
        pause = true
        pauseGame()

        // create a new game
        if (!data.loadmode.also { LOAD_MODE = it }) {
            sudokuGrid.changeBackground(SudokuGrid.BackgroundMode.LOADING)
            keyboard.activate(false)
            statusBar.activate(false)
            if (!t.isAlive) {
                t = Thread { heavyLoading() }
                t.start()
            }
        } else {
            window.decorView.post { timer.startTimer(data.loadInt(Data.GAME_TIME)) }
        }

        DIFFICULTY = getDifficulty(data.loadInt(Data.GAME_DIFFICULTY))
        statusBar.setDifficulty(DIFFICULTY)
        statusBar.setError(game.overallErrors)
    }

    private fun heavyLoading() {
        timer.stopTimer()
        game = createSudokuNative(DIFFICULTY.freeFields)

        // game created -> save and set load-mode to load sudoku next time
        data.saveSudoku(game)
        data.loadmode = !LOAD_MODE.also { LOAD_MODE = !it }

        // save the setting that apply at first next game
        data.saveBoolean(Data.GAME_SHOW_ERRORS, data.loadBoolean(Data.SETTINGS_MARK_ERRORS))
        data.saveBoolean(Data.GAME_SHOW_TIME, data.loadBoolean(Data.SETTINGS_SHOW_TIME))

        // reset game errors and time
        data.saveInt(Data.GAME_ERRORS, 0)
        data.saveInt(Data.GAME_TIME, 0)
        runOnUiThread {
            sudokuGrid.init(game.blocks)
            sudokuGrid.changeBackground(SudokuGrid.BackgroundMode.VISIBLE)
            statusBar.activate(true)
            keyboard.activate(true)
            statusBar.setError(game.overallErrors)
            window.decorView.post { timer.startTimer(0) }
        }
    }

    override fun onPause() {
        super.onPause()

        timer.stopTimer()
        data.saveInt(Data.GAME_TIME, timer.time)
        data.saveInt(Data.GAME_ERRORS, game.overallErrors)
    }

    override fun onDestroy() {
        super.onDestroy()

        timer.killTimer()
    }

    fun select(selectedPosition: Position) {
        var sudokuField: SudokuField

        // unselect old field first
        if (selected != null) {
            sudokuField =
                sudokuGrid.blocks[selected!!.block].fields[selected!!.row][selected!!.column]
            sudokuField.select(false)
            selectPartner(sudokuField, false)
        }

        // select new one now
        selected = selectedPosition
        sudokuField =
            sudokuGrid.blocks[selectedPosition.block].fields[selectedPosition.row][selectedPosition.column]
        selectPartner(sudokuField, true)
        sudokuField.select(true)
    }

    private fun selectPartner(sudokuField: SudokuField, select: Boolean) {
        if (data.loadBoolean(Data.SETTINGS_MARK_NUMBERS) && sudokuField.number.number != 0)
            for (i in 0..8) {
                val numbers = game.blocks[i].numbers
                for (a in 0..2)
                    for (b in 0..2)
                        if (numbers[a][b].number == sudokuField.number.number)
                            sudokuGrid.blocks[i].fields[a][b].lightSelect(select)
            }

        if (data.loadBoolean(Data.SETTINGS_MARK_LINES)) {
            // own block
            for (i in 0..2) {
                if (i != sudokuField.position.column)
                    sudokuGrid.blocks[sudokuField.position.block].fields[sudokuField.position.row][i]
                        .lightSelect(select)
                if (i != sudokuField.position.row) sudokuGrid.blocks[sudokuField.position.block].fields[i][sudokuField.position.column]
                    .lightSelect(select)
            }

            // other blocks
            for (i in 0..2) {
                sudokuGrid.blocks[partnerBlockLookup[sudokuField.position.block][0]].fields[sudokuField.position.row][i]
                    .lightSelect(select)
                sudokuGrid.blocks[partnerBlockLookup[sudokuField.position.block][1]].fields[sudokuField.position.row][i]
                    .lightSelect(select)
                sudokuGrid.blocks[partnerBlockLookup[sudokuField.position.block][2]].fields[i][sudokuField.position.column]
                    .lightSelect(select)
                sudokuGrid.blocks[partnerBlockLookup[sudokuField.position.block][3]].fields[i][sudokuField.position.column]
                    .lightSelect(select)
            }
        }
    }

    private fun checkNotes(position: Position, number: Int) {
        if (data.loadBoolean(Data.SETTINGS_CHECK_NOTES) && !isNotes) {
            val gameBlock = game.blocks

            // own block
            for (i in 0..2)
                for (j in 0..2)
                    gameBlock[position.block].numbers[i][j].checkNote(number)

            // other blocks
            for (i in 0..2) {
                gameBlock[partnerBlockLookup[position.block][0]].numbers[position.row][i]
                    .checkNote(number)
                gameBlock[partnerBlockLookup[position.block][1]].numbers[position.row][i]
                    .checkNote(number)
                gameBlock[partnerBlockLookup[position.block][2]].numbers[i][position.column]
                    .checkNote(number)
                gameBlock[partnerBlockLookup[position.block][3]].numbers[i][position.column]
                    .checkNote(number)
            }
        }
    }

    fun insert(number: Int) {
        selected?.let {
            selectPartner(sudokuGrid.blocks[it.block].fields[it.row][it.column], false)
            checkNotes(it, number)
            game.insert(number, it, isNotes)

            // TODO two times this.selected, maybe this can be done nicer
            select(it)

            // check if sudoku-game is finished
            checkSudoku()
        }
    }

    fun delete() {
        selected?.let {
            selectPartner(sudokuGrid.blocks[it.block].fields[it.row][it.column], false)
            game.delete(it)

            // TODO two times this.selected, maybe this can be done nicer
            select(it)

            // check new current errors
            checkErrors()
        }
    }

    fun notesMode(): Boolean {
        isNotes = !isNotes
        return isNotes
    }

    private fun checkSudoku() {
        if (game.currentErrors() == 0 && game.freeFields() == 0)
            finishSudoku()
        else if (data.loadBoolean(Data.GAME_SHOW_ERRORS)) {
            checkErrors()
            if (game.overallErrors >= MAX_ERROR)
                abortSudoku()
        }
    }

    private fun checkErrors() {
        statusBar.setError(game.overallErrors)
    }

    private fun finishSudoku() {
        timer.stopTimer()
        data.loadmode = false

        val time = if (data.loadBoolean(Data.GAME_SHOW_TIME)) timer.time else 0
        endCardDialog.show(true, time, DIFFICULTY)

        data.addTime(timer.time, DIFFICULTY)
    }

    private fun abortSudoku() {
        data.loadmode = false
        timer.stopTimer()
        endCardDialog.show(false, 0, DIFFICULTY)
    }

    fun pauseGame(): Boolean {
        if (!pause) {
            sudokuGrid.visibility = View.INVISIBLE
            timer.stopTimer()
        } else {
            sudokuGrid.visibility = View.VISIBLE
            timer.startTimer()
        }

        pause = !pause
        keyboard.pauseMode(pause)

        return pause
    }

    fun share(): String {
        try {
            return share(game, this)
        } catch (e: Exception) {
            CustomToast(this, resources.getString(R.string.error_default)).show()
        }
        return ""
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0 && resultCode == 1) recreate()
    }

    override fun timeUpdate(time: Int) {
        runOnUiThread { statusBar.setTime(time) }
    }

    private external fun createSudokuNative(freeFields: Int): Sudoku

    companion object {
        // TODO: make this non static?
        @JvmField var MAX_ERROR = 3
        var DIFFICULTY = Difficulty.BEGINNER
        var LOAD_MODE = false
        var SHARED = false

        private val partnerBlockLookup = arrayOf(
            intArrayOf(1, 2, 3, 6),
            intArrayOf(0, 2, 4, 7),
            intArrayOf(0, 1, 5, 8),
            intArrayOf(4, 5, 0, 6),
            intArrayOf(3, 5, 1, 7),
            intArrayOf(3, 4, 2, 8),
            intArrayOf(7, 8, 0, 3),
            intArrayOf(6, 8, 1, 4),
            intArrayOf(6, 7, 2, 5)
        )

        @JvmStatic
        fun getPrimaryColor(context: Context): Int {
            val typedValue = TypedValue()
            context.theme.resolveAttribute(R.attr.colorPrimary, typedValue, true)

            return typedValue.data
        }

        // JNI
        init {
            System.loadLibrary("generator-jni")
        }
    }
}