package com.aol.philipphofer.gui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.aol.philipphofer.R;
import com.aol.philipphofer.gui.custom.CustomAdLoader;
import com.aol.philipphofer.persistence.Data;

public class NewSudoku extends Activity implements View.OnClickListener {

    private Button beginner, advanced, expert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newsudoku);

        this.setFinishOnTouchOutside(true);

        beginner = findViewById(R.id.beginner);
        advanced = findViewById(R.id.advanced);
        expert = findViewById(R.id.expert);

        beginner.setOnClickListener(this);
        advanced.setOnClickListener(this);
        expert.setOnClickListener(this);

        CustomAdLoader.loadAd(this, findViewById(R.id.adView));
    }

    @Override
    public void onClick(View v) {
        beginner.setEnabled(false);
        advanced.setEnabled(false);
        expert.setEnabled(false);
        Data.instance(this).setLoadmode(false);
        Data.instance(this).saveInt(Data.GAME_DIFFICULTY, Integer.parseInt(v.getTag().toString()));
        finish();
    }
}
