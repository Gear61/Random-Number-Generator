package com.randomappsinc.randomnumbergeneratorplus.Persistence;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.randomappsinc.randomnumbergeneratorplus.R;
import com.randomappsinc.randomnumbergeneratorplus.Utils.MyApplication;

/**
 * Created by alexanderchiou on 1/1/16.
 */
public class PreferencesManager {
    private static final String DEFAULT_CONFIG = "defaultConfig";
    private static final String NUM_APP_OPENS = "numAppOpens";
    private static final String PLAY_SOUNDS = "playSounds";

    // Dice
    private static final String NUM_SIDES = "numSides";
    private static final String NUM_DICE = "numDice";

    // Coins
    private static final String NUM_COINS = "numCoins";
    private static final int DEFAULT_NUM_COINS = 1;

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

    public String getNumSides() {
        int defaultNumSides = Integer.parseInt(MyApplication.getAppContext().getString(R.string.default_sides));
        return String.valueOf(prefs.getInt(NUM_SIDES, defaultNumSides));
    }

    public String getNumDice() {
        int defaultNumDice = Integer.parseInt(MyApplication.getAppContext().getString(R.string.default_num_dice));
        return String.valueOf(prefs.getInt(NUM_DICE, defaultNumDice));
    }

    public void saveDiceSettings(String numSides, String numDice) {
        Context context = MyApplication.getAppContext();
        try {
            prefs.edit().putInt(NUM_SIDES, Integer.parseInt(numSides)).apply();
            prefs.edit().putInt(NUM_DICE, Integer.parseInt(numDice)).apply();
        } catch (NumberFormatException exception) {
            prefs.edit().putInt(NUM_SIDES, Integer.parseInt(context.getString(R.string.default_sides))).apply();
            prefs.edit().putInt(NUM_DICE, Integer.parseInt(context.getString(R.string.default_num_dice))).apply();
        }
    }

    public int getNumCoins() {
        return prefs.getInt(NUM_COINS, DEFAULT_NUM_COINS);
    }

    public void saveNumCoins(String numCoins) {
        try {
            prefs.edit().putInt(NUM_COINS, Integer.parseInt(numCoins)).apply();
        } catch (NumberFormatException ignored) {}
    }

    public boolean shouldPlaySounds() {
        return prefs.getBoolean(PLAY_SOUNDS, true);
    }

    public void setPlaySounds(boolean shouldPlay) {
        prefs.edit().putBoolean(PLAY_SOUNDS, shouldPlay).apply();
    }
}
