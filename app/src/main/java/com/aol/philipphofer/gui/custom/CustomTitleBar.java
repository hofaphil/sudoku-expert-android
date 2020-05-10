package com.aol.philipphofer.gui.custom;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.aol.philipphofer.gui.help.ColorObservable;
import com.aol.philipphofer.persistence.Data;

import java.util.Observable;
import java.util.Observer;

public class CustomTitleBar extends RelativeLayout implements Observer {
    public CustomTitleBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        ColorObservable.getInstance().addObserver(this);
        setBackgroundColor(Color.parseColor(Data.instance(context).loadString(Data.SETTINGS_COLOR, CustomColor.YELLOW.getHex())));
    }

    @Override
    public void update(Observable o, Object arg) {
        setBackgroundColor(Color.parseColor(Data.instance(getContext()).loadString(Data.SETTINGS_COLOR, CustomColor.YELLOW.getHex())));
    }
}
