package com.aol.philipphofer.sudoku;

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
        this.isChangeable = true;

        notes = new boolean[9];
        for (int i = 0; i < 9; i++) {
            notes[i] = false;
        }
        this.isNotes = false;
        this.isError = false;
    }

    public Number(int number, int solution, boolean isChangeable) {
        this.number = number;
        this.solution = solution;
        this.isChangeable = isChangeable;

        this.notes = new boolean[9];
        for (int i = 0; i < 9; i++) {
            notes[i] = false;
        }
        this.isNotes = false;
        this.isError = false;
    }

    public Number(int number, int solution, boolean[] notes, boolean isChangeable, boolean isNotes, boolean isError) {
        this.number = number;
        this.solution = solution;

        this.notes = new boolean[9];
        System.arraycopy(notes, 0, this.notes, 0, 9);
        this.isNotes = isNotes;

        this.isChangeable = isChangeable;
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
    public void insertNumber(int number) {
        if (isChangeable()) {
            System.out.println("insert " + number + " obs count " + this.countObservers());
            this.isNotes = false;
            this.isError = number != this.solution;
            this.number = number;
            setChanged();
            this.notifyObservers();
        }
    }

    public void insertNote(int number) {
        if (isChangeable()) {
            this.isNotes = true;
            this.isError = false;
            notes[number - 1] = true;
            setChanged();
            this.notifyObservers();
        }
    }

    public void deleteNumber() {
        if (isChangeable()) {
            this.number = 0;
            this.isError = false;
            setChanged();
            this.notifyObservers();
        }
    }

    public void deleteNote(int number) {
        if (isChangeable()) {
            this.notes[number] = false;
            setChanged();
            this.notifyObservers();
        }
    }

    public void delete() {
        if (isChangeable()) {
            this.number = 0;
            // TODO maybe: this.isNotes = false;
            for (int i = 0; i < 9; i++) {
                notes[i] = false;
            }
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

