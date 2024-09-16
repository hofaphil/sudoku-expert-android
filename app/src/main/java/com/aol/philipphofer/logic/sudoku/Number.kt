package com.aol.philipphofer.logic.sudoku

import com.aol.philipphofer.logic.observer.Observable


class Number : Observable<Number> {

    var number: Int
        private set
    var solution: Int
    val notes: BooleanArray

    // flags
    val isChangeable: Boolean
    var isNotes: Boolean
        private set

    constructor() : this(0, 0, BooleanArray(9), false, true)

    constructor(number: Int, solution: Int, isChangeable: Boolean) : this(
        number,
        solution,
        BooleanArray(9),
        false,
        isChangeable
    )

    constructor(
        number: Int,
        solution: Int,
        notes: BooleanArray,
        isNotes: Boolean,
        isChangeable: Boolean
    ) {
        this.number = number
        this.solution = solution
        this.isChangeable = isChangeable
        this.isNotes = isNotes
        this.notes = notes
    }

    // mutation methods
    fun insert(number: Int, note: Boolean): Boolean {
        if (!isChangeable) return true
        if (note) insertNote(number) else insertNumber(number)
        return !isError
    }

    private fun insertNumber(number: Int) {
        if (number == this.number) delete()
        else {
            if (isNotes) delete()
            isNotes = false
            this.number = number
            notifyListeners()
        }
    }

    private fun insertNote(number: Int) {
        this.number = 0
        isNotes = true
        notes[number - 1] = !notes[number - 1]
        notifyListeners()
    }

    fun delete() {
        if (isChangeable) {
            number = 0
            for (i in 0..8)
                notes[i] = false

            notifyListeners()
        }
    }

    fun checkNote(number: Int) {
        if (notes[number - 1]) {
            notes[number - 1] = false
            notifyListeners()
        }
    }

    val isError: Boolean
        get() = number != 0 && number != solution

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Number

        if (number != other.number) return false
        if (solution != other.solution) return false
        if (!notes.contentEquals(other.notes)) return false
        if (isNotes != other.isNotes) return false
        if (isChangeable != other.isChangeable) return false

        return true
    }

    override fun toString(): String {
        return "Number(number=$number, solution=$solution, notes=${notes.contentToString()}, isChangeable=$isChangeable, isNotes=$isNotes)"
    }

}