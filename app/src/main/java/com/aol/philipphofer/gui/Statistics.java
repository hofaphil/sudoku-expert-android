package com.aol.philipphofer.gui;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.aol.philipphofer.R;
import com.aol.philipphofer.gui.custom.CustomActivity;
import com.aol.philipphofer.gui.custom.CustomAdLoader;
import com.aol.philipphofer.logic.MainActivity;
import com.aol.philipphofer.logic.Timer;
import com.aol.philipphofer.persistence.Data;

public class Statistics extends CustomActivity {

    public static final String SAVE_BESTTIME = "besttime";

    public static final String SAVE_TIMEOVERALL = "timeoverall";
    public static final String SAVE_TIMESPLAYED = "timesplayed";

    private TextView beginnerTime, advancedTime, expertTime;
    private TextView beginnerPlayed, advancedPlayed, expertPlayed;
    private TextView beginnerBest, advancedBest, expertBest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        findViewById(R.id.backButton).setOnClickListener((View v) -> finish());

        beginnerTime = findViewById(R.id.Beginner_Average_Time);
        advancedTime = findViewById(R.id.Advanced_Average_Time);
        expertTime = findViewById(R.id.Expert_Average_Time);

        beginnerPlayed = findViewById(R.id.Beginner_TimesPlayed_Count);
        advancedPlayed = findViewById(R.id.Advanced_TimesPlayed_Count);
        expertPlayed = findViewById(R.id.Expert_TimesPlayed_Count);

        beginnerBest = findViewById(R.id.Beginner_Best_Time);
        advancedBest = findViewById(R.id.Advanced_Best_Time);
        expertBest = findViewById(R.id.Expert_Best_Time);
    }

    @Override
    protected void onStart() {
        super.onStart();

        CustomAdLoader.loadAd(this, findViewById(R.id.adView));

        int timesPlayed;
        if ((timesPlayed = data.loadInt(SAVE_TIMESPLAYED + MainActivity.BEGINNER)) != 0)
            beginnerTime.setText(Timer.timeToString(data.loadInt(SAVE_TIMEOVERALL + MainActivity.BEGINNER) / timesPlayed));
        if ((timesPlayed = data.loadInt(SAVE_TIMESPLAYED + MainActivity.ADVANCED)) != 0)
            advancedTime.setText(Timer.timeToString(data.loadInt(SAVE_TIMEOVERALL + MainActivity.ADVANCED) / timesPlayed));
        if ((timesPlayed = data.loadInt(SAVE_TIMESPLAYED + MainActivity.EXPERT)) != 0)
            expertTime.setText(Timer.timeToString(data.loadInt(SAVE_TIMEOVERALL + MainActivity.EXPERT) / timesPlayed));

        beginnerPlayed.setText("" + data.loadInt(SAVE_TIMESPLAYED + MainActivity.BEGINNER));
        advancedPlayed.setText("" + data.loadInt(SAVE_TIMESPLAYED + MainActivity.ADVANCED));
        expertPlayed.setText("" + data.loadInt(SAVE_TIMESPLAYED + MainActivity.EXPERT));

        beginnerBest.setText("" + Timer.timeToString(data.loadInt(SAVE_BESTTIME + MainActivity.BEGINNER)));
        advancedBest.setText("" + Timer.timeToString(data.loadInt(SAVE_BESTTIME + MainActivity.ADVANCED)));
        expertBest.setText("" + Timer.timeToString(data.loadInt(SAVE_BESTTIME + MainActivity.EXPERT)));
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    public static void addTime(Context context, int time, int difficulty) {
        int timesPlayed = Data.instance(context).loadInt(SAVE_TIMESPLAYED + difficulty) + 1;
        int timeOverall = Data.instance(context).loadInt(SAVE_TIMEOVERALL + difficulty) + time;
        Data.instance(context).saveInt(SAVE_TIMESPLAYED + difficulty, timesPlayed);
        Data.instance(context).saveInt(SAVE_TIMEOVERALL + difficulty, timeOverall);
        if (Data.instance(context).loadInt(SAVE_BESTTIME + difficulty) > time || Data.instance(context).loadInt(SAVE_BESTTIME + difficulty) == 0)
            Data.instance(context).saveInt(SAVE_BESTTIME + difficulty, time);
    }
}
