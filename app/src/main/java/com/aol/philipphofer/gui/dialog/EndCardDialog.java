package com.aol.philipphofer.gui.dialog;

import android.app.Dialog;
import android.widget.TextView;
import android.widget.Toolbar;

import com.aol.philipphofer.R;
import com.aol.philipphofer.logic.MainActivity;
import com.aol.philipphofer.logic.Timer;
import com.aol.philipphofer.logic.help.Difficulty;
import com.aol.philipphofer.persistence.Data;

public class EndCardDialog extends Dialog {

    private MainActivity mainActivity;

    public EndCardDialog(MainActivity mainActivity, boolean won, int time, Difficulty difficulty) {
        super(mainActivity);
        setContentView(R.layout.dialog_endcard);

        this.mainActivity = mainActivity;

        ((Toolbar) findViewById(R.id.title)).setTitle(won ? mainActivity.getResources().getString(R.string.win) : mainActivity.getResources().getString(R.string.lose));
        ((TextView) findViewById(R.id.difficultyInfo)).setText(difficulty.getText(mainActivity));
        ((TextView) findViewById(R.id.bestTimeInfo)).setText(Timer.timeToString(Data.instance(mainActivity).loadInt(Data.STATISTICS_BESTTIME + difficulty.getNumber())));

        if(won && Data.instance(mainActivity).loadBoolean(Data.GAME_SHOW_TIME))
            ((TextView) findViewById(R.id.timeInfo)).setText(Timer.timeToString(time));

        findViewById(R.id.ok).setOnClickListener(v -> dismiss());

        show();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mainActivity.onResume();
    }
}
