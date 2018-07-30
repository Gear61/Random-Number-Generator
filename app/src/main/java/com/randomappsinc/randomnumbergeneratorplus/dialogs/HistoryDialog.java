package com.randomappsinc.randomnumbergeneratorplus.dialogs;

import android.content.Context;
import android.support.annotation.StringRes;

import com.afollestad.materialdialogs.MaterialDialog;
import com.randomappsinc.randomnumbergeneratorplus.R;
import com.randomappsinc.randomnumbergeneratorplus.adapters.HistoryAdapter;
import com.randomappsinc.randomnumbergeneratorplus.constants.RNGType;
import com.randomappsinc.randomnumbergeneratorplus.models.HistoryRecord;
import com.randomappsinc.randomnumbergeneratorplus.persistence.HistoryDataManager;
import com.randomappsinc.randomnumbergeneratorplus.utils.SimpleDividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

public class HistoryDialog {

    private MaterialDialog dialog;
    private HistoryAdapter historyAdapter;
    private @RNGType int currentRngType;

    public HistoryDialog(Context context, @RNGType final int rngType) {
        this.currentRngType = rngType;
        historyAdapter = new HistoryAdapter(rngType == RNGType.LOTTO);
        dialog = new MaterialDialog.Builder(context)
                .title(getTitleResource())
                .content(R.string.no_history)
                .adapter(historyAdapter, null)
                .positiveText(R.string.dismiss)
                .build();
        dialog.getRecyclerView().addItemDecoration(new SimpleDividerItemDecoration(context));
        HistoryDataManager.get().addListener(listener);
    }

    private @StringRes int getTitleResource() {
        switch (currentRngType) {
            case RNGType.NUMBER:
                return R.string.rng_history;
            case RNGType.DICE:
                return R.string.dice_history;
            case RNGType.LOTTO:
                return R.string.lotto_history;
            case RNGType.COINS:
                return R.string.coins_history;
            default:
                return R.string.rng_history;
        }
    }

    private final HistoryDataManager.Listener listener = new HistoryDataManager.Listener() {
        @Override
        public void onInitialHistoryDataFetched(List<HistoryRecord> historyRecords) {
            List<CharSequence> history = new ArrayList<>();
            for (HistoryRecord historyRecord : historyRecords) {
                if (historyRecord.getRngType() == currentRngType) {
                    history.add(historyRecord.getRecordText());
                }
            }
            if (!history.isEmpty()) {
                dialog.setContent(R.string.history_explanation);
            }
            historyAdapter.setInitialItems(history);
        }

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
