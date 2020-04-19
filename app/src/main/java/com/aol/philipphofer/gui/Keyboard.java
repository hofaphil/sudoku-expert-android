package com.aol.philipphofer.gui;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;

import com.aol.philipphofer.R;
import com.aol.philipphofer.gui.custom.CustomColor;
import com.aol.philipphofer.gui.help.ColorObservable;
import com.aol.philipphofer.logic.MainActivity;
import com.aol.philipphofer.persistence.Data;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.Observable;
import java.util.Observer;

public class Keyboard extends GridLayout implements View.OnClickListener, Observer {

    private Button delButton, notesButton, pauseButton;
    private Button[] keys;
    private MainActivity mainActivity;

    public Keyboard(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.sudoku_keyboard, this);

        mainActivity = (MainActivity) context;

        ColorObservable.getInstance().addObserver(this);

        delButton = findViewById(R.id.delete);
        delButton.setOnClickListener((View v) -> mainActivity.delete());

        notesButton = findViewById(R.id.notes);
        notesButton.setOnClickListener((View v) -> {
            if (mainActivity.notesMode())
                notesButton.setBackgroundColor(Color.parseColor(Data.instance(context).loadString(Data.SETTINGS_COLOR, CustomColor.YELLOW.getHex())));
            else
                notesButton.setBackgroundColor(getResources().getColor(R.color.transparent));
        });

        pauseButton = findViewById(R.id.pause);
        pauseButton.setOnClickListener((v) -> mainActivity.pauseGame());

        keys = new Button[9];
        for (int i = 0; i < 9; i++) {
            keys[i] = findViewById(context.getResources().getIdentifier("num" + (i + 1), "id", context.getPackageName()));
            keys[i].setTag(i + 1);
            keys[i].setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View view) {
        mainActivity.insert((int) view.getTag());
    }

    public void deactivateNumber(int number) {
        keys[number - 1].setEnabled(false);
        keys[number - 1].setVisibility(View.INVISIBLE);
    }

    public void activateNumber(int number) {
        keys[number - 1].setEnabled(true);
        keys[number - 1].setVisibility(View.VISIBLE);
    }

    public void activatePauseMode() {
        for (int i = 0; i < 9; i++)
            keys[i].setEnabled(false);
        pauseButton.setBackgroundColor(Color.parseColor(Data.instance(getContext()).loadString(Data.SETTINGS_COLOR, CustomColor.YELLOW.getHex())));
        delButton.setEnabled(false);
        notesButton.setEnabled(false);
    }

    public void deactivatePauseMode() {
        for (int i = 0; i < 9; i++)
            if (keys[i].getVisibility() == View.VISIBLE)
                keys[i].setEnabled(true);
        pauseButton.setBackgroundColor(getResources().getColor(R.color.transparent));
        delButton.setEnabled(true);
        notesButton.setEnabled(true);
    }

    public void activate() {
        for (int i = 0; i < 9; i++)
            keys[i].setEnabled(true);
        pauseButton.setEnabled(true);
        delButton.setEnabled(true);
        notesButton.setEnabled(true);
    }

    public void deactivate() {
        for (int i = 0; i < 9; i++)
            keys[i].setEnabled(false);
        pauseButton.setEnabled(false);
        delButton.setEnabled(false);
        notesButton.setEnabled(false);
    }

    @Override
    public void update(Observable o, Object arg) {
        if(mainActivity.isNotes())
        notesButton.setBackgroundColor(Color.parseColor(Data.instance(mainActivity).loadString(Data.SETTINGS_COLOR, CustomColor.YELLOW.getHex())));
            else
        notesButton.setBackgroundColor(getResources().getColor(R.color.transparent));
    }
}