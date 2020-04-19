package com.aol.philipphofer.gui.help;

import android.view.Window;

import java.util.Observable;

public class ColorObservable extends Observable {

    private static ColorObservable instance = null;

    private ColorObservable(){}

    public static ColorObservable getInstance() {
        if (instance == null)
            instance = new ColorObservable();
        return instance;
    }

    @Override
    public boolean hasChanged() {
        return true;
    }
}
