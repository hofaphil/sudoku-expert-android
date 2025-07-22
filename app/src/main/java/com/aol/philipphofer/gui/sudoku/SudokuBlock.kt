package com.aol.philipphofer.gui.sudoku

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.GridLayout
import com.aol.philipphofer.R
import com.aol.philipphofer.logic.Position
import com.aol.philipphofer.logic.sudoku.Block

class SudokuBlock(context: Context, attributeSet: AttributeSet) :
    GridLayout(context, attributeSet) {

    val fields: Array<Array<SudokuField>>

    init {
        LayoutInflater.from(context).inflate(R.layout.sudoku_block, this)
        layoutParams = LayoutParams(spec(UNDEFINED, 1f), spec(UNDEFINED, 1f))

        fields = Array(3) { row ->
            Array(3) { col ->
                val id = context.resources.getIdentifier("btn$row$col", "id", context.packageName)
                findViewById(id)
            }
        }
    }

    fun init(block: Block, parent: Int) {
        for (row in 0..2)
            for (col in 0..2)
                fields[row][col].init(block.numbers[row][col], Position(parent, row, col))
    }
}