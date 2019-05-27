package com.randomappsinc.randomnumbergeneratorplus.utils;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.StringRes;

public class ToastUtil {

    public static void showLongToast(@StringRes int textResId, Context context) {
        showToast(textResId, Toast.LENGTH_LONG, context);
    }

    private static void showToast(@StringRes int textResId, int duration, Context context) {
        Toast.makeText(context, textResId, duration).show();
    }
}
