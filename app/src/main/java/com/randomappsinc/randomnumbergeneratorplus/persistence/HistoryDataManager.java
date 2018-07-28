package com.randomappsinc.randomnumbergeneratorplus.persistence;

import com.randomappsinc.randomnumbergeneratorplus.constants.RNGType;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class HistoryDataManager {

    public interface Listener {
        void onHistoryRecordAdded(@RNGType int rngType, String recordText);
    }

    private static HistoryDataManager instance;

    private HistoryDataSource dataSource;
    private Set<Listener> listeners;

    public static HistoryDataManager get() {
        if (instance == null) {
            instance = getSync();
        }
        return instance;
    }

    private static synchronized HistoryDataManager getSync() {
        if (instance == null) {
            instance = new HistoryDataManager();
        }
        return instance;
    }

    private HistoryDataManager() {
        dataSource = HistoryDataSource.get();
        listeners = new HashSet<>();
    }

    public void addListener(Listener listener) {
        listeners.add(listener);
    }

    public void addHistoryRecord(@RNGType int rngType, String recordText) {
        dataSource.addHistoryRecord(rngType, recordText);
        for (Listener listener : listeners) {
            listener.onHistoryRecordAdded(rngType, recordText);
        }
    }

    public List<CharSequence> getHistory(@RNGType int rngType) {
        return dataSource.getHistory(rngType);
    }
}
