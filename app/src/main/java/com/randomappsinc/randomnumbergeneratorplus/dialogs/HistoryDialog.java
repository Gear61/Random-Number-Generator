package com.randomappsinc.randomnumbergeneratorplus.dialogs;

import android.content.Context;
import android.util.SparseArray;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.randomappsinc.randomnumbergeneratorplus.R;
import com.randomappsinc.randomnumbergeneratorplus.adapters.HistoryAdapter;
import com.randomappsinc.randomnumbergeneratorplus.constants.RNGType;
import com.randomappsinc.randomnumbergeneratorplus.persistence.HistoryDataManager;
import com.randomappsinc.randomnumbergeneratorplus.utils.SimpleDividerItemDecoration;

import java.util.List;

public class HistoryDialog {

    private MaterialDialog dialog;
    private HistoryAdapter historyAdapter;
    private @RNGType int currentRngType;
    private HistoryDataManager historyDataManager;

    public HistoryDialog(Context context, @RNGType final int rngType, boolean isDarkModeEnabled) {
        this.currentRngType = rngType;
        historyAdapter = new HistoryAdapter(rngType == RNGType.LOTTO);
        resetDialog(context, isDarkModeEnabled);
        dialog.getRecyclerView().addItemDecoration(new SimpleDividerItemDecoration(context));
        historyDataManager = HistoryDataManager.get(context);
        historyDataManager.addListener(listener);
    }

    public void resetDialog(Context context, boolean isDarkModeEnabled) {
        dialog = new MaterialDialog.Builder(context)
                .theme(isDarkModeEnabled ? Theme.DARK : Theme.LIGHT)
                .title(getTitleResource())
                .content(R.string.no_history)
                .adapter(historyAdapter, null)
                .positiveText(R.string.dismiss)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                })
                .neutralText(R.string.clear)
                .onNeutral(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        historyDataManager.deleteHistory(currentRngType);
                        historyAdapter.clear();
                        dialog.setContent(R.string.no_history);
                    }
                })
                .autoDismiss(false)
                .build();
        dialog.getRecyclerView().addItemDecoration(new SimpleDividerItemDecoration(context));
        if (historyAdapter.getItemCount() > 0) {
            dialog.setContent(R.string.history_explanation);
        }
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
        public void onInitialHistoryDataFetched(SparseArray<List<CharSequence>> historyRecords) {
            List<CharSequence> history = historyRecords.get(currentRngType);
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
