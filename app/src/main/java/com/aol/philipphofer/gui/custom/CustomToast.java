package com.aol.philipphofer.gui.custom;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.aol.philipphofer.R;
import com.aol.philipphofer.persistence.Data;

public class CustomToast {

    private Toast toast;

    public CustomToast(Activity c, String text) {
        LayoutInflater inflater = c.getLayoutInflater();
        View layout = inflater.inflate(R.layout.custom_toast, c.findViewById(R.id.layout));
        layout.setBackgroundColor(Color.parseColor(Data.instance(c).loadString(Data.SETTINGS_COLOR, CustomColor.YELLOW.getHex())));

         ((TextView) layout.findViewById(R.id.text)).setText(text);

        toast = new Toast(c);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
    }

    public void show(){
        toast.show();
    }
}
