package com.aol.philipphofer.gui.dialog

import android.app.Dialog
import android.content.Context
import android.widget.Button
import android.widget.TextView
import android.widget.Toolbar
import com.aol.philipphofer.R

class ConfirmDialog(context: Context, title: String, message: String, okButton: Runnable) :
    Dialog(context) {

    init {
        setContentView(R.layout.dialog_confirm);

        findViewById<Toolbar>(R.id.title).title = title
        findViewById<TextView>(R.id.description).text = message

        findViewById<Button>(R.id.ok).setOnClickListener {
            okButton.run()
            dismiss()
        }

        findViewById<Button>(R.id.cancel).setOnClickListener { dismiss() }
    }
}