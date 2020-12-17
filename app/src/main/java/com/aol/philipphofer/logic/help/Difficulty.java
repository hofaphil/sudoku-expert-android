package com.aol.philipphofer.logic.help;

import android.content.Context;

import com.aol.philipphofer.R;

public enum Difficulty {
    BEGINNER(0, R.string.beginner), ADVANCED(1, R.string.advanced), EXPERT(2, R.string.expert);

    private int number;
    private int text;

    Difficulty(int number, int text) {
        this.number = number;
        this.text = text;
    }

    public String getText(Context context) {
        return context.getResources().getString(text);
    }

    public int getNumber() {
        return number;
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
