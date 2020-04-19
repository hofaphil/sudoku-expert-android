package com.aol.philipphofer.sudoku;

import java.util.Random;

public class Block {

    private int[][] numbers;
    private Block[] rowPartner, columnPartner;
    private boolean[] containsNumber;
    private boolean[][] row, column;
    public int latestDelIndexX = -1, latestDelIndexZ = -1;

    public Block() {
        numbers = new int[3][3];
        rowPartner = new Block[2];
        columnPartner = new Block[2];
        containsNumber = new boolean[10];
        row = new boolean[3][10];
        column = new boolean[3][10];
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++)
                numbers[i][j] = 0;
    }

    public Block(Block block) {
        this();
        setNumbers(block.getNumbers());
    }

    public void setRowPartner(Block block1, Block block2) {
        rowPartner[0] = block1;
        rowPartner[1] = block2;
    }

    public void setColumnPartner(Block block1, Block block2) {
        columnPartner[0] = block1;
        columnPartner[1] = block2;
    }

    public boolean insert(int number, int row, int column) {
        if (numbers[row][column] == 0 && !blockConflict(number) && !lineConflict(number, row, column)) {
            numbers[row][column] = number;
            containsNumber[number] = true;
            this.row[row][number] = true;
            this.column[column][number] = true;
            return true;
        }
        return false;
    }

    public boolean insertWithoutBlockConflict(int number, int row, int column) {
        if (numbers[row][column] == 0 && !lineConflict(number, row, column)) {
            numbers[row][column] = number;
            containsNumber[number] = true;
            this.row[row][number] = true;
            this.column[column][number] = true;
            return true;
        }
        return false;
    }

    void generateRandom() {
        int row, column;
        Random random = new Random();
        for (int i = 1; i < 10; i++) {
            if (numbers[row = random.nextInt(3)][column = random.nextInt(3)] == 0) {
                numbers[row][column] = i;
                containsNumber[i] = true;
                this.row[row][i] = true;
                this.column[column][i] = true;
            } else
                i--;
        }
    }

    private boolean blockConflict(int number) {
        return containsNumber[number];
    }

    private boolean lineConflict(int number, int row, int column) {
        return !(!rowPartner[0].hasInRow(number, row) && !rowPartner[1].hasInRow(number, row) &&
                !columnPartner[0].hasInColumn(number, column) && !columnPartner[1].hasInColumn(number, column));
    }

    private boolean hasInRow(int number, int row) {
        return this.row[row][number];
    }

    private boolean hasInColumn(int number, int column) {
        return this.column[column][number];
    }

    public boolean delete(int number) {
        if (containsNumber[number]) {
            containsNumber[number] = false;
            for (int i = 0; i < 3; i++)
                if (row[i][number])
                    for (int j = 0; j < 3; j++) {
                        if (numbers[i][j] == number) {
                            numbers[i][j] = 0;
                            this.row[i][number] = false;
                            this.column[j][number] = false;
                            latestDelIndexX = i;
                            latestDelIndexZ = j;
                            return true;
                        }
                    }
        }
        return false;
    }

    public void deleteWithPosition(int number, int row, int column) {
        if(containsNumber[number]) {
            containsNumber[number] = false;
            numbers[row][column] = 0;
            this.row[row][number] = false;
            this.column[column][number] = false;
            latestDelIndexX = row;
            latestDelIndexZ = column;
        }
    }

    public int[][] getNumbers() {
        return this.numbers;
    }

    public boolean contains(int number) {
        return containsNumber[number];
    }

    public void setNumbers(int[][] numbers) {
        containsNumber = new boolean[10];
        row = new boolean[3][10];
        column = new boolean[3][10];
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++) {
                this.numbers[i][j] = numbers[i][j];
                containsNumber[numbers[i][j]] = true;
                this.row[i][numbers[i][j]] = true;
                this.column[j][numbers[i][j]] = true;
            }
    }

    @Override
    public boolean equals(Object o) {
        if(!o.getClass().equals(this.getClass()))
            return false;
        Block b = (Block) o;
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++) {
                if (this.numbers[i][j] != b.numbers[i][j]) {
                    return false;
                }
            }
        return true;
    }

    @Override
    public String toString() {
        String ret = "";
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++)
                ret = ret + numbers[i][j] + " ";
            ret = ret + "\n";
        }
        return ret;
    }
}
