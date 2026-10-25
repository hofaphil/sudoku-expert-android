package com.aol.philipphofer.gui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import com.aol.philipphofer.R
import com.aol.philipphofer.gui.custom.CustomActivity
import com.aol.philipphofer.gui.dialog.InfoDialog
import com.aol.philipphofer.logic.MainActivity
import com.aol.philipphofer.logic.Timer
import com.aol.philipphofer.logic.help.Difficulty
import com.aol.philipphofer.persistence.Data

class Home : CustomActivity() {

    private enum class Tab(val page: Int, val button: Int) {
        PLAY(R.id.playPage, R.id.playTab),
        STATISTICS(R.id.statisticsPage, R.id.statisticsTab),
        SETTINGS(R.id.settingsPage, R.id.settingsTab)
    }

    private var tab = Tab.PLAY
    private lateinit var statisticsPage: StatisticsPage
    private lateinit var settingsPage: SettingsPage

    // back leads from the other tabs to the play tab, on the play tab it leaves the app
    private val backToPlay = object : OnBackPressedCallback(false) {
        override fun handleOnBackPressed() = select(Tab.PLAY)
    }

    private lateinit var continueTitle: View
    private lateinit var sharedGame: View
    private lateinit var sharedDifficulty: TextView
    private lateinit var continueGame: View
    private lateinit var continueDifficulty: TextView
    private lateinit var continueTime: TextView
    private lateinit var continueProgress: GameProgress

    private val difficultyCards = mapOf(
        Difficulty.BEGINNER to R.id.newBeginner,
        Difficulty.ADVANCED to R.id.newAdvanced,
        Difficulty.EXPERT to R.id.newExpert
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        continueTitle = findViewById(R.id.continueTitle)
        sharedGame = findViewById(R.id.sharedGame)
        sharedDifficulty = findViewById(R.id.sharedDifficulty)
        findViewById<View>(R.id.sharedPlay).setOnClickListener { MainActivity.startSharedGame(this) }
        findViewById<View>(R.id.sharedDismiss).setOnClickListener {
            data.dismissSharedGame()
            updateContinue()
        }
        continueGame = findViewById(R.id.continueGame)
        continueDifficulty = findViewById(R.id.continueDifficulty)
        continueTime = findViewById(R.id.continueTime)
        continueProgress = findViewById(R.id.continueProgress)
        continueGame.setOnClickListener { startActivity(Intent(this, MainActivity::class.java)) }

        for ((difficulty, id) in difficultyCards) {
            val card = findViewById<View>(id)
            card.findViewById<ImageView>(R.id.difficultyIcon).setImageResource(difficulty.icon)
            card.findViewById<TextView>(R.id.difficultyName).text = difficulty.getText(this)
            card.setOnClickListener { MainActivity.startNewGame(this, difficulty) }
        }

        statisticsPage = StatisticsPage(findViewById(R.id.statisticsPage))
        settingsPage = SettingsPage(findViewById(R.id.settingsPage)) { updatePlay() }

        findViewById<View>(R.id.streakBox).setOnClickListener { showStreakInfo() }

        onBackPressedDispatcher.addCallback(this, backToPlay)
        for (tab in Tab.entries)
            findViewById<View>(tab.button).setOnClickListener { select(tab) }
        // keep the tab when the screen is recreated, e.g. after switching to the dark theme
        select(Tab.entries[savedInstanceState?.getInt(SELECTED_TAB) ?: Tab.PLAY.ordinal])
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(SELECTED_TAB, tab.ordinal)
    }

    override fun onStart() {
        super.onStart()
        updatePlay()
        statisticsPage.update()
    }

    override fun onResume() {
        super.onResume()
        settingsPage.load()
    }

    override fun onPause() {
        super.onPause()
        settingsPage.save()
    }

    private fun select(selected: Tab) {
        if (tab == Tab.SETTINGS && selected != Tab.SETTINGS) settingsPage.save()
        tab = selected

        for (tab in Tab.entries) {
            val isSelected = tab == selected
            findViewById<View>(tab.page).visibility = if (isSelected) View.VISIBLE else View.GONE

            // colored by the Home.Tab style
            findViewById<View>(tab.button).isSelected = isSelected
        }

        when (selected) {
            Tab.PLAY -> updatePlay()
            Tab.STATISTICS -> statisticsPage.update()
            Tab.SETTINGS -> settingsPage.load()
        }
        backToPlay.isEnabled = selected != Tab.PLAY
    }

    private fun updatePlay() {
        updateStreak()
        updateContinue()
        updateBestTimes()
    }

    // days in a row with a solved game, in the top bar
    private fun updateStreak() {
        val streak = data.streak()
        findViewById<TextView>(R.id.streakView).text = streak.toString()
        findViewById<View>(R.id.streakBox).contentDescription =
            resources.getQuantityString(R.plurals.home_streak, streak, streak)
    }

    private fun showStreakInfo() {
        val streak = data.streak()
        InfoDialog(
            this, R.drawable.ic_streak, getString(R.string.home_streak_title),
            resources.getQuantityString(R.plurals.home_streak_days, streak, streak),
            R.string.home_streak_info
        ).show()
    }

    // the continue section shows a game shared by a friend and the running game, each only when there is one
    private fun updateContinue() {
        val shared = data.sharedGameDifficulty()
        sharedGame.visibility = if (shared != null) View.VISIBLE else View.GONE
        shared?.let { sharedDifficulty.text = it.getText(this) }

        continueGame.visibility = if (data.loadmode) View.VISIBLE else View.GONE
        continueTitle.visibility =
            if (shared != null || data.loadmode) View.VISIBLE else View.GONE
        if (!data.loadmode) return

        continueDifficulty.text =
            Difficulty.getDifficulty(data.loadInt(Data.GAME_DIFFICULTY)).getText(this)
        // the time played so far, only when the game runs with the timer
        val showTime = data.loadBoolean(Data.GAME_SHOW_TIME)
        continueTime.visibility = if (showTime) View.VISIBLE else View.GONE
        if (showTime) continueTime.text = Timer.timeToString(data.loadInt(Data.GAME_TIME))
        continueProgress.setProgress(data.loadSudoku().freeFields())
        continueProgress.setError(data.loadInt(Data.GAME_ERRORS))
    }

    private fun updateBestTimes() {
        for ((difficulty, id) in difficultyCards) {
            val best = data.statistics(difficulty).bestTime
            findViewById<View>(id).findViewById<TextView>(R.id.difficultyBest).text =
                getString(R.string.home_best, Timer.timeOrNone(best))
        }
    }

    companion object {
        private const val SELECTED_TAB = "selectedTab"
    }
}
