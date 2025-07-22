package com.aol.philipphofer.gui.dialog

import android.app.Dialog
import android.widget.Button
import android.widget.TextView
import android.widget.Toolbar
import com.aol.philipphofer.R
import com.aol.philipphofer.logic.MainActivity
import com.aol.philipphofer.logic.Timer
import com.aol.philipphofer.logic.help.Difficulty
import com.aol.philipphofer.persistence.Data

class EndCardDialog(mainActivity: MainActivity) : Dialog(mainActivity) {

    private val mainActivity: MainActivity
    private val noTimeText = "--:--"

    init {
        setContentView(R.layout.dialog_endcard)
        this.mainActivity = mainActivity
    }

    fun show(won: Boolean, time: Int, difficulty: Difficulty) {
        findViewById<Toolbar>(R.id.title).title =
            if (won) mainActivity.resources.getString(R.string.win)
            else mainActivity.resources.getString(R.string.lose)

        findViewById<TextView>(R.id.difficultyInfo).text = difficulty.getText(mainActivity)

        findViewById<TextView>(R.id.bestTimeInfo).text = Timer.timeToString(
            Data.instance(mainActivity).loadInt(Data.STATISTICS_BESTTIME + difficulty.number)
        )

        findViewById<TextView>(R.id.timeInfo).text =
            if (won && Data.instance(mainActivity).loadBoolean(Data.GAME_SHOW_TIME))
                Timer.timeToString(time)
            else noTimeText

        findViewById<Button>(R.id.ok).setOnClickListener { dismiss() }

        show()
    }

    override fun onStop() {
        super.onStop()
        mainActivity.onResume()
    }
}