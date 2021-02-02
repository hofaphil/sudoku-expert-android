package com.aol.philipphofer.gui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.widget.TextView;
import android.widget.Toolbar;

import com.aol.philipphofer.R;

public class ConfirmDialog extends Dialog {

    public ConfirmDialog(Context context, String title, String message, Runnable okButton) {
        super(context);
        setContentView(R.layout.dialog_confirm);

        ((Toolbar) findViewById(R.id.title)).setTitle(title);
        ((TextView) findViewById(R.id.description)).setText(message);

        findViewById(R.id.ok).setOnClickListener(v -> {
            okButton.run();
            dismiss();
        });
        findViewById(R.id.cancel).setOnClickListener(v -> dismiss());
    }
}
