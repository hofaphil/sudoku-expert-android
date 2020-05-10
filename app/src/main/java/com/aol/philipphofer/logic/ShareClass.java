package com.aol.philipphofer.logic;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.aol.philipphofer.R;
import com.aol.philipphofer.gui.custom.CustomToast;
import com.aol.philipphofer.gui.sudoku.SudokuGrid;
import com.aol.philipphofer.sudoku.Block;
import com.aol.philipphofer.sudoku.Sudoku;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.zip.DataFormatException;

class ShareClass {

    private static final String URL = "https://philipphofer.de";

    static void share(Sudoku sudoku, Activity context, SudokuGrid sudokuGrid) throws Exception {

        JSONObject o = new JSONObject();
        o.put("difficulty", MainActivity.DIFFICULTY);

        StringBuilder s = new StringBuilder();
        for (int i = 0; i < 9; i++)
            for (int a = 0; a < 3; a++)
                for (int b = 0; b < 3; b++)
                    s.append(sudoku.getSudoku()[i].getNumbers()[a][b]);

        o.put("sudoku", s.toString());

        StringBuilder sol = new StringBuilder();
        for (int i = 0; i < 9; i++)
            for (int a = 0; a < 3; a++)
                for (int b = 0; b < 3; b++)
                    sol.append(sudoku.getSolution()[i].getNumbers()[a][b]);

        o.put("solution", sol.toString());

        RequestQueue queue = Volley.newRequestQueue(context);

        JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST, URL + "/sudoku",
                o,
                r -> {
                    try {
                        Intent shareIntent = new Intent(Intent.ACTION_SEND);
                        shareIntent.setType("text/plain");
                        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Sudoku");
                        String shareMessage = "Your friend wants to challenge you!\n\n";
                        shareMessage = shareMessage + URL + "/share?id=" + r.getString("id") + "\n";
                        shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                        context.startActivity(Intent.createChooser(shareIntent, "Share your Sudoku"));
                        sudokuGrid.changeBackground(SudokuGrid.BackgroundMode.VISIBLE);
                    } catch (JSONException ignored) {
                        new CustomToast(context, context.getResources().getString(R.string.error_default)).show();
                        sudokuGrid.changeBackground(SudokuGrid.BackgroundMode.VISIBLE);
                    }
                }, e -> {
            new CustomToast(context, context.getResources().getString(R.string.error_default)).show();
            sudokuGrid.changeBackground(SudokuGrid.BackgroundMode.VISIBLE);
        }
        );
        queue.add(postRequest);
    }

    static Sudoku load(Uri uri) throws Exception {
        if (uri == null)
            throw new DataFormatException();

        Sudoku sudoku = new Sudoku(4);

        String id = uri.getQueryParameter("id");

        InputStream input = new URL(URL + "/sudoku/" + id).openStream();
        Map<String, String> data = new Gson().fromJson(new InputStreamReader(input, StandardCharsets.UTF_8),
                new TypeToken<Map<String, String>>() {
                }.getType());

        int difficulty = Integer.parseInt(data.get("difficulty"));
        if (difficulty < 0 || difficulty > 3)
            throw new Exception("Difficulty is wrong!");

        StartActivity.difficulty = difficulty;

        Block[] block = new Block[9];
        int[][] numbers = new int[3][3];
        int k = 0;
        for (int i = 0; i < 9; i++) {
            for (int a = 0; a < 3; a++)
                for (int b = 0; b < 3; b++)
                    numbers[a][b] = Character.getNumericValue(data.get("sudoku").charAt(k++));
            block[i] = new Block();
            block[i].setNumbers(numbers);
        }
        sudoku.setSudoku(block);

        block = new Block[9];
        numbers = new int[3][3];
        k = 0;
        for (int i = 0; i < 9; i++) {
            for (int a = 0; a < 3; a++)
                for (int b = 0; b < 3; b++) {
                    numbers[a][b] = Character.getNumericValue(data.get("solution").charAt(k++));
                }
            block[i] = new Block();
            block[i].setNumbers(numbers);
        }
        sudoku.setSolution(block);

        return sudoku;
    }
}
