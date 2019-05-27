package com.randomappsinc.randomnumbergeneratorplus.utils;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import androidx.annotation.NonNull;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.randomappsinc.randomnumbergeneratorplus.R;
import com.randomappsinc.randomnumbergeneratorplus.persistence.PreferencesManager;
import com.randomappsinc.randomnumbergeneratorplus.theme.ThemeManager;

public class DialogUtils {

    public static void showHomepageDialog(
            final Activity activity,
            PreferencesManager preferencesManager,
            boolean isDarkModeEnabled,
            final ThemeManager themeManager) {
        if (preferencesManager.shouldAskForRating()) {
            new MaterialDialog.Builder(activity)
                    .theme(isDarkModeEnabled ? Theme.DARK : Theme.LIGHT)
                    .content(R.string.please_rate)
                    .negativeText(R.string.decline_rating)
                    .positiveText(R.string.will_rate)
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            Uri uri = Uri.parse(
                                    "market://details?id=" + activity.getApplicationContext().getPackageName());
                            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                            if (!(activity.getPackageManager().queryIntentActivities(intent, 0).size() > 0)) {
                                ToastUtil.showLongToast(R.string.play_store_error, activity);
                                return;
                            }
                            activity.startActivity(intent);
                        }
                    })
                    .show();
        } else if (preferencesManager.shouldShowShake()) {
            new MaterialDialog.Builder(activity)
                    .theme(isDarkModeEnabled ? Theme.DARK : Theme.LIGHT)
                    .title(R.string.shake_it)
                    .content(R.string.shake_now_supported)
                    .positiveText(android.R.string.yes)
                    .cancelable(false)
                    .show();
        } else if (preferencesManager.shouldTeachAboutDarkMode()) {
            preferencesManager.setShouldTeachAboutDarkMode(false);
            new MaterialDialog.Builder(activity)
                    .theme(Theme.LIGHT)
                    .title(R.string.eyes_getting_tired)
                    .content(R.string.dark_mode_explanation)
                    .positiveText(R.string.sure_lets_do_it)
                    .negativeText(R.string.maybe_later)
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            themeManager.setDarkModeEnabled(activity, true);
                        }
                    })
                    .cancelable(false)
                    .show();
        }
    }
}
