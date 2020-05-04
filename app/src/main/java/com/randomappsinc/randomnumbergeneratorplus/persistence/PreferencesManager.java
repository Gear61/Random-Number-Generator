package com.randomappsinc.randomnumbergeneratorplus.persistence;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.randomappsinc.randomnumbergeneratorplus.constants.SortType;
import com.randomappsinc.randomnumbergeneratorplus.models.RNGSettings;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PreferencesManager {

    private static final String NUM_APP_OPENS = "numAppOpens";
    private static final String PLAY_SOUNDS = "playSounds";
    private static final String ASK_FOR_MUTE = "askForMute";
    private static final String ENABLE_SHAKE = "enableShake";
    private static final String DARK_MODE_ENABLED = "darkModeEnabled";
    private static final String SHOULD_TEACH_ABOUT_DARK_MODE = "shouldTeachAboutDarkMode";

    // RNG
    private static final String MINIMUM_KEY = "minimum";
    private static final String MAXIMUM_KEY = "maximum";
    private static final String NUM_NUMBERS_KEY = "numNumbers";
    private static final String EXCLUDED_NUMBERS_KEY = "excludedNumbers";
    private static final String SORT_TYPE_KEY = "sortType";
    private static final String NO_DUPES_KEY = "noDupes";
    private static final String SHOW_SUM_KEY = "showSum";
    private static final String HIDE_EXCLUDED_KEY = "hideExcluded";

    private static final int DEFAULT_MIN = 1;
    private static final int DEFAULT_MAX = 100;
    private static final int DEFAULT_NUM_NUMBERS = 1;
    private static final Set<String> DEFAULT_EXCLUDED_NUMBERS = new HashSet<>();
    private static final @SortType int DEFAULT_SORT_TYPE = SortType.NONE;
    private static final boolean DEFAULT_NO_DUPES = false;
    private static final boolean DEFAULT_SHOW_SUM = false;
    private static final boolean DEFAULT_HIDE_EXCLUDED = false;

    // Dice
    private static final String NUM_SIDES = "numSides";
    private static final String NUM_DICE = "numDice";
    private static final int DEFAULT_NUM_DICE = 2;
    private static final int DEFAULT_NUM_DICE_SIDES = 6;

    // Coins
    private static final String NUM_COINS = "numCoins";
    private static final int DEFAULT_NUM_COINS = 1;

    private SharedPreferences prefs;

    public PreferencesManager(Context context) {
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public boolean shouldAskForRating() {
        int currentAppOpens = prefs.getInt(NUM_APP_OPENS, 0);
        currentAppOpens++;
        prefs.edit().putInt(NUM_APP_OPENS, currentAppOpens).apply();
        return currentAppOpens == 5;
    }

    public String getNumSides() {
        return String.valueOf(prefs.getInt(NUM_SIDES, DEFAULT_NUM_DICE_SIDES));
    }

    public String getNumDice() {
        return String.valueOf(prefs.getInt(NUM_DICE, DEFAULT_NUM_DICE));
    }

    public void saveDiceSettings(String numSides, String numDice) {
        try {
            prefs.edit().putInt(NUM_SIDES, Integer.parseInt(numSides)).apply();
            prefs.edit().putInt(NUM_DICE, Integer.parseInt(numDice)).apply();
        } catch (NumberFormatException exception) {
            prefs.edit().putInt(NUM_SIDES, DEFAULT_NUM_DICE_SIDES).apply();
            prefs.edit().putInt(NUM_DICE, DEFAULT_NUM_DICE).apply();
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

    public boolean shouldAskForMute() {
        boolean shouldAsk = prefs.getBoolean(ASK_FOR_MUTE, true);
        if (shouldAsk) {
            prefs.edit().putBoolean(ASK_FOR_MUTE, false).apply();
        }
        return shouldAsk;
    }

    public boolean isShakeEnabled() {
        return prefs.getBoolean(ENABLE_SHAKE, false);
    }

    public void setShakeEnabled(boolean enableShake) {
        prefs.edit().putBoolean(ENABLE_SHAKE, enableShake).apply();
    }

    public RNGSettings getRNGSettings() {
        RNGSettings rngSettings = new RNGSettings();
        rngSettings.setMinimum(prefs.getInt(MINIMUM_KEY, DEFAULT_MIN));
        rngSettings.setMaximum(prefs.getInt(MAXIMUM_KEY, DEFAULT_MAX));
        rngSettings.setNumNumbers(prefs.getInt(NUM_NUMBERS_KEY, DEFAULT_NUM_NUMBERS));

        Set<String> excludedNumberStrings = prefs.getStringSet(EXCLUDED_NUMBERS_KEY, DEFAULT_EXCLUDED_NUMBERS);
        ArrayList<Integer> excludedNumbers = new ArrayList<>();
        for (String number : excludedNumberStrings) {
            excludedNumbers.add(Integer.valueOf(number));
        }
        Collections.sort(excludedNumbers);
        rngSettings.setExcludedNumbers(excludedNumbers);

        rngSettings.setSortType(prefs.getInt(SORT_TYPE_KEY, DEFAULT_SORT_TYPE));
        rngSettings.setNoDupes(prefs.getBoolean(NO_DUPES_KEY, DEFAULT_NO_DUPES));
        rngSettings.setShowSum(prefs.getBoolean(SHOW_SUM_KEY, DEFAULT_SHOW_SUM));
        rngSettings.setHideExcluded(prefs.getBoolean(HIDE_EXCLUDED_KEY, DEFAULT_HIDE_EXCLUDED));
        return rngSettings;
    }

    public void saveRNGSettings(RNGSettings rngSettings) {
        List<Integer> excludedNumbers = rngSettings.getExcludedNumbers();
        Set<String> excludedNumberStrings = new HashSet<>();
        for (Integer number : excludedNumbers) {
            excludedNumberStrings.add(String.valueOf(number));
        }

        prefs.edit().putInt(MINIMUM_KEY, rngSettings.getMinimum())
                .putInt(MAXIMUM_KEY, rngSettings.getMaximum())
                .putInt(NUM_NUMBERS_KEY, rngSettings.getNumNumbers())
                .putStringSet(EXCLUDED_NUMBERS_KEY, excludedNumberStrings)
                .putInt(SORT_TYPE_KEY, rngSettings.getSortType())
                .putBoolean(NO_DUPES_KEY, rngSettings.isNoDupes())
                .putBoolean(SHOW_SUM_KEY, rngSettings.isShowSum())
                .putBoolean(HIDE_EXCLUDED_KEY, rngSettings.isHideExcluded())
                .apply();
    }

    public boolean getDarkModeEnabled() {
        return prefs.getBoolean(DARK_MODE_ENABLED, false);
    }

    public void setDarkModeEnabled(boolean darkModeEnabled) {
        prefs.edit().putBoolean(DARK_MODE_ENABLED, darkModeEnabled).apply();
    }

    public boolean shouldTeachAboutDarkMode() {
        boolean darkModeDisabled = !getDarkModeEnabled();
        return darkModeDisabled && prefs.getBoolean(SHOULD_TEACH_ABOUT_DARK_MODE, true);
    }

    public void setShouldTeachAboutDarkMode(boolean shouldTeachAboutDarkMode) {
        prefs.edit().putBoolean(SHOULD_TEACH_ABOUT_DARK_MODE, shouldTeachAboutDarkMode).apply();
    }
}
