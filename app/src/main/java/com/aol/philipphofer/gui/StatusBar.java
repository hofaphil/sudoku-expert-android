package com.aol.philipphofer.gui;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
import com.aol.philipphofer.gui.custom.CustomToast;
import com.aol.philipphofer.logic.MainActivity;
import com.aol.philipphofer.logic.Timer;
import com.aol.philipphofer.logic.help.Difficulty;
import com.aol.philipphofer.persistence.Data;

public class StatusBar extends RelativeLayout {

    private final ImageButton moreButton, newButton;
    private final TextView timeView, errorView, difficultyView;
    private final MainActivity mainActivity;

    @SuppressLint("RestrictedApi")
    public StatusBar(final Context context, AttributeSet attrSet) {
        super(context, attrSet);

        LayoutInflater.from(context).inflate(R.layout.sudoku_statusbar, this);

        mainActivity = (MainActivity) context;

        moreButton = findViewById(R.id.popupButton);
        moreButton.setOnClickListener((View v) -> {
            PopupMenu popup = new PopupMenu(context, moreButton);
            popup.getMenuInflater().inflate(R.menu.popup_more, popup.getMenu());
            popup.setOnMenuItemClickListener((MenuItem item) -> {
                Intent intent;
                if (item.getItemId() == R.id.popup_statistics) {
                    intent = new Intent(mainActivity, Statistics.class);
                    mainActivity.startActivityForResult(intent, 0);
                } else if (item.getItemId() == R.id.popup_share) {
                    mainActivity.share();
                    return true;
                } else if (item.getItemId() == R.id.popup_settings) {
                    intent = new Intent(mainActivity, Settings.class);
                    mainActivity.startActivityForResult(intent, 0);
                } else if (item.getItemId() == R.id.popup_rate) {
                    Uri uri = Uri.parse("market://details?id=" + getContext().getPackageName());
                    Intent myAppLinkToMarket = new Intent(Intent.ACTION_VIEW, uri);
                    try {
                        getContext().startActivity(myAppLinkToMarket);
                    } catch (ActivityNotFoundException e) {
                        new CustomToast(getContext(), getResources().getString(R.string.error_default)).show();
                    }
                    return true;
                } else {
                    return false;
                }
                return true;
            });

            MenuPopupHelper menuHelper = new MenuPopupHelper(getContext(), (MenuBuilder) popup.getMenu(), moreButton);
            menuHelper.setForceShowIcon(true);
            menuHelper.show();
        });

        newButton = findViewById(R.id.newButton);
        newButton.setOnClickListener((View v) -> {
            PopupMenu popup = new PopupMenu(context, newButton);
            popup.getMenuInflater().inflate(R.menu.popup_new, popup.getMenu());
            popup.setOnMenuItemClickListener((MenuItem item) -> {
                if (item.getItemId() == R.id.popup_advanced) {
                    Data.instance(context).saveInt(Data.GAME_DIFFICULTY, Difficulty.ADVANCED.getNumber());
                } else if (item.getItemId() == R.id.popup_expert) {
                    Data.instance(context).saveInt(Data.GAME_DIFFICULTY, Difficulty.EXPERT.getNumber());
                } else {
                    Data.instance(context).saveInt(Data.GAME_DIFFICULTY, Difficulty.BEGINNER.getNumber());
                }
                Data.instance(context).setLoadmode(false);
                mainActivity.onResume();
                return true;
            });

            @SuppressLint("RestrictedApi") MenuPopupHelper menuHelper = new MenuPopupHelper(getContext(), (MenuBuilder) popup.getMenu(), newButton);
            menuHelper.setForceShowIcon(true);
            menuHelper.show();
        });

        timeView = findViewById(R.id.timeView);
        difficultyView = findViewById(R.id.difficultyView);
        errorView = findViewById(R.id.errorView);
    }

    public void initView(Difficulty difficulty) {
        difficultyView.setText(difficulty.getText(mainActivity));
        if (!Data.instance(mainActivity).loadBoolean(Data.GAME_SHOW_ERRORS))
            errorView.setVisibility(View.INVISIBLE);
        else
            errorView.setVisibility(View.VISIBLE);

        setDifficulty(Difficulty.ADVANCED);
        setError(0);
    }

    public void setDifficulty(Difficulty difficulty) {
        difficultyView.setText(difficulty.getText(getContext()));
    }

    public void setError(int error) {
        errorView.setText(getResources().getString(R.string.statusbar_errors, error, MainActivity.MAX_ERROR));
    }

    public void setTime(int time) {
        if (Data.instance(getContext()).loadBoolean(Data.GAME_SHOW_TIME))
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
}

