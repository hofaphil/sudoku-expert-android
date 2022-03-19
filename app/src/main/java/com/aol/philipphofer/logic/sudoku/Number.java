package com.aol.philipphofer.logic.sudoku;

import com.aol.philipphofer.persistence.Data;

import java.util.Arrays;
import java.util.Observable;

public class Number extends Observable {

    private int number;
    private int solution;
    private final boolean[] notes;

    private final boolean isChangeable;
    private boolean isNotes;

    public Number() {
        number = 0;
        solution = 0;

        notes = new boolean[9];
        Arrays.fill(notes, Boolean.FALSE);

        isChangeable = true;
        isNotes = false;
    }

    public Number(int number, int solution, boolean isChangeable) {
        this.number = number;
        this.solution = solution;

        this.notes = new boolean[9];
        Arrays.fill(this.notes, Boolean.FALSE);

        this.isChangeable = isChangeable;
        this.isNotes = false;
    }

    public int getNumber() {
        return number;
    }

    public boolean[] getNotes() {
        return notes;
    }

    public int getSolution() {
        return solution;
    }

    public void setSolution(int solution) {
        this.solution = solution;
    }

    // changing methods
    public boolean insert(int number, boolean note) {
        if (!isChangeable())
            return true;

        if (note)
            insertNote(number);
        else
            insertNumber(number);
        return !isError();
    }

    private void insertNumber(int number) {
        if (number == this.number)
            delete();
        else {
            if (this.isNotes)
                delete();
            this.isNotes = false;
            this.number = number;
            setChanged();
            notifyObservers();
        }
    }

    private void insertNote(int number) {
        this.number = 0;
        this.isNotes = true;
        this.notes[number - 1] = !this.notes[number - 1];
        setChanged();
        notifyObservers();
    }

    public void delete() {
        if (isChangeable()) {
            number = 0;
            for (int i = 0; i < 9; i++) {
                notes[i] = false;
            }
            setChanged();
            notifyObservers();
        }
    }

    public void checkNote(int number) {
        if (this.notes[number - 1]) {
            this.notes[number - 1] = false;
            setChanged();
            notifyObservers();
        }
    }

    // flags
    public boolean isChangeable() {
        return isChangeable;
    }

    public boolean isNotes() {
        return isNotes;
    }

    public boolean isError() {
        return number != 0 && number != solution;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Number number1 = (Number) o;
        return number == number1.number
                && solution == number1.solution
                && isChangeable == number1.isChangeable
                && isNotes == number1.isNotes
                && Arrays.equals(notes, number1.notes);
    }

    @Override
    public String toString() {
        return "Number{" +
                "number=" + number +
                ", solution=" + solution +
                ", notes=" + Arrays.toString(notes) +
                ", isChangeable=" + isChangeable +
                ", isNotes=" + isNotes +
                '}';
    }
}

