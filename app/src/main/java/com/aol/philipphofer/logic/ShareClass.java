package com.aol.philipphofer.logic;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import com.aol.philipphofer.R;
import com.aol.philipphofer.logic.help.LinkShorter;
import com.aol.philipphofer.sudoku.Block;
import com.aol.philipphofer.sudoku.Sudoku;

import java.util.zip.DataFormatException;

class ShareClass {

    private static final String URL = "https://philipphofer.de/";

    static void share(Sudoku sudoku, Activity context) {
        StringBuilder id = new StringBuilder();
        id.append(MainActivity.DIFFICULTY);

        for (int i = 0; i < 9; i++)
            for (int a = 0; a < 3; a++)
                for (int b = 0; b < 3; b++)
                    id.append(sudoku.getSudoku()[i].getNumbers()[a][b]);

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Sudoku");
        String shareMessage = context.getResources().getString(R.string.share_description) + "\n\n";
        shareMessage = shareMessage + URL + "share?id=" + LinkShorter.getLink(id.toString()) + "\n";
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
        context.startActivity(Intent.createChooser(shareIntent, context.getResources().getString(R.string.share_title)));
    }

    static Sudoku load(Uri uri) throws Exception {
        if (uri == null)
            throw new DataFormatException();

        Sudoku sudoku = new Sudoku(4);

        String link = uri.getQueryParameter("id");
        assert link != null;
        String id = LinkShorter.getId(link);

        int difficulty = Integer.parseInt(id.charAt(0) + "");
        if (difficulty < 0 || difficulty > 3)
            throw new Exception("Difficulty is wrong!");

        StartActivity.difficulty = difficulty;

        Block[] block = new Block[9];
        int[][] numbers = new int[3][3];
        int k = 1;
        for (int i = 0; i < 9; i++) {
            for (int a = 0; a < 3; a++)
                for (int b = 0; b < 3; b++)
                    numbers[a][b] = Integer.parseInt(id.charAt(k++) + "");
            block[i] = new Block();
            block[i].setNumbers(numbers);
        }
        sudoku.setSudoku(block);

        Block[] solution = sudoku.getSolution(sudoku.getSudoku());
        sudoku.setSolution(solution);

        return sudoku;
    }
}
