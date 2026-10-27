package com.aol.philipphofer.gui

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.GridLayout
import com.aol.philipphofer.R
import com.aol.philipphofer.logic.MainActivity

class Keyboard(context: Context, attributeSet: AttributeSet) :
    GridLayout(context, attributeSet),
    View.OnClickListener {

    private val delButton: Button
    private val notesButton: Button
    private val pauseButton: Button

    private var keys: Array<Button>

    private val mainActivity: MainActivity

    init {
        LayoutInflater.from(context).inflate(R.layout.sudoku_keyboard, this)

        mainActivity = context as MainActivity

        delButton = findViewById(R.id.delete)
        delButton.setOnClickListener { mainActivity.delete() }

        notesButton = findViewById(R.id.notes)
        notesButton.setOnClickListener {
            if (mainActivity.notesMode())
                notesButton.setBackgroundColor(resources.getColor(R.color.key_active))
            else
                notesButton.setBackgroundColor(resources.getColor(R.color.key_background))
        }

        pauseButton = findViewById(R.id.pause)
        pauseButton.setOnClickListener { mainActivity.pauseGame() }

        keys = emptyArray()
        for (i in 0..8) {
            val identifier =
                context.resources.getIdentifier("num" + (i + 1), "id", context.packageName)

            keys += findViewById<Button>(identifier)
            keys[i].tag = i + 1
            keys[i].setOnClickListener(this)
        }
    }

    // the keys share the screen width, so the digits scale with it
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        val keyWidth = findViewById<View>(R.id.keyGrid).measuredWidth / keys.size
        for (key in keys)
            key.setTextSize(TypedValue.COMPLEX_UNIT_PX, keyWidth * KEY_SCALE)
    }

    override fun onClick(view: View) {
        mainActivity.insert(view.tag as Int)
    }

    fun activate(activate: Boolean) {
        for (i in 0..8)
            keys[i].isEnabled = activate

        pauseButton.isEnabled = activate
        delButton.isEnabled = activate
        notesButton.isEnabled = activate
    }

    fun hideKeys(hide: (Int) -> Boolean) {
        for (i in 0..8)
            keys[i].visibility = if (hide(i + 1)) View.INVISIBLE else View.VISIBLE
    }

    fun pauseMode(isPauseMode: Boolean) {
        activate(!isPauseMode)
        pauseButton.isEnabled = true

        val backgroundColor =
            if (isPauseMode) resources.getColor(R.color.key_active)
            else resources.getColor(R.color.key_background)
        pauseButton.setBackgroundColor(backgroundColor)

    }

    companion object {
        // text size relative to the key width
        private const val KEY_SCALE = 0.88f
    }
}
