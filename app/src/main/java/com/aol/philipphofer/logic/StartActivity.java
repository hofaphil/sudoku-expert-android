package com.aol.philipphofer.logic;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import com.aol.philipphofer.R;
import com.aol.philipphofer.persistence.Data;
import com.aol.philipphofer.sudoku.Sudoku;
import com.google.android.gms.ads.MobileAds;

public class StartActivity extends Activity {

    public static boolean sharedGame = false;
    public static int difficulty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onStart(){
        super.onStart();

        Sudoku sudoku;

        MobileAds.initialize(this, i -> {});

        Data data = Data.instance(this);

        Intent intent = getIntent();
        sharedGame = !intent.getAction().equals(Intent.ACTION_MAIN);

        if (sharedGame) {
            MainActivity.SHARED = true;
            Uri uri = getIntent().getData();
            try {
                sudoku = ShareClass.load(uri, this);
                data.saveSudoku(sudoku.getSudoku());
                data.saveSolution(sudoku.getSolution());
                data.saveInt(Data.GAME_DIFFICULTY, difficulty);
                data.saveInt(Data.GAME_ERRORS, 0);
                data.saveInt(Data.GAME_TIME, 0);
                data.setLoadmode(true);
            } catch (Exception e) {
                Toast.makeText(this, getResources().getString(R.string.start_error), Toast.LENGTH_LONG).show();
                data.setLoadmode(false); //TODO Error catching
            }
        }
        intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
