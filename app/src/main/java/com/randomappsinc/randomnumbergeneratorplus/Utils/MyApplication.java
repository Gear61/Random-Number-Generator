package com.randomappsinc.randomnumbergeneratorplus.Utils;

import android.app.Application;
import android.content.Context;

import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeModule;
import com.joanzapata.iconify.fonts.IoniconsModule;

import java.util.Random;

/**
 * Created by alexanderchiou on 12/30/15.
 */
public final class MyApplication extends Application {
    private static Context instance;
    private static Random random;

    @Override
    public void onCreate() {
        super.onCreate();
        Iconify.with(new IoniconsModule())
                .with(new FontAwesomeModule());
        instance = getApplicationContext();
        random = new Random();
    }

    public static Context getAppContext() {
        return instance;
    }

    public static Random getRandom() {
        return random;
    }
}
