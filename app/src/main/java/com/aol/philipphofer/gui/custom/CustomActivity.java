package com.aol.philipphofer.gui.custom;

import android.app.Activity;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.aol.philipphofer.R;
import com.aol.philipphofer.persistence.Data;

public abstract class CustomActivity extends AppCompatActivity{

    protected Data data;

    @Override
    protected void onCreate(Bundle savedBundle) {
        super.onCreate(savedBundle);
        data = Data.instance(this);

        System.out.println(getTheme());
    }
}
