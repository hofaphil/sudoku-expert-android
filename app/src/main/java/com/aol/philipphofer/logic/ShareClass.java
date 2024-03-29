package com.aol.philipphofer.logic;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import com.aol.philipphofer.R;
import com.aol.philipphofer.logic.help.LinkShortener;
import com.aol.philipphofer.logic.sudoku.Number;
import com.aol.philipphofer.logic.sudoku.Sudoku;

import java.util.zip.DataFormatException;

class ShareClass {

    // TODO: change to new url
    private static final String URL = "https://philipphofer.de/";

    static String share(Sudoku sudoku, Activity context) {
        StringBuilder id = new StringBuilder();
        id.append(MainActivity.DIFFICULTY.getNumber());

        for (int b = 0; b < 9; b++)
            for (int i = 0; i < 3; i++)
                for (int j = 0; j < 3; j++)
                    id.append(sudoku.getNumber(new Position(b, i, j)).getNumber());

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Sudoku");

        String shareMessage = context.getResources().getString(R.string.share_description);
        String shareLink = URL + "share?id=" + LinkShortener.getLink(id.toString());
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage + "\n\n" + shareLink + "\n");
        context.startActivity(Intent.createChooser(shareIntent, context.getResources().getString(R.string.share_title)));

        return shareLink;
    }

    // TODO: change exceptions
    static Sudoku load(Uri uri) throws Exception {
        if (uri == null)
            throw new DataFormatException();

        String link = uri.getQueryParameter("id");
        // TODO: error handling!
        assert link != null;
        String id = LinkShortener.getId(link);

        int difficulty = Integer.parseInt(id.charAt(0) + "");
        if (difficulty < 0 || difficulty > 3)
            throw new Exception("Difficulty is wrong!");

        // TODO: not nice!
        StartActivity.difficulty = difficulty;

        Sudoku sudoku = new Sudoku();

        int k = 1;
        for (int b = 0; b < 9; b++)
            for (int i = 0; i < 3; i++)
                for (int j = 0; j < 3; j++) {
                    Position p = new Position(b, i, j);
                    int number = Integer.parseInt(id.charAt(k++) + "");
                    Number n = new Number(number, number, number == 0);
                    sudoku.setNumber(p, n);
                }

        solveSudokuNative(sudoku);

        return sudoku;
    }

    // JNI
    static {
        System.loadLibrary("generator-jni");
    }

    public native static void solveSudokuNative(Sudoku sudoku);

}
