package com.aol.philipphofer.sudoku;

public class Sudoku {

    private Block[] blocks;     // The sudoku to be filled out
    private Block[] game;       // The current game

    public Sudoku() {
        blocks = new Block[9];
        game = new Block[9];

        initBlock(blocks);
        initBlock(game);
    }

    private void initBlock(Block[] block) {
        for (int i = 0; i < 9; i++)
            block[i] = new Block();
    }

    // TODO init game!

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

    public Block[] getSudoku() {
        return this.blocks;
    }

    public void setSudoku(Block[] blocks) {
        this.blocks = blocks;
    }

    public Block[] getGame() {
        return this.game;
    }

    public void setGame(Block[] blocks) {
        this.game = blocks;
    }

    public void printBlocks(Block[] blocks) {
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
