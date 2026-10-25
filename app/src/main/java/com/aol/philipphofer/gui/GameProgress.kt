package com.aol.philipphofer.gui

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.graphics.drawable.ScaleDrawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import com.aol.philipphofer.R
import com.aol.philipphofer.logic.MainActivity
import com.aol.philipphofer.persistence.Data

/**
 * Box with the progress of the filled fields and the lives left,
 * in the status bar of the game and on the continue card of the home screen.
 */
class GameProgress(context: Context, attributeSet: AttributeSet) :
    LinearLayout(context, attributeSet) {

    private val errorView: LinearLayout
    private val errorDivider: View
    private val progressBar: ProgressBar
    private val progressView: TextView

    init {
        LayoutInflater.from(context).inflate(R.layout.game_progress, this)

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

    // one heart per life, lost lives turn black
    fun setError(error: Int) {
        val showErrors = Data.instance(context).loadBoolean(Data.GAME_SHOW_ERRORS)

        errorView.visibility = if (showErrors) VISIBLE else GONE
        errorDivider.visibility = errorView.visibility
        if (!showErrors) return

        val lives = (MainActivity.MAX_ERROR - error).coerceAtLeast(0)
        errorView.contentDescription = resources.getQuantityString(R.plurals.lives, lives, lives)
        errorView.removeAllViews()
        for (i in 0 until MainActivity.MAX_ERROR) {
            val heart = ImageView(context)
            heart.setImageResource(if (i < lives) R.drawable.ic_heart else R.drawable.ic_heart_lost)
            val params = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
            if (i > 0) params.marginStart = resources.getDimensionPixelSize(R.dimen.statusbar_heart_spacing)
            errorView.addView(heart, params)
        }
    }

    fun setProgress(freeFields: Int) {
        val filled = FIELDS - freeFields
        progressBar.progress = filled
        progressView.text = resources.getString(R.string.statusbar_progress, filled, FIELDS)
    }

    companion object {
        const val FIELDS = 81
    }
}
