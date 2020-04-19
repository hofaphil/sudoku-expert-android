package com.aol.philipphofer.logic;


public class Position {

    public int row, column, parent;

    public Position(int row, int column, int parent) {
        this.row = row;
        this.column = column;
        this.parent = parent;
    }

    @Override
    public String toString() {
        return "Position in block[" + parent + "] and row = " + row + " column = " + column;
    }

    @Override
    public boolean equals(Object o) {
        if(!o.getClass().equals(this.getClass()))
            return false;
        Position p = (Position) o;
        return p.parent == this.parent && p.row == this.row && p.column == this.column;
    }
}
