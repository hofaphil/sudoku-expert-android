package com.aol.philipphofer.gui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import com.aol.philipphofer.R
import com.aol.philipphofer.gui.custom.CustomActivity
import com.aol.philipphofer.logic.Timer
import com.aol.philipphofer.logic.help.Difficulty
import com.aol.philipphofer.persistence.Data.*
import com.aol.philipphofer.persistence.Data.Constants.STATISTICS_BESTTIME
import com.aol.philipphofer.persistence.Data.Constants.STATISTICS_TIMEOVERALL
import com.aol.philipphofer.persistence.Data.Constants.STATISTICS_TIMESPLAYED

class Statistics : CustomActivity() {

    private lateinit var beginnerTime: TextView
    private lateinit var advancedTime: TextView
    private lateinit var expertTime: TextView

    private lateinit var beginnerPlayed: TextView
    private lateinit var advancedPlayed: TextView
    private lateinit var expertPlayed: TextView

    private lateinit var beginnerBest: TextView
    private lateinit var advancedBest: TextView
    private lateinit var expertBest: TextView

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_statistics)

        setActionBar(findViewById(R.id.title))

        beginnerTime = findViewById(R.id.Beginner_Average_Time)
        advancedTime = findViewById(R.id.Advanced_Average_Time)
        expertTime = findViewById(R.id.Expert_Average_Time)

        beginnerPlayed = findViewById(R.id.Beginner_TimesPlayed_Count)
        advancedPlayed = findViewById(R.id.Advanced_TimesPlayed_Count)
        expertPlayed = findViewById(R.id.Expert_TimesPlayed_Count)

        beginnerBest = findViewById(R.id.Beginner_Best_Time)
        advancedBest = findViewById(R.id.Advanced_Best_Time)
        expertBest = findViewById(R.id.Expert_Best_Time)
    }

    override fun onStart() {
        super.onStart()

        beginnerTime.text = loadOverall(Difficulty.BEGINNER)
        advancedTime.text = loadOverall(Difficulty.ADVANCED)
        expertTime.text = loadOverall(Difficulty.EXPERT)

        beginnerPlayed.text = loadTimesPlayed(Difficulty.BEGINNER).toString()
        advancedPlayed.text = loadTimesPlayed(Difficulty.ADVANCED).toString()
        expertPlayed.text = loadTimesPlayed(Difficulty.EXPERT).toString()

        beginnerBest.text = loadBestTime(Difficulty.BEGINNER)
        advancedBest.text = loadBestTime(Difficulty.ADVANCED)
        expertBest.text = loadBestTime(Difficulty.EXPERT)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.home)
            finish()
        return super.onOptionsItemSelected(item)
    }

    private fun loadTimesPlayed(difficulty: Difficulty): Int {
        return data.loadInt(STATISTICS_TIMESPLAYED + difficulty.number)
    }

    private fun loadBestTime(difficulty: Difficulty): String {
        return Timer.timeToString(data.loadInt(STATISTICS_BESTTIME + difficulty.number))
    }

    private fun loadOverall(difficulty: Difficulty): String {
        val timesPlayed: Int = loadTimesPlayed(Difficulty.BEGINNER)
        if (timesPlayed == 0) return "00:00"
        return Timer.timeToString(data.loadInt(STATISTICS_TIMEOVERALL + difficulty.number) / timesPlayed)
    }
}