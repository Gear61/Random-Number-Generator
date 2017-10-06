package com.randomappsinc.randomnumbergeneratorplus.utils;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;

import com.randomappsinc.randomnumbergeneratorplus.R;

public class TextUtils {

    public interface SnackbarDisplay {
        void showSnackbar(String message);
    }

    public static void copyTextToClipboard(String text, SnackbarDisplay snackbarDisplay) {
        Context context = MyApplication.getAppContext();

        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Activity.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(context.getString(R.string.generated_numbers), text);

        if (clipboard != null) {
            clipboard.setPrimaryClip(clip);
            snackbarDisplay.showSnackbar(context.getString(R.string.copied_to_clipboard));
        } else {
            snackbarDisplay.showSnackbar(context.getString(R.string.clipboard_fail));
        }
    }
}
