package com.aol.philipphofer.logic;

public class Position {

    public int row, column, block;

    public Position(int row, int column, int block) {
        this.row = row;
        this.column = column;
        this.block = block;
    }

    @Override
    public String toString() {
        return "Position in block[" + block + "] and row = " + row + " column = " + column;
    }

    @Override
    public boolean equals(Object o) {
        if(!o.getClass().equals(this.getClass()))
            return false;
        Position p = (Position) o;
        return p.block == this.block && p.row == this.row && p.column == this.column;
    }
}
