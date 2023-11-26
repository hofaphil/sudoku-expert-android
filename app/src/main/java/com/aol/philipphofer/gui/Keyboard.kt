package com.aol.philipphofer.gui

import android.content.Context
import android.util.AttributeSet
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
                notesButton.setBackgroundColor(MainActivity.getPrimaryColor(context))
            else
                notesButton.setBackgroundColor(resources.getColor(R.color.transparent))
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

    fun pauseMode(isPauseMode: Boolean) {
        activate(!isPauseMode)
        pauseButton.isEnabled = true

        val backgroundColor =
            if (isPauseMode) MainActivity.getPrimaryColor(context)
            else resources.getColor(R.color.transparent)
        pauseButton.setBackgroundColor(backgroundColor)

    }

}