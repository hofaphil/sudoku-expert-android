package com.aol.philipphofer.logic.sudoku;

import java.util.Arrays;
import java.util.Observable;

public class Number extends Observable {

    private int number;
    private int solution;
    private final boolean[] notes;

    private final boolean isChangeable;
    private boolean isNotes;

    public Number() {
        this.number = 0;
        this.solution = 0;

        this.notes = new boolean[9];
        Arrays.fill(this.notes, Boolean.FALSE);

        this.isChangeable = true;
        this.isNotes = false;
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
        return this.solution;
    }

    public void setSolution(int solution) {
        this.solution = solution;
    }

    // changing methods
    public boolean insert(int number, boolean note) {
        if (!isChangeable())
            return false;

        if (note)
            this.insertNote(number);
        else
            this.insertNumber(number);
        return this.isError();
    }

    private void insertNumber(int number) {
        if (number == this.number)
            delete();
        else {
            if (isNotes)
                delete();
            this.isNotes = false;
            this.number = number;
            setChanged();
            this.notifyObservers();
        }
    }

    private void insertNote(int number) {
        this.number = 0;
        this.isNotes = true;
        notes[number - 1] = !notes[number - 1];
        setChanged();
        this.notifyObservers();
    }

    public void delete() {
        if (isChangeable()) {
            this.number = 0;
            for (int i = 0; i < 9; i++) {
                notes[i] = false;
            }
            setChanged();
            this.notifyObservers();
        }
    }

    public void checkNote(int number) {
        if (this.notes[number - 1]) {
            this.notes[number - 1] = false;
            setChanged();
            this.notifyObservers();
        }
    }

    // flags
    public boolean isChangeable() {
        return this.isChangeable;
    }

    public boolean isNotes() {
        return this.isNotes;
    }

    public boolean isError() {
        return !(number == 0 || number == solution);
    }
}

