package com.randomappsinc.randomnumbergeneratorplus.utils;

import com.randomappsinc.randomnumbergeneratorplus.constants.RNGType;

import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Utility class to broadcast shake detected signals because I don't get fragments.
 */
public class ShakeManager {

    public interface Listener {
        void onShakeDetected(@RNGType int currentRngPage);
    }

    private static ShakeManager instance;

    public static ShakeManager get() {
        if (instance == null) {
            instance = getSync();
        }
        return instance;
    }

    private static synchronized ShakeManager getSync() {
        if (instance == null) {
            instance = new ShakeManager();
        }
        return instance;
    }

    private CopyOnWriteArraySet<Listener> listeners;

    private ShakeManager() {
        listeners = new CopyOnWriteArraySet<>();
    }

    public void registerListener(Listener listener) {
        listeners.add(listener);
    }

    public void unregisterListener(Listener listener) {
        listeners.remove(listener);
    }

    public void onShakeDetected(@RNGType int currentRngPage) {
        for (Listener listener : listeners) {
            listener.onShakeDetected(currentRngPage);
        }
    }
}
