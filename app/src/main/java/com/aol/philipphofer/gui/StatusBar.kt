package com.aol.philipphofer.gui

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MenuItem
import android.widget.ImageButton
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.widget.PopupMenu
import com.aol.philipphofer.R
import com.aol.philipphofer.gui.custom.CustomToast
import com.aol.philipphofer.logic.MainActivity
import com.aol.philipphofer.logic.Timer
import com.aol.philipphofer.logic.help.Difficulty
import com.aol.philipphofer.persistence.Data

class StatusBar(context: Context, attributeSet: AttributeSet) :
    RelativeLayout(context, attributeSet) {

    private val moreButton: ImageButton
    private val newButton: ImageButton

    private val timeView: TextView
    private val errorView: TextView
    private val difficultyView: TextView

    private val mainActivity: MainActivity

    init {
        LayoutInflater.from(context).inflate(R.layout.sudoku_statusbar, this)

        mainActivity = context as MainActivity;

        newButton = findViewById(R.id.newButton)
        val newPopup = PopupMenu(context, newButton)
        newPopup.inflate(R.menu.popup_new)
        newPopup.setForceShowIcon(true)
        newPopup.setOnMenuItemClickListener { item -> newPopupHandler(item) }
        newButton.setOnClickListener { newPopup.show() }

        moreButton = findViewById(R.id.moreButton)
        val morePopup = PopupMenu(context, moreButton)
        morePopup.inflate(R.menu.popup_more)
        newPopup.setForceShowIcon(true)
        newPopup.setOnMenuItemClickListener { item -> morePopupHandler(item) }
        moreButton.setOnClickListener { morePopup.show() }

        timeView = findViewById(R.id.timeView)
        difficultyView = findViewById(R.id.difficultyView)
        errorView = findViewById(R.id.errorView)
    }

    private fun morePopupHandler(item: MenuItem): Boolean {
        val intent: Intent
        when (item.itemId) {
            R.id.popup_statistics -> {
                intent = Intent(mainActivity, Statistics::class.java)
                mainActivity.startActivityForResult(intent, 0)
            }

            R.id.popup_challenge -> mainActivity.share()

            R.id.popup_settings -> {
                intent = Intent(mainActivity, Settings::class.java)
                mainActivity.startActivityForResult(intent, 0)
            }

            R.id.popup_rate -> {
                val uri = Uri.parse("market://details?id=" + context.packageName)
                intent = Intent(Intent.ACTION_VIEW, uri)
                try {
                    context.startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    CustomToast(context, resources.getString(R.string.error_default)).show();
                }
            }

            else -> return false
        }
        return true
    }

    private fun newPopupHandler(item: MenuItem): Boolean {
        val difficultyNumber = when (item.itemId) {
            R.id.popup_advanced -> Difficulty.ADVANCED.number
            R.id.popup_expert -> Difficulty.EXPERT.number
            else -> Difficulty.BEGINNER.number
        }

        Data.instance(mainActivity).saveInt(Data.GAME_DIFFICULTY, difficultyNumber)
        Data.instance(mainActivity).loadmode = false

        mainActivity.onResume()
        return true
    }

    fun setDifficulty(difficulty: Difficulty) {
        difficultyView.text = difficulty.getText(context)
    }

    fun setError(error: Int) {
        val showErrors = Data.instance(mainActivity).loadBoolean(Data.GAME_SHOW_ERRORS)

        if (showErrors) errorView.text =
            resources.getString(R.string.statusbar_errors, error, MainActivity.MAX_ERROR)
        else errorView.text = resources.getString(R.string.statusbar_errors_not_enabled)
    }

    fun setTime(time: Int) {
        val showTime = Data.instance(mainActivity).loadBoolean(Data.GAME_SHOW_TIME)

        if (showTime) timeView.text = Timer.timeToString(time)
        else timeView.text = "--:--"
    }

    fun activate(activate: Boolean) {
        moreButton.isEnabled = activate
        newButton.isEnabled = activate;
    }
}