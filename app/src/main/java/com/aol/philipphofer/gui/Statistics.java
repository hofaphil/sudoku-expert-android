package com.aol.philipphofer.gui;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.aol.philipphofer.R;
import com.aol.philipphofer.gui.custom.CustomActivity;
import com.aol.philipphofer.logic.Timer;
import com.aol.philipphofer.logic.help.Difficulty;
import com.aol.philipphofer.persistence.Data;

public class Statistics extends CustomActivity {

    private TextView beginnerTime, advancedTime, expertTime;
    private TextView beginnerPlayed, advancedPlayed, expertPlayed;
    private TextView beginnerBest, advancedBest, expertBest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        setActionBar(findViewById(R.id.title));

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

        int timesPlayed;
        if ((timesPlayed = data.loadInt(Data.STATISTICS_TIMESPLAYED + Difficulty.BEGINNER.getNumber())) != 0)
            beginnerTime.setText(Timer.timeToString(data.loadInt(Data.STATISTICS_TIMEOVERALL + Difficulty.BEGINNER.getNumber()) / timesPlayed));
        if ((timesPlayed = data.loadInt(Data.STATISTICS_TIMESPLAYED + Difficulty.ADVANCED.getNumber())) != 0)
            advancedTime.setText(Timer.timeToString(data.loadInt(Data.STATISTICS_TIMEOVERALL + Difficulty.ADVANCED.getNumber()) / timesPlayed));
        if ((timesPlayed = data.loadInt(Data.STATISTICS_TIMESPLAYED + Difficulty.EXPERT.getNumber())) != 0)
            expertTime.setText(Timer.timeToString(data.loadInt(Data.STATISTICS_TIMEOVERALL + Difficulty.EXPERT.getNumber()) / timesPlayed));

        beginnerPlayed.setText(String.valueOf(data.loadInt(Data.STATISTICS_TIMESPLAYED + Difficulty.BEGINNER.getNumber())));
        advancedPlayed.setText(String.valueOf(data.loadInt(Data.STATISTICS_TIMESPLAYED + Difficulty.ADVANCED.getNumber())));
        expertPlayed.setText(String.valueOf(data.loadInt(Data.STATISTICS_TIMESPLAYED + Difficulty.EXPERT.getNumber())));

        beginnerBest.setText(Timer.timeToString(data.loadInt(Data.STATISTICS_BESTTIME + Difficulty.BEGINNER.getNumber())));
        advancedBest.setText(Timer.timeToString(data.loadInt(Data.STATISTICS_BESTTIME + Difficulty.ADVANCED.getNumber())));
        expertBest.setText(Timer.timeToString(data.loadInt(Data.STATISTICS_BESTTIME + Difficulty.EXPERT.getNumber())));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return true;
    }
}
