package com.aol.philipphofer.gui.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.aol.philipphofer.R;
import com.aol.philipphofer.gui.help.ColorObservable;
import com.aol.philipphofer.persistence.Data;

public class ColorChooserDialog extends Dialog implements View.OnClickListener {

    private Button yellow, green, blue, orange;

    public ColorChooserDialog(Context context) {
        super(context);
        setContentView(R.layout.dialog_colorchooser);

        yellow = findViewById(R.id.yellow);
        yellow.setOnClickListener(this);

        green = findViewById(R.id.green);
        green.setOnClickListener(this);

        blue = findViewById(R.id.blue);
        blue.setOnClickListener(this);

        orange = findViewById(R.id.orange);
        orange.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Data.instance(getContext()).saveString(Data.SETTINGS_COLOR, (String) v.getTag());
        ColorObservable.getInstance().notifyObservers();
    }
}
