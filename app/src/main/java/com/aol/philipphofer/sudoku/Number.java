package com.aol.philipphofer.sudoku;

public class Number {

    public int block, number;

    public Number(int block, int number) {
        this.block = block;
        this.number = number;
    }

    @Override
    public boolean equals(Object o) {
        if(!o.getClass().equals(this.getClass()))
            return false;
        Number num = (Number) o;
        return num.number == this.number && num.block == this.block;
    }
}

