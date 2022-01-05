package com.aol.philipphofer.sudoku;

public class Sudoku {

    private Block[] blocks;
    private Block[] solution;

    public Sudoku() {
        blocks = new Block[9];
        solution = new Block[9];

        initBlock(blocks);
        initBlock(solution);
    }

    private void initBlock(Block[] block) {
        for (int i = 0; i < 9; i++)
            block[i] = new Block();
    }

    public Block[] getSolution() {
        return this.solution;
    }

    public void setSolution(Block[] blocks) {
        this.solution = blocks;
    }

    public Block[] getSudoku() {
        return this.blocks;
    }

    public void setSudoku(Block[] blocks) {
        this.blocks = blocks;
    }

    public void printBlocks(Block[] blocks) {
        for (int i = 0; i < 9; i = i + 3) {
            for (int k = 0; k < 3; k++) {
                for (int j = i; j < i + 3; j++) {
                    for (int a = 0; a < 3; a++)
                        System.out.print(blocks[j].getNumbers()[k][a]);
                    System.out.print(" ");
                }
                System.out.println();
            }
            System.out.println();
        }
    }
}
