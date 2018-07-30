package com.randomappsinc.randomnumbergeneratorplus.persistence;

import android.os.Handler;
import android.os.HandlerThread;

import com.randomappsinc.randomnumbergeneratorplus.constants.RNGType;
import com.randomappsinc.randomnumbergeneratorplus.models.HistoryRecord;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class HistoryDataManager {

    public interface Listener {
        void onInitialHistoryDataFetched(List<HistoryRecord> historyRecords);

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

    public void getInitialHistory() {
        HandlerThread handlerThread = new HandlerThread(UUID.randomUUID().toString());
        handlerThread.start();
        Handler backgroundHandler = new Handler(handlerThread.getLooper());
        backgroundHandler.post(new Runnable() {
            @Override
            public void run() {
                List<HistoryRecord> historyRecords = dataSource.getHistory();
                for (Listener listener : listeners) {
                    listener.onInitialHistoryDataFetched(historyRecords);
                }
            }
        });
    }
}
