package com.randomappsinc.randomnumbergeneratorplus.Persistence;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.randomappsinc.randomnumbergeneratorplus.Utils.MyApplication;

/**
 * Created by alexanderchiou on 1/1/16.
 */
public class PreferencesManager {
    private static final String DEFAULT_CONFIG = "defaultConfig";
    private static final String NUM_APP_OPENS = "numAppOpens";

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

    public String getDefaultConfig() {
        return prefs.getString(DEFAULT_CONFIG, "");
    }

    public void setDefaultConfig(String configName) {
        prefs.edit().putString(DEFAULT_CONFIG, configName).apply();
    }

    public boolean shouldAskForRating() {
        int currentAppOpens = prefs.getInt(NUM_APP_OPENS, 0);
        currentAppOpens++;
        prefs.edit().putInt(NUM_APP_OPENS, currentAppOpens).apply();
        return currentAppOpens == 5;
    }
}

