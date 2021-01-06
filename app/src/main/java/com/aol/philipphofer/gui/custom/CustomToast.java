package com.aol.philipphofer.gui.custom;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.aol.philipphofer.R;
import com.aol.philipphofer.persistence.Data;

public class CustomToast {

    private final Toast toast;

    public CustomToast(Context c, String text) {
        LayoutInflater inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        @SuppressLint("InflateParams") View layout = inflater.inflate(R.layout.custom_toast, null);
        layout.setBackgroundColor(Color.parseColor(Data.instance(c).loadString(Data.SETTINGS_COLOR, CustomColor.YELLOW.getHex())));

        ((TextView) layout.findViewById(R.id.text)).setText(text);

        toast = new Toast(c);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
    }

    public void show() {
        toast.show();
    }
}
