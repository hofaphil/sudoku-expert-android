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

    public Block(Block block) {
        this();
        setNumbers(block.getNumbers());
    }

    public Number insert(int number, Position position, boolean note) {
        return this.numbers[position.row][position.column].insert(number, note);
    }

    public Number delete(Position position) {
        return this.numbers[position.row][position.column].delete();
    }

    // TODO do we need all this?
    public Number[][] getNumbers() {
        return this.numbers;
    }

    public Number getNumber(int row, int column) {
        return this.numbers[row][column];
    }

    public void setNumbers(Number[][] numbers) {
        for (int i = 0; i < 3; i++)
            System.arraycopy(numbers[i], 0, this.numbers[i], 0, 3);
    }

    public void setNumber(Number number, int row, int col) {
        this.numbers[row][col] = number;
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
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++)
                sb.append(numbers[i][j]).append(" ");
            sb.append("\n");
        }
        return sb.toString();
    }
}
