package com.aol.philipphofer.gui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aol.philipphofer.R;
import com.aol.philipphofer.gui.custom.CustomColor;
import com.aol.philipphofer.gui.help.ColorObservable;
import com.aol.philipphofer.gui.sudoku.SudokuGrid;
import com.aol.philipphofer.logic.MainActivity;
import com.aol.philipphofer.logic.Timer;
import com.aol.philipphofer.logic.help.Difficulty;
import com.aol.philipphofer.persistence.Data;

import java.util.Observable;
import java.util.Observer;

public class StatusBar extends RelativeLayout implements Observer {

    private ImageButton settingsButton, newButton, statisticsButton, shareButton;
    private TextView timeView, errorView, difficultyView;
    private MainActivity mainActivity;

    public StatusBar(final Context context, AttributeSet attrSet) {
        super(context, attrSet);

        LayoutInflater.from(context).inflate(R.layout.sudoku_statusbar, this);

        mainActivity = (MainActivity) context;

        findViewById(R.id.layout).setBackgroundColor(Color.parseColor(Data.instance(mainActivity).loadString(Data.SETTINGS_COLOR, CustomColor.YELLOW.getHex())));
        ColorObservable.getInstance().addObserver(this);



        settingsButton = findViewById(R.id.settingsButton);
        settingsButton.setOnClickListener((View v) -> {

            PopupMenu popup = new PopupMenu(context, settingsButton);
            //Inflating the Popup using xml file
            popup.getMenuInflater().inflate(R.menu.popup, popup.getMenu());
            popup.show();

            //Intent intent = new Intent(mainActivity, Settings.class);
            //mainActivity.startActivity(intent);
        });


        newButton = findViewById(R.id.newButton);
        newButton.setOnClickListener((View v) -> {
            Intent intent = new Intent(mainActivity, NewSudoku.class);
            mainActivity.sudokuGrid.changeBackground(SudokuGrid.BackgroundMode.TRANSPARENT);
            mainActivity.startActivityForResult(intent, 1);
        });

        statisticsButton = findViewById(R.id.statisticsButton);
        statisticsButton.setOnClickListener((View v) -> {
            Intent intent = new Intent(mainActivity, Statistics.class);
            mainActivity.startActivity(intent);
        });

        shareButton = findViewById(R.id.shareButton);
        shareButton.setOnClickListener((View v) -> mainActivity.share());

        timeView = findViewById(R.id.timeView);

        difficultyView = findViewById(R.id.difficultyView);
        initDifficultyView();

        errorView = findViewById(R.id.errorView);
        errorView.setText(getResources().getString(R.string.statusbar_errors, mainActivity.getErrors(), MainActivity.MAXERROR));
        initError();
    }

    public void initDifficultyView() {
        difficultyView.setText(MainActivity.DIFFICULTY.getText(getContext()));
    }

    public void initError() {
        if (!Data.instance(mainActivity).loadBoolean(Data.GAME_SHOW_ERRORS))
            errorView.setVisibility(View.INVISIBLE);
        else
            errorView.setVisibility(View.VISIBLE);
    }

    public void setError() {
        errorView.setText(getResources().getString(R.string.statusbar_errors, mainActivity.getErrors(), MainActivity.MAXERROR));
    }

    public void setTime(int time) {
        if (Data.instance(mainActivity).loadBoolean(Data.GAME_SHOW_TIME))
            this.timeView.setText(Timer.timeToString(time));
        else
            this.timeView.setText("--:--");
    }
/*
    public void initTime() {
        Data.instance(context).saveBoolean(SHOW_TIME, Data.instance(context).loadBoolean(Settings.SHOW_TIME));
    }
*/
    public void deactivate() {
        settingsButton.setEnabled(false);
        shareButton.setEnabled(false);
        statisticsButton.setEnabled(false);
        newButton.setEnabled(false);
    }

    public void activate() {
        settingsButton.setEnabled(true);
        shareButton.setEnabled(true);
        statisticsButton.setEnabled(true);
        newButton.setEnabled(true);
    }

    @Override
    public void update(Observable o, Object arg) {
        findViewById(R.id.layout).setBackgroundColor(Color.parseColor(Data.instance(getContext()).loadString(Data.SETTINGS_COLOR, CustomColor.YELLOW.getHex())));
    }
}

