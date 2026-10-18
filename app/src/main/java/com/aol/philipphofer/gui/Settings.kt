package com.aol.philipphofer.gui

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.aol.philipphofer.R
import com.aol.philipphofer.gui.custom.CustomActivity
import com.aol.philipphofer.gui.dialog.ConfirmDialog
import com.aol.philipphofer.persistence.Data
import androidx.core.net.toUri
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.materialswitch.MaterialSwitch

private const val CONTACT_URL = "https://www.sudoku-expert.com"

class Settings : CustomActivity() {

    private lateinit var markNumbers: MaterialSwitch
    private lateinit var markLines: MaterialSwitch
    private lateinit var showErrors: MaterialSwitch
    private lateinit var checkNotes: MaterialSwitch
    private lateinit var showTime: MaterialSwitch
    private lateinit var hideCompleted: MaterialSwitch

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        findViewById<MaterialToolbar>(R.id.toolbar).setNavigationOnClickListener { finish() }
        findViewById<AppBarLayout>(R.id.title).setExpanded(false, false)

        markLines = findViewById(R.id.markLinesSwitch)
        markNumbers = findViewById(R.id.markNumbersSwitch)
        checkNotes = findViewById(R.id.checkNotesSwitch)
        showErrors = findViewById(R.id.showErrorsSwitch)
        showTime = findViewById(R.id.showTimeSwitch)
        hideCompleted = findViewById(R.id.hideCompletedSwitch)

        findViewById<View>(R.id.deleteData).setOnClickListener {
            ConfirmDialog(
                this,
                resources.getString(R.string.settings_confirm),
                resources.getString(R.string.settings_confirm_annotations)
            ) { data.drop() }.show()
        }
        findViewById<View>(R.id.info).setOnClickListener {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    CONTACT_URL.toUri()
                )
            )
        }
    }

    override fun onResume() {
        super.onResume()
        markLines.isChecked = data.loadBoolean(Data.SETTINGS_MARK_LINES)
        markNumbers.isChecked = data.loadBoolean(Data.SETTINGS_MARK_NUMBERS)
        checkNotes.isChecked = data.loadBoolean(Data.SETTINGS_CHECK_NOTES)
        showErrors.isChecked = data.loadBoolean(Data.SETTINGS_MARK_ERRORS)
        showTime.isChecked = data.loadBoolean(Data.SETTINGS_SHOW_TIME)
        hideCompleted.isChecked = data.loadBoolean(Data.SETTINGS_HIDE_COMPLETED, false)
    }

    override fun onPause() {
        super.onPause()
        data.saveBoolean(Data.SETTINGS_MARK_LINES, markLines.isChecked)
        data.saveBoolean(Data.SETTINGS_MARK_NUMBERS, markNumbers.isChecked)
        data.saveBoolean(Data.SETTINGS_CHECK_NOTES, checkNotes.isChecked)
        data.saveBoolean(Data.SETTINGS_MARK_ERRORS, showErrors.isChecked)
        data.saveBoolean(Data.SETTINGS_SHOW_TIME, showTime.isChecked)
        data.saveBoolean(Data.SETTINGS_HIDE_COMPLETED, hideCompleted.isChecked)
    }
}
