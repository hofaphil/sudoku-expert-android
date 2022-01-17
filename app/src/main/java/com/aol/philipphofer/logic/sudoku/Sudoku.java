package com.aol.philipphofer.logic.sudoku;

import com.aol.philipphofer.logic.Position;

import java.util.HashMap;
import java.util.Map;
import java.util.Observable;

public class Sudoku {

    private Block[] blocks;
    public int overallErrors = 0;

    public Sudoku() {
        blocks = new Block[9];

        for (int i = 0; i < 9; i++)
            blocks[i] = new Block();
    }

    public void insert(int number, Position position, boolean note) {
        if (blocks[position.block].insert(number, position, note))
            overallErrors++;
    }

    public void delete(Position position) {
        blocks[position.block].delete(position);
    }

    public int freeFields() {
        int freeFields = 0;
        for (int k = 0; k < 9; k++)
            for (int i = 0; i < 3; i++)
                for (int j = 0; j < 3; j++)
                    if (getNumber(new Position(i, j, k)).getNumber() == 0)
                        freeFields++;
        return freeFields;
    }

    public int currentErrors() {
        int currentErrors = 0;
        for (int k = 0; k < 9; k++)
            for (int i = 0; i < 3; i++)
                for (int j = 0; j < 3; j++)
                    if (getNumber(new Position(i, j, k)).isError())
                        currentErrors++;
        return currentErrors;
    }

    public Number getNumber(Position position) {
        return blocks[position.block].getNumber(position.row, position.column);
    }

    // TODO do we need all this?
    public Block[] getSudoku() {
        return blocks;
    }

    public void setSudoku(Block[] blocks) {
        blocks = blocks;
    }

    public int[][][] getSolution() {
        int[][][] solution = new int[9][3][3];
        for (int k = 0; k < 9; k++) {
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    solution[k][i][j] = blocks[k].getNumbers()[i][j].getSolution();
                }
            }
        }
        return solution;
    }

    public void setSolution(int[][][] solution) {
        for (int k = 0; k < 9; k++) {
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    blocks[k].getNumbers()[i][j].setSolution(solution[k][i][j]);
                }
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 9; i = i + 3) {
            for (int k = 0; k < 3; k++) {
                for (int j = i; j < i + 3; j++) {
                    for (int a = 0; a < 3; a++)
                        sb.append(blocks[j].getNumbers()[k][a].getNumber());
                    sb.append(" ");
                }
                sb.append("\n");
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}
