package com.aol.philipphofer.gui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.widget.TextView;

import com.aol.philipphofer.R;

public class ConfirmDialog extends Dialog {

    public ConfirmDialog(Context context, String title, String message, Runnable okButton) {
        super(context);
        setContentView(R.layout.dialog_confirm);

        ((TextView) findViewById(R.id.title)).setText(title);
        ((TextView) findViewById(R.id.description)).setText(message);

        findViewById(R.id.ok).setOnClickListener(v ->  {
            okButton.run();
            dismiss();
        });
        findViewById(R.id.cancel).setOnClickListener(v -> dismiss());

        show();
    }
}
