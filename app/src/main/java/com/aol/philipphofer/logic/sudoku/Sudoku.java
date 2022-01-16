package com.aol.philipphofer.logic.sudoku;

import com.aol.philipphofer.logic.Position;

import java.util.Observable;

public class Sudoku {

    private Block[] blocks;

    // TODO
    public int freeFields;
    public int currentErrors;
    public int overallErrors;

    public Sudoku() {
        blocks = new Block[9];

        for (int i = 0; i < 9; i++)
            blocks[i] = new Block();
    }

    public void insert(int number, Position position, boolean note) {
        blocks[position.block].insert(number, position, note);
    }

    public void delete(Position position) {
        blocks[position.block].delete(position);
    }

    public Number getNumber(Position position) {
        return this.blocks[position.block].getNumber(position.row, position.column);
    }


    // TODO do we need all this?
    public Block[] getSudoku() {
        return this.blocks;
    }

    public void setSudoku(Block[] blocks) {
        this.blocks = blocks;
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

    public static void printBlocks(Block[] blocks) {
        for (int i = 0; i < 9; i = i + 3) {
            for (int k = 0; k < 3; k++) {
                for (int j = i; j < i + 3; j++) {
                    for (int a = 0; a < 3; a++)
                        System.out.print(blocks[j].getNumbers()[k][a].getNumber());
                    System.out.print(" ");
                }
                System.out.println();
            }
            System.out.println();
        }
    }
}
