package com.aol.philipphofer.gui.help;

import java.util.Observable;

public class ColorObservable extends Observable {

    private static ColorObservable instance = null;

    private ColorObservable(){}

    public static ColorObservable getInstance() {
        return instance == null ? instance = new ColorObservable() : instance;
    }

    @Override
    public boolean hasChanged() {
        return true;
    }
}
