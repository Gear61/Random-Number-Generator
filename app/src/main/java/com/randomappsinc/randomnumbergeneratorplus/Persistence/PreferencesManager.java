package com.randomappsinc.randomnumbergeneratorplus.Persistence;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.randomappsinc.randomnumbergeneratorplus.Utils.MyApplication;

/**
 * Created by alexanderchiou on 1/1/16.
 */
public class PreferencesManager {
    private static final String FIRST_TIME_USER = "firstTimeUser";
    private static final String DEFAULT_CONFIG = "defaultConfig";

    private SharedPreferences prefs;
    private static PreferencesManager instance;

    public static PreferencesManager get() {
        if (instance == null) {
            instance = getSync();
        }
        return instance;
    }

    private static synchronized PreferencesManager getSync() {
        if (instance == null) {
            instance = new PreferencesManager();
        }
        return instance;
    }

    private PreferencesManager() {
        Context context = MyApplication.getAppContext();
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public boolean isFirstTimeUser() {
        return prefs.getBoolean(FIRST_TIME_USER, true);
    }

    public void rememberWelcome() {
        prefs.edit().putBoolean(FIRST_TIME_USER, false).apply();
    }


}

