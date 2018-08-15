package com.randomappsinc.randomnumbergeneratorplus.utils;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.text.Html;

import com.randomappsinc.randomnumbergeneratorplus.R;

public class TextUtils {

    public interface SnackbarDisplay {
        void showSnackbar(String message);
    }

    public static void copyResultsToClipboard(String text, SnackbarDisplay snackbarDisplay, Context context) {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Activity.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(context.getString(R.string.generated_numbers), text);
        if (clipboard != null) {
            clipboard.setPrimaryClip(clip);
            snackbarDisplay.showSnackbar(context.getString(R.string.results_copied_to_clipboard));
        } else {
            snackbarDisplay.showSnackbar(context.getString(R.string.clipboard_fail));
        }
    }

    public static void copyHistoryRecordToClipboard(String text, Context context) {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Activity.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(context.getString(R.string.record), text);
        if (clipboard != null) {
            clipboard.setPrimaryClip(clip);
            ToastUtil.showLongToast(R.string.record_copied, context);
        } else {
            ToastUtil.showLongToast(R.string.clipboard_fail, context);
        }
    }

    @SuppressWarnings("deprecation")
    public static String stripHtml(String html) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            return Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY).toString();
        } else {
            return Html.fromHtml(html).toString();
        }
    }
}
