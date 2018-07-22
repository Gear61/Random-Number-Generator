package com.randomappsinc.randomnumbergeneratorplus.utils;

import android.support.annotation.StringRes;
import android.widget.Toast;

public class ToastUtil {

    public static void showLongToast(@StringRes int textResId) {
        showToast(textResId, Toast.LENGTH_LONG);
    }

    private static void showToast(@StringRes int textResId, int duration) {
        Toast.makeText(MyApplication.getAppContext(), textResId, duration).show();
    }
}
