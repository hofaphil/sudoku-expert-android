package com.aol.philipphofer.logic.help;

import com.aol.philipphofer.logic.sudoku.Number;

public class NumberSerializer {

    public static String numberToString(Number number) {
        String[] parts = new String[13];

        parts[0] = number.getNumber() + "";
        parts[1] = number.getSolution() + "";
        parts[2] = number.isChangeable() + "";
        parts[3] = number.isNotes() + "";

        for (int i = 0; i < 9; i++) {
            parts[i + 4] = number.getNotes()[i] + "";
        }

        StringBuilder builder = new StringBuilder();
        for(String s : parts) {
            builder.append(s).append(";");
        }
        return builder.toString();
    }

    public static Number numberFromString(String numberString) {
        String[] parts = numberString.split(";");

        int number = Integer.parseInt(parts[0]);
        int solution = Integer.parseInt(parts[1]);
        boolean isChangeable = Boolean.parseBoolean(parts[2]);
        boolean isNotes = Boolean.parseBoolean(parts[3]);
        boolean[] notes = new boolean[9];
        for (int i = 0; i < 9; i++)
            notes[i] = Boolean.parseBoolean(parts[i + 4]);

        return new Number(number, solution, notes, isNotes, isChangeable);
    }
}
