package com.aol.philipphofer.gui;

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

    public StatusBar(final Context context, AttributeSet attrSet) {
        super(context, attrSet);

        LayoutInflater.from(context).inflate(R.layout.sudoku_statusbar, this);

        mainActivity = (MainActivity) context;

        // new menu
        newButton = findViewById(R.id.newButton);
        PopupMenu newPopup = new PopupMenu(context, newButton);
        newPopup.inflate(R.menu.popup_new);
        newPopup.setForceShowIcon(true);
        newPopup.setOnMenuItemClickListener(this::newPopupHandler);
        newButton.setOnClickListener((View v) -> newPopup.show());

        // more menu
        moreButton = findViewById(R.id.popupButton);
        PopupMenu morePopup = new PopupMenu(context, moreButton);
        morePopup.inflate(R.menu.popup_more);
        morePopup.setForceShowIcon(true);
        morePopup.setOnMenuItemClickListener(this::morePopupHandler);
        moreButton.setOnClickListener((View v) -> morePopup.show());

        timeView = findViewById(R.id.timeView);
        difficultyView = findViewById(R.id.difficultyView);
        errorView = findViewById(R.id.errorView);
    }

    private boolean morePopupHandler(MenuItem item) {
        Intent intent;
        if (item.getItemId() == R.id.popup_statistics) {
            intent = new Intent(mainActivity, Statistics.class);
            mainActivity.startActivityForResult(intent, 0);
        } else if (item.getItemId() == R.id.popup_challenge) {
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
    }

    private boolean newPopupHandler(MenuItem item) {
        if (item.getItemId() == R.id.popup_advanced)
            Data.instance(mainActivity).saveInt(Data.GAME_DIFFICULTY, Difficulty.ADVANCED.getNumber());
        else if (item.getItemId() == R.id.popup_expert)
            Data.instance(mainActivity).saveInt(Data.GAME_DIFFICULTY, Difficulty.EXPERT.getNumber());
        else
            Data.instance(mainActivity).saveInt(Data.GAME_DIFFICULTY, Difficulty.BEGINNER.getNumber());
        Data.instance(mainActivity).setLoadmode(false);
        mainActivity.onResume();
        return true;
    }

    public void setDifficulty(Difficulty difficulty) {
        difficultyView.setText(difficulty.getText(getContext()));
    }

    public void setError(int error) {
        if (Data.instance(getContext()).loadBoolean(Data.GAME_SHOW_ERRORS))
            errorView.setText(getResources().getString(R.string.statusbar_errors, error, MainActivity.MAX_ERROR));
        else
            errorView.setText(getResources().getString(R.string.statusbar_errors_not_enabled));
    }

    public void setTime(int time) {
        if (Data.instance(getContext()).loadBoolean(Data.GAME_SHOW_TIME))
            this.timeView.setText(Timer.timeToString(time));
        else
            this.timeView.setText("--:--");
    }

    public void activate(boolean activate) {
        moreButton.setEnabled(activate);
        newButton.setEnabled(activate);
    }
}

