package com.aol.philipphofer.gui

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.graphics.drawable.ScaleDrawable
import android.util.AttributeSet
import android.view.ContextThemeWrapper
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.widget.PopupMenu
import com.aol.philipphofer.R
import com.aol.philipphofer.gui.custom.CustomToast
import com.aol.philipphofer.logic.MainActivity
import com.aol.philipphofer.logic.Timer
import com.aol.philipphofer.logic.help.Difficulty
import com.aol.philipphofer.persistence.Data
import androidx.core.net.toUri

class StatusBar(context: Context, attributeSet: AttributeSet) :
    RelativeLayout(context, attributeSet) {

    private val moreButton: ImageButton
    private val newButton: ImageButton

    private val timeView: TextView
    private val errorView: LinearLayout
    private val errorDivider: View
    private val progressBar: ProgressBar
    private val progressView: TextView
    private val difficultyView: TextView

    private val mainActivity: MainActivity

    init {
        LayoutInflater.from(context).inflate(R.layout.sudoku_statusbar, this)

        mainActivity = context as MainActivity;

        newButton = findViewById(R.id.newButton)
        val popupContext = ContextThemeWrapper(context, R.style.ThemeOverlay_Sudoku_PopupMenu)

        val newPopup = PopupMenu(popupContext, newButton)
        newPopup.inflate(R.menu.popup_new)
        newPopup.setForceShowIcon(true)
        newPopup.setOnMenuItemClickListener { item -> newPopupHandler(item) }
        newButton.setOnClickListener { newPopup.show() }

        moreButton = findViewById(R.id.moreButton)
        val morePopup = PopupMenu(popupContext, moreButton, Gravity.END)
        morePopup.inflate(R.menu.popup_more)
        morePopup.setForceShowIcon(true)
        morePopup.setOnMenuItemClickListener { item -> morePopupHandler(item) }
        moreButton.setOnClickListener { morePopup.show() }

        timeView = findViewById(R.id.timeView)
        difficultyView = findViewById(R.id.difficultyView)
        errorView = findViewById(R.id.errorView)
        errorDivider = findViewById(R.id.errorDivider)
        progressBar = findViewById(R.id.progressBar)
        // the hearts outline is 2 of 24 units wide, the progress bar borders get the same width
        val outline = resources.getDimensionPixelSize(R.dimen.statusbar_heart_size) / 12
        val outlineColor = context.getColor(R.color.outline)
        (progressBar.progressDrawable.mutate() as LayerDrawable).apply {
            (findDrawableByLayerId(android.R.id.background) as GradientDrawable)
                .setStroke(outline, outlineColor)
            ((findDrawableByLayerId(android.R.id.progress) as ScaleDrawable).drawable as GradientDrawable)
                .setStroke(outline, outlineColor)
        }
        progressView = findViewById(R.id.progressView)
    }

    private fun morePopupHandler(item: MenuItem): Boolean {
        val intent: Intent
        when (item.itemId) {
            R.id.popup_statistics -> {
                intent = Intent(mainActivity, Statistics::class.java)
                mainActivity.startActivity(intent)
            }

            R.id.popup_challenge -> mainActivity.share()

            R.id.popup_settings -> {
                intent = Intent(mainActivity, Settings::class.java)
                mainActivity.startActivity(intent)
            }

            R.id.popup_rate -> {
                val uri = ("market://details?id=" + context.packageName).toUri()
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
        mainActivity.startNewGame()
        return true
    }

    fun setDifficulty(difficulty: Difficulty) {
        difficultyView.text = difficulty.getText(context)
    }

    // one heart per life, lost lives turn black
    fun setError(error: Int) {
        val showErrors = Data.instance(mainActivity).loadBoolean(Data.GAME_SHOW_ERRORS)

        errorView.visibility = if (showErrors) VISIBLE else GONE
        errorDivider.visibility = errorView.visibility
        if (!showErrors) return

        val lives = (MainActivity.MAX_ERROR - error).coerceAtLeast(0)
        errorView.removeAllViews()
        for (i in 0 until MainActivity.MAX_ERROR) {
            val heart = ImageView(context)
            heart.setImageResource(if (i < lives) R.drawable.ic_heart else R.drawable.ic_heart_lost)
            val params = LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
            if (i > 0) params.marginStart = resources.getDimensionPixelSize(R.dimen.statusbar_heart_spacing)
            errorView.addView(heart, params)
        }
    }

    fun setProgress(freeFields: Int) {
        val filled = FIELDS - freeFields
        progressBar.progress = filled
        progressView.text = resources.getString(R.string.statusbar_progress, filled, FIELDS)
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

    companion object {
        private const val FIELDS = 81
    }
}
