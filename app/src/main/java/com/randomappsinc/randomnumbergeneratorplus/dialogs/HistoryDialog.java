package com.randomappsinc.randomnumbergeneratorplus.dialogs;

import android.content.Context;

import com.afollestad.materialdialogs.MaterialDialog;
import com.randomappsinc.randomnumbergeneratorplus.R;
import com.randomappsinc.randomnumbergeneratorplus.adapters.HistoryAdapter;
import com.randomappsinc.randomnumbergeneratorplus.utils.SimpleDividerItemDecoration;
import com.randomappsinc.randomnumbergeneratorplus.utils.TextUtils.SnackbarDisplay;

public class HistoryDialog {

    private MaterialDialog dialog;
    private HistoryAdapter historyAdapter;

    public HistoryDialog(Context context, SnackbarDisplay snackbarDisplay) {
        historyAdapter = new HistoryAdapter(snackbarDisplay);
        dialog = new MaterialDialog.Builder(context)
                .title(R.string.history)
                .content(R.string.history_explanation)
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
