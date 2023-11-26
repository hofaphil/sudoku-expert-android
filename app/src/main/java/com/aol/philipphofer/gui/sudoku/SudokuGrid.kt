package com.aol.philipphofer.gui.sudoku

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.GridLayout
import android.widget.ProgressBar
import android.widget.RelativeLayout
import com.aol.philipphofer.R
import com.aol.philipphofer.logic.MainActivity
import com.aol.philipphofer.logic.sudoku.Block

class SudokuGrid(context: Context, attributeSet: AttributeSet) :
    RelativeLayout(context, attributeSet) {

    var blocks: Array<SudokuBlock>
    var mainActivity: MainActivity

    var grid: GridLayout
    var layout: RelativeLayout

    private val progressBar: ProgressBar

    init {
        LayoutInflater.from(context).inflate(R.layout.sudoku_grid, this)
        mainActivity = context as MainActivity

        layout = findViewById(R.id.sudokuGrid)
        grid = findViewById(R.id.grid)
        progressBar = findViewById(R.id.progressBar)

        blocks = Array(9) {
            val id = context.resources.getIdentifier("blk$it", "id", context.packageName)
            findViewById(id)
        }
    }

    fun init(blocks: Array<Block>) {
        for (blockNr in 0..8)
            this.blocks[blockNr].init(blocks[blockNr], blockNr)
    }

    fun changeBackground(mode: BackgroundMode) {
        when (mode) {
            BackgroundMode.TRANSPARENT -> {
                progressBar.visibility = INVISIBLE
                grid.visibility = VISIBLE
                visibility = INVISIBLE
            }

            BackgroundMode.LOADING -> {
                progressBar.visibility = VISIBLE
                grid.visibility = INVISIBLE
                visibility = VISIBLE
            }

            else -> {
                progressBar.visibility = INVISIBLE
                grid.visibility = VISIBLE
                visibility = VISIBLE
            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec)
    }

    enum class BackgroundMode {
        TRANSPARENT, VISIBLE, LOADING
    }
}