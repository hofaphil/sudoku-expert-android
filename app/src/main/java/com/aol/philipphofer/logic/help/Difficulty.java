package com.aol.philipphofer.logic.help;

import android.content.Context;

import com.aol.philipphofer.R;

public enum Difficulty {
    BEGINNER(0, R.string.beginner, 42), ADVANCED(1, R.string.advanced, 49), EXPERT(2, R.string.expert, 56);

    private final int number;
    private final int text;

    private final int freeFields;

    Difficulty(int number, int text, int freeFields) {
        this.number = number;
        this.text = text;
        this.freeFields = freeFields;
    }

    public String getText(Context context) {
        return context.getResources().getString(text);
    }

    public int getNumber() {
        return this.number;
    }

    public int getFreeFields() {
        return this.freeFields;
    }

    public static Difficulty getDifficulty(int number) {
        switch (number) {
            case 0:
                return BEGINNER;
            case 2:
                return EXPERT;
            default:
                return ADVANCED;
        }
    }
}
