package com.aol.philipphofer.gui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Button;

import com.aol.philipphofer.R;
import com.aol.philipphofer.persistence.Data;

public class RateAppDialog {

    private final static String APP_NAME = "com.aol.philipphofer";

    private final static String TIMES_OPENED = "times_opened";
    public final static String SHOW_DIALOG = "show_dialog";

    private Data data;

    public RateAppDialog(Context context) {
        data = Data.instance(context);
        data.saveInt(TIMES_OPENED, data.loadInt(TIMES_OPENED) + 1);
    }

    public void showDialog(Context context) {
        if (data.loadInt(TIMES_OPENED) > 4 && data.loadBoolean(SHOW_DIALOG)) {
            Dialog dialog = new Dialog(context);
            dialog.setContentView(R.layout.dialog_rate);

            Button rate = dialog.findViewById(R.id.rate);
            rate.setOnClickListener((view) -> {
                try {
                    context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + APP_NAME)));
                } catch (Exception e) {
                }
                data.saveBoolean(SHOW_DIALOG, false);
                dialog.dismiss();
            });

            Button noThanks = dialog.findViewById(R.id.no_thanks);
            noThanks.setOnClickListener((view) -> {
                data.saveBoolean(SHOW_DIALOG, false);
                dialog.dismiss();
            });

            Button remindMeLater = dialog.findViewById(R.id.remind_me_later);
            remindMeLater.setOnClickListener((view) -> dialog.dismiss());

            dialog.show();
        }
    }
}