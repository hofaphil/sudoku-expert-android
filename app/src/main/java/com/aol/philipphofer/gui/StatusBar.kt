package com.aol.philipphofer.gui

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageButton
import android.widget.RelativeLayout
import android.widget.TextView
import com.aol.philipphofer.R
import com.aol.philipphofer.logic.MainActivity
import com.aol.philipphofer.logic.Timer
import com.aol.philipphofer.logic.help.Difficulty
import com.aol.philipphofer.persistence.Data

class StatusBar(context: Context, attributeSet: AttributeSet) :
    RelativeLayout(context, attributeSet) {

    private val timeView: TextView
    private val difficultyView: TextView
    private val gameProgress: GameProgress

    private val mainActivity: MainActivity

    init {
        LayoutInflater.from(context).inflate(R.layout.sudoku_statusbar, this)

        mainActivity = context as MainActivity;

        // back to the home screen, the game is saved when the activity pauses
        findViewById<ImageButton>(R.id.backButton).setOnClickListener { mainActivity.finish() }

        timeView = findViewById(R.id.timeView)
        difficultyView = findViewById(R.id.difficultyView)
        gameProgress = findViewById(R.id.gameProgress)
    }

    fun setDifficulty(difficulty: Difficulty) {
        difficultyView.text = difficulty.getText(context)
    }

    fun setError(error: Int) {
        gameProgress.setError(error)
    }

    fun setProgress(freeFields: Int) {
        gameProgress.setProgress(freeFields)
    }

    fun setTime(time: Int) {
        val showTime = Data.instance(mainActivity).loadBoolean(Data.GAME_SHOW_TIME)

        if (showTime) timeView.text = Timer.timeToString(time)
        else timeView.text = Timer.NO_TIME
    }
}
