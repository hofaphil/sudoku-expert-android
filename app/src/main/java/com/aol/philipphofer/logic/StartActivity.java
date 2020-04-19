package com.aol.philipphofer.logic;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.widget.Toast;

import com.aol.philipphofer.R;
import com.aol.philipphofer.persistence.Data;
import com.aol.philipphofer.sudoku.Sudoku;
import com.google.android.gms.ads.MobileAds;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class StartActivity extends Activity {

    public static boolean sharedGame = false;
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
        sharedGame = !intent.getAction().equals(Intent.ACTION_MAIN);

        if (sharedGame) {
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
                e.printStackTrace();
                Toast.makeText(this, getResources().getString(R.string.start_error), Toast.LENGTH_LONG).show();
                data.setLoadmode(false); //TODO Error catching
            }
        }

        intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}