package com.aol.philipphofer.gui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.CheckBox;

import com.aol.philipphofer.R;
import com.aol.philipphofer.gui.custom.CustomActivity;
import com.aol.philipphofer.gui.custom.CustomToast;
import com.aol.philipphofer.gui.dialog.ColorChooserDialog;
import com.aol.philipphofer.gui.dialog.ConfirmDialog;
import com.aol.philipphofer.persistence.Data;

public class Settings extends CustomActivity {

    private CheckBox markNumbers, markLines, showErrors, checkNotes, showTime;

    private static int colorChanged;
    private static final String COLOR_CHANGED = "color_changed";
    private static final String CONTACT_URL = "https://www.philipphofer.de/contact";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        setActionBar(findViewById(R.id.title));

        colorChanged = getIntent().getIntExtra(COLOR_CHANGED, 0);

        markLines = findViewById(R.id.markLinesSwitch);
        markNumbers = findViewById(R.id.markNumbersSwitch);
        checkNotes = findViewById(R.id.checkNotesSwitch);
        showErrors = findViewById(R.id.showErrorsSwitch);
        showTime = findViewById(R.id.showTimeSwitch);

        findViewById(R.id.deleteData).setOnClickListener(v -> new ConfirmDialog(this, getResources().getString(R.string.settings_confirm), getResources().getString(R.string.settings_confirm_annotations), () -> data.drop()).show());
        findViewById(R.id.color).setOnClickListener(v -> new ColorChooserDialog(this, this).show());
        findViewById(R.id.info).setOnClickListener(v -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(CONTACT_URL))));
    }

    @Override
    protected void onResume() {
        super.onResume();
        markLines.setChecked(data.loadBoolean(Data.SETTINGS_MARK_LINES));
        markNumbers.setChecked(data.loadBoolean(Data.SETTINGS_MARK_NUMBERS));
        checkNotes.setChecked(data.loadBoolean(Data.SETTINGS_CHECK_NOTES));
        showErrors.setChecked(data.loadBoolean(Data.SETTINGS_MARK_ERRORS));
        showTime.setChecked(data.loadBoolean(Data.SETTINGS_SHOW_TIME));
    }

    @Override
    protected void onPause() {
        super.onPause();
        data.saveBoolean(Data.SETTINGS_MARK_LINES, markLines.isChecked());
        data.saveBoolean(Data.SETTINGS_MARK_NUMBERS, markNumbers.isChecked());
        data.saveBoolean(Data.SETTINGS_CHECK_NOTES, checkNotes.isChecked());
        data.saveBoolean(Data.SETTINGS_MARK_ERRORS, showErrors.isChecked());
        data.saveBoolean(Data.SETTINGS_SHOW_TIME, showTime.isChecked());
    }

    @Override
    public void onBackPressed() {
        Intent returnIntent = new Intent();
        setResult(colorChanged, returnIntent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent returnIntent = new Intent();
            setResult(colorChanged, returnIntent);
            finish();
        }
        return true;
    }

    public void recreate(int colorChanged) {
        getIntent().putExtra(COLOR_CHANGED, colorChanged);
        super.recreate();
    }
}