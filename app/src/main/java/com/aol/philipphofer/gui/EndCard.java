package com.aol.philipphofer.gui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.aol.philipphofer.R;
import com.aol.philipphofer.gui.custom.CustomActivity;
import com.aol.philipphofer.logic.MainActivity;
import com.aol.philipphofer.logic.Timer;
import com.aol.philipphofer.logic.help.Difficulty;
import com.aol.philipphofer.persistence.Data;

public class EndCard extends CustomActivity implements View.OnClickListener {

    private Button ok;
    private TextView title, annotations;
    private TextView time, bestTime, difficulty;

    public final static String WON = "won";
    public final static String TIME = "time";
    public final static String DIFFICULTY = "diffic";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_endcard);

        ok = findViewById(R.id.okbutton);
        ok.setOnClickListener(this);

        title = findViewById(R.id.title);
        annotations = findViewById(R.id.annotations);

        this.time = findViewById(R.id.timeInfo);
        this.time.setText("---");
        this.bestTime = findViewById(R.id.bestTimeInfo);
        this.difficulty = findViewById(R.id.difficultyInfo);
    }

    @Override
    protected void onStart() {
        super.onStart();

        Intent intent = getIntent();
        boolean won = intent.getBooleanExtra(WON, false);
        int time = 0;
        if (data.loadBoolean(Data.GAME_SHOW_TIME))
            time = intent.getIntExtra(TIME, 0);
        int difficulty = intent.getIntExtra(DIFFICULTY, 0);
        init(won, time, Difficulty.getDifficulty(difficulty), this);
    }

    private void init(boolean won, int time, Difficulty difficulty, Context context) {
        this.difficulty.setText(difficulty.getText(context));

        bestTime.setText(Timer.timeToString(data.loadInt(Statistics.SAVE_BESTTIME + difficulty.getNumber())));

        if (won) {
            title.setText(getResources().getString(R.string.win));
            if (data.loadBoolean(Data.GAME_SHOW_TIME)) {
                this.time.setText(Timer.timeToString(time));
                annotations.setText(getResources().getString(R.string.endcard_winannotations, Timer.timeToString(time)));
                Statistics.addTime(this, time, difficulty);
            } else
                annotations.setText(getResources().getString(R.string.endcard_winannotationswithouttime));
        } else {
            title.setText(getResources().getString(R.string.lose));
            annotations.setText(getResources().getString(R.string.endcard_loseannotaions));
        }
    }

    @Override
    public void onClick(View v) {
        data.setLoadmode(false);
        finish();
    }
}
