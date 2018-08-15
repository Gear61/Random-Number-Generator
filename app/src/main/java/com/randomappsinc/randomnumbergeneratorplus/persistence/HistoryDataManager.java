package com.randomappsinc.randomnumbergeneratorplus.persistence;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.SparseArray;

import com.randomappsinc.randomnumbergeneratorplus.constants.RNGType;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class HistoryDataManager {

    public interface Listener {
        void onInitialHistoryDataFetched(SparseArray<List<CharSequence>> historyRecords);

        void onHistoryRecordAdded(@RNGType int rngType, String recordText);
    }

    private static HistoryDataManager instance;

    private HistoryDataSource dataSource;
    private Set<Listener> listeners;

    public static HistoryDataManager get(Context context) {
        if (instance == null) {
            instance = getSync(context);
        }
        return instance;
    }

    private static synchronized HistoryDataManager getSync(Context context) {
        if (instance == null) {
            instance = new HistoryDataManager(context);
        }
        return instance;
    }

    private HistoryDataManager(Context context) {
        dataSource = HistoryDataSource.get(context);
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
                SparseArray<List<CharSequence>> historyRecords = dataSource.getHistory();
                for (Listener listener : listeners) {
                    listener.onInitialHistoryDataFetched(historyRecords);
                }
            }
        });
    }

    public void deleteHistory(@RNGType int rngType) {
        dataSource.deleteHistory(rngType);
    }
}
