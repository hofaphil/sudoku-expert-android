package com.aol.philipphofer.gui.dialog

import android.widget.Button
import android.widget.TextView
import com.aol.philipphofer.R
import com.aol.philipphofer.gui.custom.CustomDialog
import com.aol.philipphofer.logic.MainActivity
import com.aol.philipphofer.logic.Timer
import com.aol.philipphofer.logic.help.Difficulty
import com.aol.philipphofer.persistence.Data

class EndCardDialog(private val mainActivity: MainActivity) :
    CustomDialog(mainActivity, R.layout.dialog_endcard) {

    private val noTimeText = "--:--"

    fun show(won: Boolean, time: Int, difficulty: Difficulty) {
        setHeader(
            if (won) R.drawable.ic_check else R.drawable.ic_close,
            mainActivity.getString(if (won) R.string.win else R.string.lose),
            difficulty.getText(mainActivity)
        )

        findViewById<TextView>(R.id.bestTimeInfo).text = Timer.timeToString(
            Data.instance(mainActivity).loadInt(Data.STATISTICS_BESTTIME + difficulty.number)
        )

        findViewById<TextView>(R.id.timeInfo).text =
            if (won && Data.instance(mainActivity).loadBoolean(Data.GAME_SHOW_TIME))
                Timer.timeToString(time)
            else noTimeText

        findViewById<Button>(R.id.dialogOk).setOnClickListener { dismiss() }

        show()
    }

    override fun onStop() {
        super.onStop()
        mainActivity.startNewGame()
    }
}