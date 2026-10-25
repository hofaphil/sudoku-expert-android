package com.aol.philipphofer.gui.dialog

import android.content.Context
import android.widget.TextView
import com.aol.philipphofer.R
import com.aol.philipphofer.gui.custom.CustomDialog

class InfoDialog(context: Context, icon: Int, title: String, subtitle: String?, message: Int) :
    CustomDialog(context, R.layout.dialog_info) {

    init {
        setHeader(icon, title, subtitle)
        findViewById<TextView>(R.id.dialogMessage).setText(message)
        findViewById<TextView>(R.id.dialogOk).setOnClickListener { dismiss() }
    }
}
