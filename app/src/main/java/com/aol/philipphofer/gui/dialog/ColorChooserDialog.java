package com.aol.philipphofer.gui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;

import com.aol.philipphofer.R;
import com.aol.philipphofer.gui.Settings;
import com.aol.philipphofer.persistence.Data;

public class ColorChooserDialog extends Dialog implements View.OnClickListener {

    private final Settings settings;

    public ColorChooserDialog(Context context, Settings settings) {
        super(context);
        setContentView(R.layout.dialog_colorchooser);

        this.settings = settings;

        findViewById(R.id.yellow).setOnClickListener(this);
        findViewById(R.id.green).setOnClickListener(this);
        findViewById(R.id.blue).setOnClickListener(this);
        findViewById(R.id.orange).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Data.instance(getContext()).saveTheme(Integer.parseInt((String) v.getTag()));
        dismiss();
        settings.recreate(1);
    }
}
