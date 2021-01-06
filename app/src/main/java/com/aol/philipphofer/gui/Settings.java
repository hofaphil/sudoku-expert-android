package com.aol.philipphofer.gui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.aol.philipphofer.R;
import com.aol.philipphofer.gui.custom.CustomActivity;
import com.aol.philipphofer.gui.custom.CustomButton;
import com.aol.philipphofer.gui.custom.CustomToast;
import com.aol.philipphofer.gui.dialog.ColorChooserDialog;
import com.aol.philipphofer.gui.dialog.ConfirmDialog;
import com.aol.philipphofer.persistence.Data;

public class Settings extends CustomActivity implements CompoundButton.OnCheckedChangeListener, BillingProcessor.IBillingHandler {

    private CheckBox markNumbers, markLines, powerMode, showErrors, checkNotes, showTime;
    private CustomButton deleteDataButton, supportButton, colorButton;
    private BillingProcessor bp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        bp = new BillingProcessor(this, "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAx61y5ioHZSErdYWJwURIZ1EAMwNRQsvgu3mDr9FDmWbu9JQf4qcjbKVpFsggXnp24Y3whDXJ4mMMI9fCnuQDMy0+Z+18sRtKtT5t1+prdvJb7gbY8D8Xeb1XYDxhC7p3M/pg1KDjdB1jax8s8pqHex5suUkE9n5349EpES/pMpgZlFGO4i2wMTAPiJJ8C5bVyPXeGVstGckdnUKtPZpnZQm/kWjvra7+Ccqltz8f7T89zsr2e2kEk3q6kRUrF2dln6kXpLJulWG6pAZ6DZq0t8UsPS5lsJtuC1dyHd9w/cUJ1x3q8dXnJCrcXnaKUMocVqkpWwk60jwGcwhOBo/KkQIDAQAB", this);
        bp.initialize();

        ImageButton back = findViewById(R.id.backButton);
        back.setOnClickListener(v -> finish());

        markLines = findViewById(R.id.markLinesSwitch);
        markLines.setOnCheckedChangeListener(this);

        markNumbers = findViewById(R.id.markNumbersSwitch);
        markNumbers.setOnCheckedChangeListener(this);

        checkNotes = findViewById(R.id.checkNotesSwitch);
        checkNotes.setOnCheckedChangeListener(this);

        powerMode = findViewById(R.id.powerModeSwitch);
        powerMode.setOnCheckedChangeListener(this);

        showErrors = findViewById(R.id.showErrorsSwitch);
        showErrors.setOnCheckedChangeListener(this);

        showTime = findViewById(R.id.showTimeSwitch);
        showTime.setOnCheckedChangeListener(this);

        deleteDataButton = findViewById(R.id.deleteDataButton);
        supportButton = findViewById(R.id.supportButton);
        colorButton = findViewById(R.id.colorButton);
    }

    @Override
    protected void onStart() {
        super.onStart();
        deleteDataButton.setOnClickListener(v -> new ConfirmDialog(this,
                getResources().getString(R.string.settings_confirm),
                getResources().getString(R.string.settings_confirm_annotations),
                () -> data.drop()));

        supportButton.setOnClickListener(v -> bp.purchase(this, "supporter"));

        colorButton.setOnClickListener(v -> {
            if (!data.loadBoolean(Data.SETTINGS_SUPPORTER, false))
                new CustomToast(this, getResources().getString(R.string.settings_support_error)).show();
            else
                new ColorChooserDialog(this).show();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        powerMode.setChecked(data.loadBoolean(Data.SETTINGS_POWERMODE));
        markLines.setChecked(data.loadBoolean(Data.SETTINGS_MARK_LINES));
        markNumbers.setChecked(data.loadBoolean(Data.SETTINGS_MARK_NUMBERS));
        checkNotes.setChecked(data.loadBoolean(Data.SETTINGS_CHECK_NOTES));
        showErrors.setChecked(data.loadBoolean(Data.SETTINGS_MARK_ERRORS));
        showTime.setChecked(data.loadBoolean(Data.SETTINGS_SHOW_TIME));
    }

    @Override
    protected void onPause() {
        super.onPause();
        data.saveBoolean(Data.SETTINGS_POWERMODE, powerMode.isChecked());
        data.saveBoolean(Data.SETTINGS_CHECK_NOTES, markLines.isChecked());
        data.saveBoolean(Data.SETTINGS_MARK_NUMBERS, markNumbers.isChecked());
        data.saveBoolean(Data.SETTINGS_CHECK_NOTES, checkNotes.isChecked());
        data.saveBoolean(Data.SETTINGS_MARK_ERRORS, showErrors.isChecked());
        data.saveBoolean(Data.SETTINGS_SHOW_TIME, showTime.isChecked());
    }

    @Override
    protected void onDestroy() {
        if (bp != null)
            bp.release();
        super.onDestroy();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView.equals(powerMode) && isChecked) {
            markNumbers.setChecked(false);
            markLines.setChecked(false);
            checkNotes.setChecked(false);
        } else if ((buttonView.equals(markLines) || buttonView.equals(markNumbers) || buttonView.equals(checkNotes)) && isChecked)
            powerMode.setChecked(false);
    }

    @Override
    public void onProductPurchased(String productId, TransactionDetails details) {
        Data.instance(this).saveBoolean(Data.SETTINGS_SUPPORTER, true);
        new CustomToast(this, getResources().getString(R.string.settings_support_thanks)).show();
    }

    @Override
    public void onPurchaseHistoryRestored() {
    }

    @Override
    public void onBillingError(int errorCode, Throwable error) {
        if (errorCode == com.anjlab.android.iab.v3.Constants.BILLING_RESPONSE_RESULT_ITEM_ALREADY_OWNED) {
            Data.instance(this).saveBoolean(Data.SETTINGS_SUPPORTER, true);
            new CustomToast(this, getResources().getString(R.string.settings_support_already)).show();
        }
    }

    @Override
    public void onBillingInitialized() {
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!bp.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}