package com.aol.philipphofer.gui.sudoku;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.GridLayout;

import com.aol.philipphofer.R;
import com.aol.philipphofer.logic.Position;
import com.aol.philipphofer.sudoku.Block;

public class SudokuBlock extends GridLayout {

    public SudokuField[][] field;

    public SudokuBlock(Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater.from(context).inflate(R.layout.sudoku_block, this);

        LayoutParams param = new LayoutParams(GridLayout.spec(GridLayout.UNDEFINED, 1f), GridLayout.spec(GridLayout.UNDEFINED, 1f));
        this.setLayoutParams(param);

        field = new SudokuField[3][3];
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++) {
                field[i][j] = findViewById(context.getResources().getIdentifier("btn" + i + j, "id", context.getPackageName()));
                // TODO field[i][j].setBackgroundColor(Color.GRAY);
            }
    }

    /* public void init(Block block, int parent) {
        for (int i = 0; i < 3; i++)
            for(int j = 0; j < 3; j++)
                field[i][j].init(block.getNumbers()[i][j], new Position(i, j, parent));
    } */

    public void init(Block block, int parent) {
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++)
                field[i][j].init(block.getNumbers()[i][j], new Position(i, j, parent));
    }
}
