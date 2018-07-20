package com.randomappsinc.randomnumbergeneratorplus.dialogs;

import android.content.Context;

import com.afollestad.materialdialogs.MaterialDialog;
import com.randomappsinc.randomnumbergeneratorplus.R;
import com.randomappsinc.randomnumbergeneratorplus.adapters.HistoryAdapter;
import com.randomappsinc.randomnumbergeneratorplus.utils.SimpleDividerItemDecoration;

public class HistoryDialog {

    private MaterialDialog dialog;
    private HistoryAdapter historyAdapter;

    public HistoryDialog(Context context) {
        historyAdapter = new HistoryAdapter();
        dialog = new MaterialDialog.Builder(context)
                .title(R.string.history)
                .adapter(historyAdapter, null)
                .positiveText(R.string.dismiss)
                .build();
        dialog.getRecyclerView().addItemDecoration(new SimpleDividerItemDecoration(context));
    }

    public void addItem(String item) {
        historyAdapter.addItem(item);
    }

    public void show() {
        dialog.show();
    }
}
