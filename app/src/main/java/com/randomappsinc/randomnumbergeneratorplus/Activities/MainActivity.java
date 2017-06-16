package com.randomappsinc.randomnumbergeneratorplus.Activities;

import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.randomappsinc.randomnumbergeneratorplus.Adapters.HomepageTabsAdapter;
import com.randomappsinc.randomnumbergeneratorplus.Persistence.PreferencesManager;
import com.randomappsinc.randomnumbergeneratorplus.R;
import com.randomappsinc.randomnumbergeneratorplus.Utils.MyApplication;
import com.randomappsinc.randomnumbergeneratorplus.Utils.UIUtils;

import butterknife.Bind;
import butterknife.BindColor;
import butterknife.ButterKnife;
import butterknife.OnPageChange;

public class MainActivity extends StandardActivity {
    @Bind(R.id.parent) View parent;
    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.tab_layout) TabLayout homeTabs;
    @Bind(R.id.view_pager) ViewPager homePager;
    @BindColor(R.color.app_blue) int blue;

    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homepage);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        setTitle(" ");

        mediaPlayer = new MediaPlayer();

        homePager.setAdapter(new HomepageTabsAdapter(getFragmentManager()));
        homePager.setOffscreenPageLimit(3);
        homeTabs.setupWithViewPager(homePager);

        if (PreferencesManager.get().shouldAskForRating()) {
            new MaterialDialog.Builder(this)
                    .content(R.string.please_rate)
                    .negativeText(R.string.decline_rating)
                    .positiveText(R.string.will_rate)
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            Uri uri = Uri.parse("market://details?id=" + getApplicationContext().getPackageName());
                            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                            if (!(getPackageManager().queryIntentActivities(intent, 0).size() > 0)) {
                                UIUtils.showSnackbar(parent, getString(R.string.play_store_error));
                                return;
                            }
                            startActivity(intent);
                        }
                    })
                    .show();
        }
    }

    public void showSnackbar(String message) {
        UIUtils.showSnackbar(parent, message);
    }

    public View getParentView() {
        return parent;
    }

    @OnPageChange(R.id.view_pager)
    public void onPageChange() {
        UIUtils.hideKeyboard(this);
    }

    public void playSound(String filePath) {
        try {
            mediaPlayer.reset();
            AssetFileDescriptor fileDescriptor = MyApplication.getAppContext().getAssets().openFd(filePath);
            mediaPlayer.setDataSource(fileDescriptor.getFileDescriptor(), fileDescriptor.getStartOffset(), fileDescriptor.getLength());
            mediaPlayer.prepare();
            mediaPlayer.start();
            if (PreferencesManager.get().shouldAskForMute()) {
                askToMute();
            }
        } catch (Exception ignored) {}
    }

    public void askToMute() {
        Snackbar snackbar = Snackbar.make(parent, R.string.dislike_sound, Snackbar.LENGTH_LONG);
        View rootView = snackbar.getView();
        snackbar.getView().setBackgroundColor(blue);
        TextView tv = (TextView) rootView.findViewById(android.support.design.R.id.snackbar_text);
        tv.setTextColor(Color.WHITE);
        snackbar.setAction(R.string.turn_off, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PreferencesManager.get().setPlaySounds(false);
                showSnackbar(getString(R.string.sounds_muted));
            }
        });
        snackbar.setActionTextColor(Color.WHITE);
        snackbar.show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mediaPlayer.stop();
    }
}
