package com.aol.philipphofer.gui.custom;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Window;

import com.aol.philipphofer.gui.help.ColorObservable;
import com.aol.philipphofer.persistence.Data;

import java.util.Observable;
import java.util.Observer;

public abstract class CustomActivity extends Activity implements Observer {

    protected Data data;

    @Override
    protected void onCreate(Bundle savedBundle) {
        super.onCreate(savedBundle);
    }

    @Override
    protected void onStart() {
        super.onStart();
        data = Data.instance(this);

        ColorObservable.getInstance().addObserver(this);
        Window window = getWindow();
        window.setStatusBarColor(Color.parseColor(data.loadString(Data.SETTINGS_COLOR, CustomColor.YELLOW.getHex())));

    }

    @Override
    public void update(Observable o, Object arg) {
        Window window = getWindow();
        window.setStatusBarColor(Color.parseColor(data.loadString(Data.SETTINGS_COLOR, CustomColor.YELLOW.getHex())));
    }
}
