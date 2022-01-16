package com.aol.philipphofer.logic.sudoku;

import java.util.Arrays;
import java.util.Observable;

public class Number extends Observable {

    private int number;
    private int solution;
    private final boolean[] notes;

    private final boolean isChangeable;
    private boolean isNotes;
    private boolean isError;

    public Number() {
        this.number = 0;
        this.solution = 0;

        this.notes = new boolean[9];
        Arrays.fill(this.notes, Boolean.FALSE);

        this.isChangeable = true;
        this.isNotes = false;
        this.isError = false;
    }

    public Number(int number, int solution, boolean isChangeable) {
        this.number = number;
        this.solution = solution;

        this.notes = new boolean[9];
        Arrays.fill(this.notes, Boolean.FALSE);

        this.isChangeable = isChangeable;
        this.isNotes = false;
        this.isError = false;
    }

    public Number(int number, int solution, boolean[] notes, boolean isChangeable, boolean isNotes, boolean isError) {
        this.number = number;
        this.solution = solution;

        this.notes = new boolean[9];
        System.arraycopy(notes, 0, this.notes, 0, 9);

        this.isChangeable = isChangeable;
        this.isNotes = isNotes;
        this.isError = isError;
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
    public Number insert(int number, boolean note) {
        if (!isChangeable())
            return this;

        if (note)
            return this.insertNote(number);
        else
            return this.insertNumber(number);
    }

    private Number insertNumber(int number) {
        if (number == this.number)
            return delete();
        else {
            if (isNotes)
                delete();
            this.isNotes = false;
            this.isError = number != this.solution;
            this.number = number;
            setChanged();
            this.notifyObservers();
            return this;
        }
    }

    private Number insertNote(int number) {
        this.number = 0;
        this.isNotes = true;
        this.isError = false;
        notes[number - 1] = !notes[number - 1];
        setChanged();
        this.notifyObservers();
        return this;
    }

    public Number delete() {
        if (isChangeable()) {
            this.number = 0;
            this.isError = false;
            // TODO maybe: this.isNotes = false;
            for (int i = 0; i < 9; i++) {
                notes[i] = false;
            }
            setChanged();
            this.notifyObservers();
        }
        return this;
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
        return this.isError;
    }
}

