package com.aol.philipphofer.gui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.widget.CheckBox
import android.widget.RelativeLayout
import com.aol.philipphofer.R
import com.aol.philipphofer.gui.custom.CustomActivity
import com.aol.philipphofer.gui.dialog.ConfirmDialog
import com.aol.philipphofer.persistence.Data
import androidx.core.net.toUri

private const val CONTACT_URL = "https://www.sudoku-expert.com"

class Settings : CustomActivity() {

    private lateinit var markNumbers: CheckBox
    private lateinit var markLines: CheckBox
    private lateinit var showErrors: CheckBox
    private lateinit var checkNotes: CheckBox
    private lateinit var showTime: CheckBox

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        setActionBar(findViewById(R.id.title))

        markLines = findViewById(R.id.markLinesSwitch)
        markNumbers = findViewById(R.id.markNumbersSwitch)
        checkNotes = findViewById(R.id.checkNotesSwitch)
        showErrors = findViewById(R.id.showErrorsSwitch)
        showTime = findViewById(R.id.showTimeSwitch)

        findViewById<RelativeLayout>(R.id.deleteData).setOnClickListener {
            ConfirmDialog(
                this,
                resources.getString(R.string.settings_confirm),
                resources.getString(R.string.settings_confirm_annotations)
            ) { data.drop() }.show()
        }
        findViewById<RelativeLayout>(R.id.info).setOnClickListener {
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
    }

    override fun onPause() {
        super.onPause()
        data.saveBoolean(Data.SETTINGS_MARK_LINES, markLines.isChecked)
        data.saveBoolean(Data.SETTINGS_MARK_NUMBERS, markNumbers.isChecked)
        data.saveBoolean(Data.SETTINGS_CHECK_NOTES, checkNotes.isChecked)
        data.saveBoolean(Data.SETTINGS_MARK_ERRORS, showErrors.isChecked)
        data.saveBoolean(Data.SETTINGS_SHOW_TIME, showTime.isChecked)
    }
}