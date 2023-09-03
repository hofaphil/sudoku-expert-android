package com.aol.philipphofer.logic.sudoku

import com.aol.philipphofer.logic.Position

class Block {
    val numbers: Array<Array<Number>> = Array(3) { Array(3) { Number() } }

    fun insert(number: Int, position: Position, note: Boolean): Boolean {
        return numbers[position.row][position.column].insert(number, note)
    }

    fun delete(position: Position) {
        numbers[position.row][position.column].delete()
    }

    fun getNumber(row: Int, column: Int): Number {
        return numbers[row][column]
    }

    fun setNumber(row: Int, col: Int, number: Number) {
        numbers[row][col] = number
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Block

        return numbers.contentDeepEquals(other.numbers)
    }
}