package com.aol.philipphofer.gui.custom;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Window;

import com.aol.philipphofer.gui.help.ColorObservable;
import com.aol.philipphofer.persistence.Data;

import java.util.Observable;
import java.util.Observer;

public abstract class CustomActivity extends Activity {

    protected Data data;

    @Override
    protected void onCreate(Bundle savedBundle) {
        super.onCreate(savedBundle);
        data = Data.instance(this);
        setTheme(data.getTheme());
    }
}
