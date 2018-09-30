package com.randomappsinc.randomnumbergeneratorplus.theme;

import android.content.Context;

import com.randomappsinc.randomnumbergeneratorplus.persistence.PreferencesManager;

import java.util.HashSet;
import java.util.Set;

public class ThemeManager {

    public interface Listener {
        void onThemeChanged(boolean darkModeEnabled);
    }

    private static ThemeManager instance;

    private Set<Listener> listeners;

    public static ThemeManager get() {
        if (instance == null) {
            instance = getSync();
        }
        return instance;
    }

    private static synchronized ThemeManager getSync() {
        if (instance == null) {
            instance = new ThemeManager();
        }
        return instance;
    }

    private ThemeManager() {
        listeners = new HashSet<>();
    }

    public void registerListener(Listener listener) {
        listeners.add(listener);
    }

    public void unregisterListener(Listener listener) {
        listeners.remove(listener);
    }

    public void setDarkModeEnabled(Context context, boolean darkModeEnabled) {
        PreferencesManager preferencesManager = new PreferencesManager(context);
        preferencesManager.setDarkModeEnabled(darkModeEnabled);
        for (Listener listener : listeners) {
            listener.onThemeChanged(darkModeEnabled);
        }
    }

    public boolean getDarkModeEnabled(Context context) {
        PreferencesManager preferencesManager = new PreferencesManager(context);
        return preferencesManager.getDarkModeEnabled();
    }
}
