package com.aol.philipphofer.logic;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;

import com.aol.philipphofer.R;
import com.aol.philipphofer.gui.custom.CustomToast;
import com.aol.philipphofer.persistence.Data;
import com.aol.philipphofer.sudoku.Sudoku;
import com.google.android.gms.ads.MobileAds;

import java.util.Objects;

public class StartActivity extends Activity {

    public static int difficulty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onStart() {
        super.onStart();

        Sudoku sudoku;

        MobileAds.initialize(this, i -> {
        });

        Data data = Data.instance(this);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        Intent intent = getIntent();

        /* if (!Objects.equals(intent.getAction(), Intent.ACTION_MAIN)) {
            Uri uri = getIntent().getData();

            try {
                sudoku = ShareClass.load(uri);
                MainActivity.SHARED = true;
                data.saveSudoku(sudoku.getSudoku());
                data.saveSolution(sudoku.getSolution());
                data.saveInt(Data.GAME_DIFFICULTY, difficulty);
                data.saveInt(Data.GAME_ERRORS, 0);
                data.saveInt(Data.GAME_TIME, 0);
                data.setLoadmode(true);
            } catch (Exception e) {
                new CustomToast(this, getResources().getString(R.string.error_default)).show();
            }
        } */

        intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}