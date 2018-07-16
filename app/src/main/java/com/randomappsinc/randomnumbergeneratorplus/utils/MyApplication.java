package com.randomappsinc.randomnumbergeneratorplus.utils;

import android.app.Application;
import android.content.Context;

import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeModule;
import com.joanzapata.iconify.fonts.IoniconsModule;

import java.util.Random;

public final class MyApplication extends Application {

    private static Context instance;
    private static Random random;

    @Override
    public void onCreate() {
        super.onCreate();
        Iconify.with(new IoniconsModule())
               .with(new FontAwesomeModule());
        random = new Random();
        instance = getApplicationContext();
    }

    public static Context getAppContext() {
        return instance;
    }

    public static Random getRandom() {
        return random;
    }
}
