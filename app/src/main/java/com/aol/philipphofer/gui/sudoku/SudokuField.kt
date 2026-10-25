package com.aol.philipphofer.gui.sudoku

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import android.util.TypedValue
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
    private var onSelectedCell = false

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

        val style = if (number.isChangeable) Typeface.BOLD_ITALIC else Typeface.BOLD
        numberView.setTypeface(Typeface.create(numberView.typeface, style))
        updateTextColor()
    }

    // the grid takes the screen width, so the digits scale with the field
    override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int) {
        super.onSizeChanged(width, height, oldWidth, oldHeight)
        updateTextSize()
    }

    private fun updateTextSize() {
        numberView.setTextSize(TypedValue.COMPLEX_UNIT_PX, height * NUMBER_SCALE)
        for (note in notes)
            note.setTextSize(TypedValue.COMPLEX_UNIT_PX, height * NOTE_SCALE)
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
        val color = if (error && showErrors) R.color.cell_error else R.color.cell_background
        setCellColor(color)
    }

    fun select(select: Boolean) {
        val showErrors = Data.instance(mainActivity).loadBoolean(GAME_SHOW_ERRORS)
        if (showErrors && number.isError) return

        val color = if (select) R.color.cell_selected else R.color.cell_background
        setCellColor(color, select)
    }

    fun lightSelect(select: Boolean) {
        val showErrors = Data.instance(mainActivity).loadBoolean(GAME_SHOW_ERRORS)
        if (showErrors && number.isError) return

        val color = if (select) R.color.cell_highlight else R.color.cell_background
        setCellColor(color)
    }

    fun numberSelect(select: Boolean) {
        val showErrors = Data.instance(mainActivity).loadBoolean(GAME_SHOW_ERRORS)
        if (showErrors && number.isError) return

        val color = if (select) R.color.cell_same_number else R.color.cell_background
        setCellColor(color)
    }

    private fun setCellColor(color: Int, selected: Boolean = false) {
        setBackgroundColor(ContextCompat.getColor(context, color))
        if (onSelectedCell != selected) {
            onSelectedCell = selected
            updateTextColor()
        }
    }

    // text on the selected cell needs its own colors to stay readable on cell_selected
    private fun updateTextColor() {
        val numberColor = when {
            number.isChangeable && onSelectedCell -> R.color.number_user_selected
            number.isChangeable -> R.color.number_user
            onSelectedCell -> R.color.number_given_selected
            else -> R.color.number_given
        }
        numberView.setTextColor(ContextCompat.getColor(context, numberColor))

        val notesColor = if (onSelectedCell) R.color.number_given_selected else R.color.number_given
        for (note in notes)
            note.setTextColor(ContextCompat.getColor(context, notesColor))
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
            updateTextColor()
            updateTextSize()
        }
        return notesLayout!!
    }

    companion object {
        // text size relative to the field height
        private const val NUMBER_SCALE = 0.6f
        private const val NOTE_SCALE = 0.25f
    }
}