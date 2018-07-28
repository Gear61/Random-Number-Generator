package com.randomappsinc.randomnumbergeneratorplus.dialogs;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;

import com.afollestad.materialdialogs.MaterialDialog;
import com.randomappsinc.randomnumbergeneratorplus.R;
import com.randomappsinc.randomnumbergeneratorplus.adapters.HistoryAdapter;
import com.randomappsinc.randomnumbergeneratorplus.constants.RNGType;
import com.randomappsinc.randomnumbergeneratorplus.persistence.HistoryDataManager;
import com.randomappsinc.randomnumbergeneratorplus.utils.SimpleDividerItemDecoration;

import java.util.List;
import java.util.UUID;

public class HistoryDialog {

    private MaterialDialog dialog;
    private HistoryAdapter historyAdapter;
    private @RNGType int currentRngType;

    public HistoryDialog(Context context, @RNGType final int rngType) {
        this.currentRngType = rngType;
        historyAdapter = new HistoryAdapter(rngType == RNGType.LOTTO);
        dialog = new MaterialDialog.Builder(context)
                .title(R.string.history)
                .content(R.string.no_history)
                .adapter(historyAdapter, null)
                .positiveText(R.string.dismiss)
                .build();
        dialog.getRecyclerView().addItemDecoration(new SimpleDividerItemDecoration(context));

        final HistoryDataManager historyDataManager = HistoryDataManager.get();
        HandlerThread handlerThread = new HandlerThread(UUID.randomUUID().toString());
        handlerThread.start();
        Handler backgroundHandler = new Handler(handlerThread.getLooper());
        backgroundHandler.post(new Runnable() {
            @Override
            public void run() {
                List<CharSequence> history = historyDataManager.getHistory(rngType);
                if (!history.isEmpty()) {
                    dialog.setContent(R.string.history_explanation);
                }
                historyAdapter.setItems(history);
            }
        });

        historyDataManager.addListener(listener);
    }

    private final HistoryDataManager.Listener listener = new HistoryDataManager.Listener() {
        @Override
        public void onHistoryRecordAdded(int rngType, String recordText) {
            if (currentRngType == rngType) {
                historyAdapter.addItem(recordText);
                dialog.setContent(R.string.history_explanation);
            }
        }
    };

    public void show() {
        dialog.show();
    }
}
