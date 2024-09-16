package com.aol.philipphofer.gui.dialog

import android.app.Dialog
import android.content.Context
import android.view.View
import android.widget.Button
import com.aol.philipphofer.R
import com.aol.philipphofer.gui.Settings
import com.aol.philipphofer.persistence.Data

class ColorChooserDialog(context: Context, settings: Settings) : Dialog(context),
    View.OnClickListener {

    private val settings: Settings

    init {
        setContentView(R.layout.dialog_colorchooser)

        this.settings = settings

        findViewById<Button>(R.id.yellow).setOnClickListener(this)
        findViewById<Button>(R.id.green).setOnClickListener(this)
        findViewById<Button>(R.id.blue).setOnClickListener(this)
        findViewById<Button>(R.id.orange).setOnClickListener(this)

    }

    override fun onClick(v: View) {
        Data.instance(context).theme = (v.tag as String).toInt()

        dismiss()
        settings.recreate(1);
    }
}