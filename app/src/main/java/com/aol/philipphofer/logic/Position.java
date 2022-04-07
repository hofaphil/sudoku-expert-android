package com.aol.philipphofer.logic;

public class Position {

    public int row, column, block;

    public Position(int block, int row, int column) {
        this.row = row;
        this.column = column;
        this.block = block;
    }

    @Override
    public String toString() {
        return "[" + block + "][" + row + "][" + column + "]";
    }

    @Override
    public boolean equals(Object o) {
        if(!o.getClass().equals(this.getClass()))
            return false;
        Position p = (Position) o;
        return p.block == this.block && p.row == this.row && p.column == this.column;
    }
}
