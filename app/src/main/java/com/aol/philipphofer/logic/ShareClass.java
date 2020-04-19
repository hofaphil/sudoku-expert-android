package com.aol.philipphofer.logic;

import android.content.Context;
import android.net.Uri;

import com.aol.philipphofer.sudoku.Block;
import com.aol.philipphofer.sudoku.Sudoku;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.zip.DataFormatException;

class ShareClass {

    static void share(Sudoku sudoku, File file) {
        try (BufferedWriter w = new BufferedWriter(new FileWriter(file))) {
            w.write("$SUDOKU_DOCUMENT" + "\n");
            w.write(MainActivity.DIFFICULTY + "\n");
            for (int i = 0; i < 9; i++) {
                String line = "";
                for (int a = 0; a < 3; a++)
                    for (int b = 0; b < 3; b++)
                        line = line + sudoku.getSudoku()[i].getNumbers()[a][b];
                line = line + "\n";
                w.write(line);
            }

            for (int i = 0; i < 9; i++) {
                String line = "";
                for (int a = 0; a < 3; a++)
                    for (int b = 0; b < 3; b++)
                        line = line + sudoku.getSolution()[i].getNumbers()[a][b];
                line = line + "\n";
                w.write(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static Sudoku load(Uri uri, Context context) throws DataFormatException, IOException {
        if(uri == null)
            throw new DataFormatException();

        File file = null;
        Sudoku sudoku;

        if (uri.getScheme().equals("file")) {
            String fileName = uri.getEncodedPath();
            file = new File(fileName);
        } else if (!uri.getScheme().equals("content"))
            throw new DataFormatException();

        InputStream inputStream;
        if (file != null)
            inputStream = new FileInputStream(file);
        else
            inputStream = context.getContentResolver().openInputStream(uri);

        try (BufferedReader r = new BufferedReader(new InputStreamReader(inputStream))) {
            sudoku = new Sudoku(4);

            if (!r.readLine().equals("$SUDOKU_DOCUMENT"))
                throw new DataFormatException();

            StartActivity.difficulty = Integer.parseInt(r.readLine());

            Block[] block = new Block[9];
            int[][] numbers = new int[3][3];
            for (int i = 0; i < 9; i++) {
                String line = r.readLine();
                int k = 0;
                for (int a = 0; a < 3; a++)
                    for (int b = 0; b < 3; b++)
                        numbers[a][b] = Integer.parseInt(String.valueOf(line.charAt(k++)));
                block[i] = new Block();
                block[i].setNumbers(numbers);
            }
            sudoku.setSudoku(block);

            block = new Block[9];
            numbers = new int[3][3];
            for (int i = 0; i < 9; i++) {
                String line = r.readLine();
                int k = 0;
                for (int a = 0; a < 3; a++)
                    for (int b = 0; b < 3; b++)
                        numbers[a][b] = Integer.parseInt(String.valueOf(line.charAt(k++)));
                block[i] = new Block();
                block[i].setNumbers(numbers);
            }
            sudoku.setSolution(block);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return sudoku;
    }
}
