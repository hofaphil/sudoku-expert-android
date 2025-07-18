package com.aol.philipphofer.gui.sudoku

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewStub
import android.widget.GridLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.aol.philipphofer.R
import com.aol.philipphofer.logic.MainActivity
import com.aol.philipphofer.logic.Position
import com.aol.philipphofer.logic.observer.Listener
import com.aol.philipphofer.logic.observer.Observable
import com.aol.philipphofer.logic.sudoku.Number
import com.aol.philipphofer.persistence.Data
import com.aol.philipphofer.persistence.Data.Constants.GAME_SHOW_ERRORS

class SudokuField(context: Context, attributeSet: AttributeSet) :
    GridLayout(context, attributeSet), OnClickListener, Listener<Number> {

    lateinit var position: Position
    lateinit var number: Number

    private val numberView: TextView
    private var notesLayout: GridLayout? = null
    private var notes: Array<TextView> = emptyArray()

    private val mainActivity: MainActivity = context as MainActivity

    init {
        LayoutInflater.from(context).inflate(R.layout.sudoku_field, this)

        numberView = findViewById(R.id.numberView)
        numberView.setOnClickListener(this)
    }

    fun init(number: Number, position: Position) {
        this.position = position

        this.number = number
        this.number.addListener(this)
        observableChanged(this.number)

        val typeFace = if (number.isChangeable) Typeface.ITALIC else Typeface.BOLD
        numberView.setTypeface(numberView.typeface, typeFace)
    }

    private fun switchLayout(isNotes: Boolean) {
        numberView.visibility = if (isNotes) INVISIBLE else VISIBLE

        if (isNotes) {
            getNotesLayout().visibility = VISIBLE
        } else {
            if (notesLayout != null)
                getNotesLayout().visibility = INVISIBLE
        }

    }

    private fun error(error: Boolean) {
        val showErrors = Data.instance(mainActivity).loadBoolean(GAME_SHOW_ERRORS)
        val color = if (error && showErrors) R.color.error else R.color.unselected

        setBackgroundColor(ContextCompat.getColor(context, color))
    }

    fun select(select: Boolean) {
        val showErrors = Data.instance(mainActivity).loadBoolean(GAME_SHOW_ERRORS)
        if (showErrors && number.isError) return

        if (select)
            this.setBackgroundColor(MainActivity.getPrimaryColor(context));
        else
            this.setBackgroundColor(ContextCompat.getColor(context, R.color.unselected));
    }

    fun lightSelect(select: Boolean) {
        val showErrors = Data.instance(mainActivity).loadBoolean(GAME_SHOW_ERRORS)
        if (showErrors && number.isError) return

        val color = if (select) R.color.lightSelected else R.color.unselected
        setBackgroundColor(ContextCompat.getColor(context, color))
    }

    private fun setNumberViewText(number: Int) {
        val stringValue = if (number != 0) number.toString() else ""
        numberView.text = stringValue
    }

    private fun setNotes(visibleNotes: BooleanArray) {
        for (i in 0..8)
            notes[i].visibility = if (visibleNotes[i]) VISIBLE else INVISIBLE
    }

    override fun onClick(view: View) {
        mainActivity.select(position)
    }

    override fun observableChanged(observable: Observable<Number>) {
        val changedNumber = observable as Number

        error(changedNumber.isError)
        setNumberViewText(changedNumber.number)
        switchLayout(number.isNotes)
        if (null != notesLayout) setNotes(changedNumber.notes)

        Data.instance(context).saveGameNumber(changedNumber, position)
    }

    private fun getNotesLayout(): GridLayout {
        if (null == notesLayout) {
            notesLayout = (findViewById<ViewStub>(R.id.notesGridStub)).inflate() as GridLayout
            notesLayout!!.setOnClickListener(this)

            notes = Array(9) {
                val id = context.resources.getIdentifier("tv$it", "id", context.packageName)
                findViewById(id)
            }
        }
        return notesLayout!!
    }
}