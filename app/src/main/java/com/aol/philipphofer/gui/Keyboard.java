package com.aol.philipphofer.gui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;

import com.aol.philipphofer.R;
import com.aol.philipphofer.logic.MainActivity;

public class Keyboard extends GridLayout implements View.OnClickListener {

    private final Button delButton, notesButton, pauseButton;
    private final Button[] keys;
    private final MainActivity mainActivity;

    public Keyboard(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.sudoku_keyboard, this);

        mainActivity = (MainActivity) context;

        delButton = findViewById(R.id.delete);
        delButton.setOnClickListener((View v) -> mainActivity.delete());

        notesButton = findViewById(R.id.notes);
        notesButton.setOnClickListener((View v) -> {
            if (mainActivity.notesMode())
                notesButton.setBackgroundColor(MainActivity.getPrimaryColor(getContext()));
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

    public void pauseMode(boolean isPauseMode) {
        activate(!isPauseMode);
        pauseButton.setEnabled(true);

        if (isPauseMode)
            pauseButton.setBackgroundColor(MainActivity.getPrimaryColor(getContext()));
        else
            pauseButton.setBackgroundColor(getResources().getColor(R.color.transparent));
    }

    public void activate(boolean activate) {
        for (int i = 0; i < 9; i++)
            keys[i].setEnabled(activate);
        pauseButton.setEnabled(activate);
        delButton.setEnabled(activate);
        notesButton.setEnabled(activate);
    }
}