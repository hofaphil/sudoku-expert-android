package com.aol.philipphofer.gui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.aol.philipphofer.R
import com.aol.philipphofer.gui.custom.CustomActivity
import com.aol.philipphofer.gui.custom.CustomToast
import com.aol.philipphofer.logic.MainActivity
import com.aol.philipphofer.logic.ShareClass
import com.aol.philipphofer.logic.Timer
import com.aol.philipphofer.logic.help.Difficulty
import com.aol.philipphofer.persistence.Data

/**
 * Shown after a game is won or lost, replaces the game screen. Back leads home.
 */
class GameResult : CustomActivity() {

    private lateinit var difficulty: Difficulty

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        val won = intent.getBooleanExtra(EXTRA_WON, false)
        val time = intent.getIntExtra(EXTRA_TIME, 0)
        val errors = intent.getIntExtra(EXTRA_ERRORS, 0)
        val newBest = intent.getBooleanExtra(EXTRA_NEW_BEST, false)
        difficulty = Difficulty.getDifficulty(intent.getIntExtra(EXTRA_DIFFICULTY, 0))

        val showTime = data.loadBoolean(Data.GAME_SHOW_TIME)
        val showErrors = data.loadBoolean(Data.GAME_SHOW_ERRORS)

        // header
        findViewById<ImageView>(R.id.resultIcon)
            .setImageResource(if (won) R.drawable.ic_check else R.drawable.ic_close)
        findViewById<TextView>(R.id.resultTitle)
            .setText(if (won) R.string.result_solved else R.string.result_lost)

        // tiles, time and errors only when they were enabled for this game
        findViewById<TextView>(R.id.resultTime).text = Timer.timeToString(time)
        val statistics = data.statistics(difficulty)
        val best = statistics.bestTime
        findViewById<TextView>(R.id.resultBest).text = Timer.timeOrNone(best)
        if (newBest) findViewById<TextView>(R.id.resultBestLabel).setText(R.string.result_new_best)
        findViewById<TextView>(R.id.resultErrors).text =
            getString(R.string.result_errors_value, errors, MainActivity.MAX_ERROR)
        findViewById<TextView>(R.id.resultSolvedGames).text =
            statistics.solved.toString()
        arrangeTiles(
            listOfNotNull(
                R.id.resultTimeTile.takeIf { showTime },
                R.id.resultBestTile,
                R.id.resultErrorsTile.takeIf { showErrors },
                R.id.resultSolvedTile
            )
        )

        // buttons
        findViewById<View>(R.id.resultPlayAgain).setOnClickListener { startNewGame(difficulty) }

        val next = difficulty.next()
        val nextButton = findViewById<TextView>(R.id.resultNextDifficulty)
        if (next == null) nextButton.visibility = View.GONE
        else {
            nextButton.text = getString(R.string.result_try, next.getText(this))
            nextButton.setOnClickListener { startNewGame(next) }
        }

        // the game is already gone, home is below this screen
        findViewById<View>(R.id.resultDone).setOnClickListener { finish() }
        findViewById<View>(R.id.resultChallenge).setOnClickListener { challenge() }
    }

    // rows of two tiles sharing the width, a single tile in the last row gets the full width
    private fun arrangeTiles(visible: List<Int>) {
        val container = findViewById<LinearLayout>(R.id.resultTiles)
        val tiles = visible.map { findViewById<View>(it) }
        container.removeAllViews()

        val gap = resources.getDimensionPixelSize(R.dimen.result_tile_gap)
        for ((index, pair) in tiles.chunked(2).withIndex()) {
            val row = LinearLayout(this)
            row.orientation = LinearLayout.HORIZONTAL
            val rowParams = LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
            if (index > 0) rowParams.topMargin =
                resources.getDimensionPixelSize(R.dimen.result_tile_gap_vertical)
            container.addView(row, rowParams)

            for ((position, tile) in pair.withIndex()) {
                val params = LinearLayout.LayoutParams(0, MATCH_PARENT, 1f)
                if (pair.size == 2) {
                    if (position == 0) params.marginEnd = gap else params.marginStart = gap
                }
                row.addView(tile, params)
            }
        }
    }

    private fun startNewGame(difficulty: Difficulty) {
        MainActivity.startNewGame(this, difficulty)
        finish()
    }

    // the given numbers of the finished game are still saved, friends get the same puzzle
    private fun challenge() {
        try {
            ShareClass.share(data.loadSudoku(), difficulty, this)
        } catch (e: Exception) {
            CustomToast(this, resources.getString(R.string.error_default)).show()
        }
    }

    companion object {
        private const val EXTRA_WON = "won"
        private const val EXTRA_TIME = "time"
        private const val EXTRA_ERRORS = "errors"
        private const val EXTRA_DIFFICULTY = "difficulty"
        private const val EXTRA_NEW_BEST = "newBest"

        fun intent(
            context: Context, won: Boolean, time: Int, errors: Int,
            difficulty: Difficulty, newBest: Boolean
        ): Intent = Intent(context, GameResult::class.java)
            .putExtra(EXTRA_WON, won)
            .putExtra(EXTRA_TIME, time)
            .putExtra(EXTRA_ERRORS, errors)
            .putExtra(EXTRA_DIFFICULTY, difficulty.number)
            .putExtra(EXTRA_NEW_BEST, newBest)
    }
}
