package com.aol.philipphofer.gui.custom;

import android.app.Activity;
import android.os.Bundle;

import com.aol.philipphofer.persistence.Data;

public abstract class CustomActivity extends Activity {

    protected Data data;

    @Override
    protected void onCreate(Bundle savedBundle) {
        super.onCreate(savedBundle);
        data = Data.instance(this);
        setTheme(data.getTheme());
    }
}
