package com.aol.philipphofer.gui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;

import com.aol.philipphofer.R;
import com.aol.philipphofer.gui.help.ColorObservable;
import com.aol.philipphofer.persistence.Data;

public class ColorChooserDialog extends Dialog implements View.OnClickListener {

    public ColorChooserDialog(Context context) {
        super(context);
        setContentView(R.layout.dialog_colorchooser);

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
        Data.instance(getContext()).saveString(Data.SETTINGS_COLOR, (String) v.getTag());
        ColorObservable.getInstance().notifyObservers();
    }
}
