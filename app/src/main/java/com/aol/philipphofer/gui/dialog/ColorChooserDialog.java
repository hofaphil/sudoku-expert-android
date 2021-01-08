package com.aol.philipphofer.gui.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;

import com.aol.philipphofer.R;
import com.aol.philipphofer.gui.Settings;
import com.aol.philipphofer.gui.help.ColorObservable;
import com.aol.philipphofer.logic.MainActivity;
import com.aol.philipphofer.logic.StartActivity;
import com.aol.philipphofer.persistence.Data;

import java.util.ArrayList;
import java.util.Arrays;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class ColorChooserDialog extends Dialog implements View.OnClickListener {

    private final Settings settings;
    private MainActivity mainActivity;

    public ColorChooserDialog(Context context, Settings settings) {
        super(context);
        setContentView(R.layout.dialog_colorchooser);

        System.out.println(context.getClass());
        System.out.println(settings.getClass());

        this.settings = settings;
        //this.mainActivity = (MainActivity) context;

        Button yellow = findViewById(R.id.yellow);
        yellow.setOnClickListener(this);

        Button green = findViewById(R.id.green);
        green.setOnClickListener(this);

        Button blue = findViewById(R.id.blue);
        blue.setOnClickListener(this);

        Button orange = findViewById(R.id.orange);
        orange.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Data.instance(getContext()).saveTheme(Integer.parseInt((String) v.getTag()));
        ColorObservable.getInstance().notifyObservers();
        dismiss();
        settings.recreate();
        //mainActivity.recreate();
        //((Activity) getContext()).recreate();
        // Restart app
        /*Intent intent = new Intent(getContext(), StartActivity.class);
        intent.setAction(Intent.ACTION_MAIN);
        getContext().startActivity(intent);
        Runtime.getRuntime().exit(0);*/
    }
}
