package com.aol.philipphofer.gui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.view.menu.MenuPopupHelper;
import androidx.appcompat.widget.PopupMenu;

import com.aol.philipphofer.R;
import com.aol.philipphofer.gui.custom.CustomColor;
import com.aol.philipphofer.gui.help.ColorObservable;
import com.aol.philipphofer.gui.sudoku.SudokuGrid;
import com.aol.philipphofer.logic.MainActivity;
import com.aol.philipphofer.logic.Timer;
import com.aol.philipphofer.persistence.Data;

import java.util.Observable;
import java.util.Observer;

public class StatusBar extends RelativeLayout implements Observer {

    private final ImageButton moreButton, newButton;
    private final TextView timeView, errorView, difficultyView;
    private final MainActivity mainActivity;

    @SuppressLint("RestrictedApi")
    public StatusBar(final Context context, AttributeSet attrSet) {
        super(context, attrSet);

        LayoutInflater.from(context).inflate(R.layout.sudoku_statusbar, this);

        mainActivity = (MainActivity) context;

        findViewById(R.id.layout).setBackgroundColor(Color.parseColor(Data.instance(mainActivity).loadString(Data.SETTINGS_COLOR, CustomColor.YELLOW.getHex())));
        ColorObservable.getInstance().addObserver(this);

        moreButton = findViewById(R.id.popupButton);
        moreButton.setOnClickListener((View v) -> {
            PopupMenu popup = new PopupMenu(context, moreButton);
            popup.setOnMenuItemClickListener((MenuItem item) -> {
                Intent intent;
                if (item.getItemId() == R.id.popup_statistics) {
                    intent = new Intent(mainActivity, Statistics.class);
                } else if (item.getItemId() == R.id.popup_share) {
                    mainActivity.share();
                    return true;
                } else {
                    intent = new Intent(mainActivity, Settings.class);
                }
                mainActivity.startActivity(intent);
                return true;
            });
            popup.getMenuInflater().inflate(R.menu.popup_more, popup.getMenu());

            @SuppressLint("RestrictedApi") MenuPopupHelper menuHelper = new MenuPopupHelper(getContext(), (MenuBuilder) popup.getMenu(), moreButton);
            menuHelper.setForceShowIcon(true);
            menuHelper.show();
        });


        newButton = findViewById(R.id.newButton);
        newButton.setOnClickListener((View v) -> {
            Intent intent = new Intent(mainActivity, NewSudoku.class);
            mainActivity.sudokuGrid.changeBackground(SudokuGrid.BackgroundMode.TRANSPARENT);
            mainActivity.startActivityForResult(intent, 1);
        });

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

    public void deactivate() {
        moreButton.setEnabled(false);
        newButton.setEnabled(false);
    }

    public void activate() {
        moreButton.setEnabled(true);
        newButton.setEnabled(true);
    }

    @Override
    public void update(Observable o, Object arg) {
        findViewById(R.id.layout).setBackgroundColor(Color.parseColor(Data.instance(getContext()).loadString(Data.SETTINGS_COLOR, CustomColor.YELLOW.getHex())));
    }
}

