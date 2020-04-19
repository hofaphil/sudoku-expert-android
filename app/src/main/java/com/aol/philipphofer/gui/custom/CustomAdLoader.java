package com.aol.philipphofer.gui.custom;

import android.content.Context;
import android.view.ViewManager;

import com.aol.philipphofer.persistence.Data;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class CustomAdLoader {

    public static void loadAd(Context context, AdView adView) {
        if(!Data.instance(context).loadBoolean(Data.SETTINGS_SUPPORTER, false)) {
            AdRequest adRequest = new AdRequest.Builder().build();
            adView.loadAd(adRequest);
        } else
            ((ViewManager) adView.getParent()).removeView(adView);
    }
}
