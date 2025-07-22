package com.aol.philipphofer.logic

class Position(var block: Int, var row: Int, var column: Int) {
    override fun toString(): String {
        return "[$block][$row][$column]"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Position

        if (block != other.block) return false
        if (row != other.row) return false
        if (column != other.column) return false

        return true
    }
}