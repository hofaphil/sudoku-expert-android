package com.aol.philipphofer.gui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.CheckBox
import com.aol.philipphofer.R
import com.aol.philipphofer.gui.custom.CustomActivity
import com.aol.philipphofer.gui.dialog.ColorChooserDialog
import com.aol.philipphofer.gui.dialog.ConfirmDialog
import com.aol.philipphofer.persistence.Data

private const val COLOR_CHANGED = "color_changed"
private const val CONTACT_URL = "https://www.philipphofer.de/contact"

class Settings : CustomActivity() {

    private lateinit var markNumbers: CheckBox
    private lateinit var markLines: CheckBox
    private lateinit var showErrors: CheckBox
    private lateinit var checkNotes: CheckBox
    private lateinit var showTime: CheckBox

    private var colorChanged = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        setActionBar(findViewById(R.id.title))

        colorChanged = intent.getIntExtra(COLOR_CHANGED, 0)

        markLines = findViewById(R.id.markLinesSwitch)
        markNumbers = findViewById(R.id.markNumbersSwitch)
        checkNotes = findViewById(R.id.checkNotesSwitch)
        showErrors = findViewById(R.id.showErrorsSwitch)
        showTime = findViewById(R.id.showTimeSwitch)

        findViewById<Button>(R.id.deleteData).setOnClickListener {
            ConfirmDialog(
                this,
                resources.getString(R.string.settings_confirm),
                resources.getString(R.string.settings_confirm_annotations)
            ) { data.drop() }.show()
        }
        findViewById<Button>(R.id.color).setOnClickListener {
            ColorChooserDialog(
                this,
                this
            ).show()
        }
        findViewById<Button>(R.id.info).setOnClickListener {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(CONTACT_URL)
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

    override fun onBackPressed() {
        super.onBackPressed()
        val returnIntent = Intent()
        setResult(colorChanged, returnIntent)
        finish()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.home) {
            val returnIntent = Intent()
            setResult(colorChanged, returnIntent)
            finish()
        }
        return true
    }

    fun recreate(colorChanged: Int) {
        intent.putExtra(COLOR_CHANGED, colorChanged)
        super.recreate()
    }
}