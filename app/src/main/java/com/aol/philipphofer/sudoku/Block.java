package com.aol.philipphofer.sudoku;

public class Block {

    private final int[][] numbers;

    public Block() {
        numbers = new int[3][3];
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++)
                numbers[i][j] = 0;
    }

    public Block(Block block) {
        this();
        setNumbers(block.getNumbers());
    }

    void insert(int number, int row, int column) {
        numbers[row][column] = number;
    }

    boolean delete(int number) {
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++)
                if (numbers[i][j] == number) {
                    numbers[i][j] = 0;
                    return true;
                }
        return false;
    }

    void delete(int number, int row, int column) {
        numbers[row][column] = 0;
    }

    public int[][] getNumbers() {
        return this.numbers;
    }

    public int getNumber(int row, int column) {
        return this.numbers[row][column];
    }

    public void setNumbers(int[][] numbers) {
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++)
                this.numbers[i][j] = numbers[i][j];
    }

    @Override
    public boolean equals(Object o) {
        if (!o.getClass().equals(this.getClass()))
            return false;
        Block b = (Block) o;
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++)
                if (this.numbers[i][j] != b.numbers[i][j])
                    return false;
        return true;
    }

    @Override
    public String toString() {
        StringBuilder ret = new StringBuilder();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++)
                ret.append(numbers[i][j]).append(" ");
            ret.append("\n");
        }
        return ret.toString();
    }
}
