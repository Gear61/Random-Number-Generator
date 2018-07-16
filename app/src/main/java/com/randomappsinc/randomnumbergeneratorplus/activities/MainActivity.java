package com.randomappsinc.randomnumbergeneratorplus.activities;

import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Color;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.joanzapata.iconify.fonts.IoniconsIcons;
import com.randomappsinc.randomnumbergeneratorplus.R;
import com.randomappsinc.randomnumbergeneratorplus.adapters.HomepageTabsAdapter;
import com.randomappsinc.randomnumbergeneratorplus.persistence.PreferencesManager;
import com.randomappsinc.randomnumbergeneratorplus.utils.MyApplication;
import com.randomappsinc.randomnumbergeneratorplus.utils.UIUtils;
import com.squareup.seismic.ShakeDetector;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnPageChange;

public class MainActivity extends StandardActivity implements ShakeDetector.Listener {

    @BindView(R.id.parent) View parent;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.tab_layout) TabLayout homeTabs;
    @BindView(R.id.view_pager) ViewPager homePager;
    @BindColor(R.color.app_blue) int blue;

    private HomepageTabsAdapter tabsAdapter;
    private MediaPlayer mediaPlayer;
    private ShakeDetector shakeDetector;
    private boolean disableGeneration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homepage);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                disableGeneration = false;
            }
        });

        tabsAdapter = new HomepageTabsAdapter(getSupportFragmentManager());
        homePager.setAdapter(tabsAdapter);
        homePager.setOffscreenPageLimit(3);
        homeTabs.setupWithViewPager(homePager);

        shakeDetector = new ShakeDetector(this);

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
        } else if (PreferencesManager.get().shouldShowShake()) {
            new MaterialDialog.Builder(this)
                    .title(R.string.shake_it)
                    .content(R.string.shake_now_supported)
                    .positiveText(android.R.string.yes)
                    .cancelable(false)
                    .show();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        if (PreferencesManager.get().isShakeEnabled()) {
            shakeDetector.stop();
        }
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (PreferencesManager.get().isShakeEnabled()) {
            shakeDetector.start((SensorManager) getSystemService(SENSOR_SERVICE));
        }
    }

    public void showSnackbar(String message) {
        UIUtils.showSnackbar(parent, message);
    }

    @OnPageChange(R.id.view_pager)
    public void onPageChange() {
        UIUtils.hideKeyboard(this);
    }

    public void playSound(String filePath) {
        try {
            mediaPlayer.reset();
            AssetFileDescriptor fileDescriptor = MyApplication.getAppContext().getAssets().openFd(filePath);
            mediaPlayer.setDataSource(
                    fileDescriptor.getFileDescriptor(),
                    fileDescriptor.getStartOffset(),
                    fileDescriptor.getLength());
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
        TextView tv = rootView.findViewById(android.support.design.R.id.snackbar_text);
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
    public void hearShake() {
        if (PreferencesManager.get().shouldPlaySounds()) {
            if (!disableGeneration) {
                disableGeneration = true;
                tabsAdapter.generate(homePager.getCurrentItem());
            }
        } else {
            tabsAdapter.generate(homePager.getCurrentItem());
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (PreferencesManager.get().isShakeEnabled()) {
            shakeDetector.stop();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mediaPlayer.stop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        UIUtils.loadMenuIcon(menu, R.id.settings, IoniconsIcons.ion_android_settings);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
