package com.hardrubic.application;

import android.app.Application;
import android.content.Context;

import com.facebook.stetho.Stetho;
import com.hardrubic.util.network.PreferencesUtils;


public class AppApplication extends Application{
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();

        PreferencesUtils.initializeInstance(getApplicationContext());
        Stetho.initializeWithDefaults(this);
    }

    public static Context getContext() {
        return context;
    }
}
