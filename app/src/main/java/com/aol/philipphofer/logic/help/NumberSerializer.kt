package com.aol.philipphofer.logic.help

import com.aol.philipphofer.logic.sudoku.Number

object NumberSerializer {

    @JvmStatic
    fun numberToString(number: Number): String {
        val parts = Array(13) { _ -> "" }

        parts[0] = number.number.toString()
        parts[1] = number.solution.toString()
        parts[2] = number.isChangeable.toString()
        parts[3] = number.isNotes.toString()

        for (i in 0..8)
            parts[i + 4] = number.notes[i].toString()

        val builder = StringBuilder()
        for (s in parts)
            builder.append(s).append(";")

        return builder.toString()
    }

    @JvmStatic
    fun numberFromString(numberString: String?): Number {
        if (numberString.isNullOrBlank()) return Number()

        val parts = numberString.split(";")

        val number = parts[0].toInt()
        val solution = parts[1].toInt()
        val isChangeable = parts[2].toBoolean()
        val isNotes = parts[3].toBoolean()
        val notes = BooleanArray(9)
        for (i in 0..8)
            notes[i] = parts[i + 4].toBoolean()

        return Number(number, solution, notes, isNotes, isChangeable)
    }
}