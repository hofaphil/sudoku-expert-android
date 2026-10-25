package com.aol.philipphofer.gui.custom

import android.app.Dialog
import android.content.Context
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import com.aol.philipphofer.R

// for dialogs with the dialog_header layout
open class CustomDialog(context: Context, layout: Int) : Dialog(context) {

    init {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(layout)
        // the layout draws the box, the window only dims the screen behind it
        window?.setBackgroundDrawableResource(R.color.transparent)
        window?.setLayout(MATCH_PARENT, WRAP_CONTENT)
    }

    protected fun setHeader(icon: Int, title: String, subtitle: String? = null) {
        findViewById<ImageView>(R.id.dialogIcon).setImageResource(icon)
        findViewById<TextView>(R.id.dialogTitle).text = title
        findViewById<TextView>(R.id.dialogSubtitle).apply {
            text = subtitle
            visibility = if (subtitle == null) View.GONE else View.VISIBLE
        }
    }
}
