package com.aol.philipphofer.logic.sudoku

import com.aol.philipphofer.logic.Position
import java.util.Arrays

class Sudoku {
    val sudoku: Array<Block> = Array(9) { Block() }
    var overallErrors = 0

    fun insert(number: Int, position: Position, note: Boolean) {
        if (!sudoku[position.block].insert(number, position, note)) overallErrors++
    }

    fun delete(position: Position) {
        sudoku[position.block].delete(position)
    }

    fun freeFields(): Int {
        var freeFields = 0
        for (k in 0..8)
            for (i in 0..2)
                for (j in 0..2)
                    if (getNumber(Position(k, i, j)).number == 0)
                        freeFields++
        return freeFields
    }

    fun currentErrors(): Int {
        var currentErrors = 0
        for (k in 0..8)
            for (i in 0..2)
                for (j in 0..2)
                    if (getNumber(Position(k, i, j)).isError)
                        currentErrors++
        return currentErrors
    }

    fun getNumber(position: Position): Number {
        return sudoku[position.block].getNumber(position.row, position.column)
    }

    fun setNumber(position: Position, number: Number?) {
        sudoku[position.block].setNumber(position.row, position.column, number!!)
    }

    override fun toString(): String {
        val sb = StringBuilder("Sudoku with $overallErrors overall-errors: \n")
        var i = 0
        while (i < 9) {
            for (k in 0..2) {
                for (j in i until i + 3) {
                    for (a in 0..2)
                        sb.append(sudoku[j].numbers[k][a].number)
                    sb.append(" ")
                }
                sb.append("\n")
            }
            sb.append("\n")
            i += 3
        }
        return sb.toString()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Sudoku

        if (!sudoku.contentEquals(other.sudoku)) return false
        if (overallErrors != other.overallErrors) return false

        return true
    }
}