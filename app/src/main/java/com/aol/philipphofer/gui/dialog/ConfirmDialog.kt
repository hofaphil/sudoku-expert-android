package com.aol.philipphofer.gui.dialog

import android.content.Context
import android.widget.TextView
import com.aol.philipphofer.R
import com.aol.philipphofer.gui.custom.CustomDialog

class ConfirmDialog(context: Context, title: Int, message: Int, onConfirm: () -> Unit) :
    CustomDialog(context, R.layout.dialog_confirm) {

    init {
        setHeader(R.drawable.ic_alert, context.getString(title))
        findViewById<TextView>(R.id.dialogMessage).setText(message)
        findViewById<TextView>(R.id.dialogCancel).setOnClickListener { dismiss() }
        findViewById<TextView>(R.id.dialogOk).setOnClickListener {
            onConfirm()
            dismiss()
        }
    }
}
