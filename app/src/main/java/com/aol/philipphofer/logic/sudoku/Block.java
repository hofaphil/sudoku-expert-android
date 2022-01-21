package com.aol.philipphofer.logic.sudoku;

import com.aol.philipphofer.logic.Position;

public class Block {

    private final Number[][] numbers;

    public Block() {
        numbers = new Number[3][3];
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++)
                numbers[i][j] = new Number();
    }

    public boolean insert(int number, Position position, boolean note) {
        return this.numbers[position.row][position.column].insert(number, note);
    }

    public void delete(Position position) {
        this.numbers[position.row][position.column].delete();
    }

    public Number[][] getNumbers() {
        return this.numbers;
    }

    public Number getNumber(int row, int column) {
        return this.numbers[row][column];
    }

    public void setNumber(int row, int col, Number number) {
        this.numbers[row][col] = number;
    }
}
