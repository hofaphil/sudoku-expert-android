package com.aol.philipphofer.gui.custom

import android.content.Context
import android.view.LayoutInflater
import android.widget.TextView
import android.widget.Toast
import com.aol.philipphofer.R

class CustomToast(context: Context, text: String) {

    private val toast: Toast

    init {
        val inflater: LayoutInflater =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val layout = inflater.inflate(R.layout.custom_toast, null)

        layout.findViewById<TextView>(R.id.text).text = text
        toast = Toast(context)
        toast.duration = Toast.LENGTH_SHORT
        // TODO deprecated: change to new way of activation
        toast.view = layout
    }

    fun show() {
        toast.show()
    }
}