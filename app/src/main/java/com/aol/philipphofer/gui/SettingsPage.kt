package com.aol.philipphofer.gui

import android.content.ActivityNotFoundException
import android.content.Intent
import android.view.View
import androidx.appcompat.widget.SwitchCompat
import androidx.core.net.toUri
import com.aol.philipphofer.R
import com.aol.philipphofer.gui.custom.CustomToast
import com.aol.philipphofer.gui.dialog.ConfirmDialog
import com.aol.philipphofer.persistence.Data

private const val CONTACT_URL = "https://www.sudoku-expert.com"

/**
 * Settings tab of the home screen, the switches are saved when the tab is left.
 */
class SettingsPage(root: View, onReset: () -> Unit) {

    private val data = Data.instance(root.context)

    private val markNumbers: SwitchCompat = root.findViewById(R.id.markNumbersSwitch)
    private val markLines: SwitchCompat = root.findViewById(R.id.markLinesSwitch)
    private val showErrors: SwitchCompat = root.findViewById(R.id.showErrorsSwitch)
    private val checkNotes: SwitchCompat = root.findViewById(R.id.checkNotesSwitch)
    private val showTime: SwitchCompat = root.findViewById(R.id.showTimeSwitch)
    private val hideCompleted: SwitchCompat = root.findViewById(R.id.hideCompletedSwitch)

    init {
        val context = root.context
        root.findViewById<View>(R.id.deleteData).setOnClickListener {
            ConfirmDialog(context, R.string.settings_confirm, R.string.settings_confirm_annotations) {
                data.drop()
                load()
                onReset()
            }.show()
        }
        root.findViewById<View>(R.id.info).setOnClickListener {
            context.startActivity(Intent(Intent.ACTION_VIEW, CONTACT_URL.toUri()))
        }
        // the Play Store page of the app, the toast covers devices without a store
        root.findViewById<View>(R.id.rate).setOnClickListener {
            try {
                context.startActivity(Intent(Intent.ACTION_VIEW, "market://details?id=${context.packageName}".toUri()))
            } catch (e: ActivityNotFoundException) {
                CustomToast(context, context.getString(R.string.error_default)).show()
            }
        }
    }

    fun load() {
        markLines.isChecked = data.loadBoolean(Data.SETTINGS_MARK_LINES)
        markNumbers.isChecked = data.loadBoolean(Data.SETTINGS_MARK_NUMBERS)
        checkNotes.isChecked = data.loadBoolean(Data.SETTINGS_CHECK_NOTES)
        showErrors.isChecked = data.loadBoolean(Data.SETTINGS_MARK_ERRORS)
        showTime.isChecked = data.loadBoolean(Data.SETTINGS_SHOW_TIME)
        hideCompleted.isChecked = data.loadBoolean(Data.SETTINGS_HIDE_COMPLETED, false)
    }

    fun save() {
        data.saveBoolean(Data.SETTINGS_MARK_LINES, markLines.isChecked)
        data.saveBoolean(Data.SETTINGS_MARK_NUMBERS, markNumbers.isChecked)
        data.saveBoolean(Data.SETTINGS_CHECK_NOTES, checkNotes.isChecked)
        data.saveBoolean(Data.SETTINGS_MARK_ERRORS, showErrors.isChecked)
        data.saveBoolean(Data.SETTINGS_SHOW_TIME, showTime.isChecked)
        data.saveBoolean(Data.SETTINGS_HIDE_COMPLETED, hideCompleted.isChecked)
    }
}
