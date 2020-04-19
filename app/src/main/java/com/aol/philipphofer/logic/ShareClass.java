package com.aol.philipphofer.logic;

import android.content.Context;
import android.net.Uri;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.aol.philipphofer.sudoku.Block;
import com.aol.philipphofer.sudoku.Sudoku;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.zip.DataFormatException;

class ShareClass {


    static void share(Sudoku sudoku, Context context) throws Exception {
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

        JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST, "http://192.168.2.102:8080",
                o,
                r -> {
                },
                e -> {
                });

        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(postRequest);
    }

    static Sudoku load(Uri uri) throws Exception {
        if (uri == null)
            throw new DataFormatException();

        Sudoku sudoku = new Sudoku(4);

        InputStream input = new URL(uri.toString()).openStream();
        Map<String, String> data = new Gson().fromJson(new InputStreamReader(input, StandardCharsets.UTF_8),
                new TypeToken<Map<String, String>>() {
                }.getType());

        //TODO check if data is correct

        StartActivity.difficulty = Integer.parseInt(data.get("difficulty"));

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
                for (int b = 0; b < 3; b++)
                    numbers[a][b] = Character.getNumericValue(data.get("solution").charAt(k++));
            block[i] = new Block();
            block[i].setNumbers(numbers);
        }
        sudoku.setSolution(block);

        return sudoku;
    }
}
