package com.aol.philipphofer.gui.sudoku;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.GridLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.aol.philipphofer.R;
import com.aol.philipphofer.logic.MainActivity;
import com.aol.philipphofer.sudoku.Block;


public class SudokuGrid extends RelativeLayout {

    public SudokuBlock[] blocks;
    public MainActivity mainActivity;
    public GridLayout grid;
    public RelativeLayout layout;
    private ProgressBar progressBar;

    public SudokuGrid(Context context, AttributeSet attrs) {
        super(context, attrs);

        mainActivity = (MainActivity) context;

        LayoutInflater.from(context).inflate(R.layout.sudoku_grid, this);

       layout = findViewById(R.id.sudokuGrid);
       grid = findViewById(R.id.grid);
       progressBar = findViewById(R.id.progressBar);

        blocks = new SudokuBlock[9];
        for (int j = 0; j < 9; j++)
            blocks[j] = findViewById(context.getResources().getIdentifier("blk" + j, "id", context.getPackageName()));
    }

    public void init(Block[] block) {
        for (int j = 0; j < 9; j++)
            blocks[j].init(block[j], j);
    }

    public void changeBackground(BackgroundMode mode) {
        progressBar.setVisibility(INVISIBLE);
        grid.setVisibility(VISIBLE);

        switch (mode) {
            case VISIBLE:
                this.setVisibility(VISIBLE);
                break;
            case TRANSPARENT:
                this.setVisibility(INVISIBLE);
                break;
            case LOADING:
                this.setVisibility(VISIBLE);
                grid.setVisibility(INVISIBLE);
                progressBar.setVisibility(VISIBLE);
                break;
            default:
                this.setVisibility(VISIBLE);
        }
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }

    public enum BackgroundMode {
        TRANSPARENT, VISIBLE, LOADING;
    }
}