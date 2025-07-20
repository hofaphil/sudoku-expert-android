package com.aol.philipphofer.gui.custom

import android.app.Activity
import android.os.Bundle
import com.aol.philipphofer.persistence.Data

abstract class CustomActivity : Activity() {

    protected lateinit var data: Data

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        data = Data.instance(this);
    }
}