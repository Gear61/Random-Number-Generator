package com.randomappsinc.randomnumbergeneratorplus.activities;

import android.content.Intent;
import android.graphics.Color;
import android.hardware.SensorManager;
import android.media.AudioManager;
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
import com.afollestad.materialdialogs.Theme;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;
import com.joanzapata.iconify.fonts.IoniconsIcons;
import com.randomappsinc.randomnumbergeneratorplus.R;
import com.randomappsinc.randomnumbergeneratorplus.adapters.HomepageTabsAdapter;
import com.randomappsinc.randomnumbergeneratorplus.audio.SoundPlayer;
import com.randomappsinc.randomnumbergeneratorplus.constants.RNGType;
import com.randomappsinc.randomnumbergeneratorplus.dialogs.HistoryDialog;
import com.randomappsinc.randomnumbergeneratorplus.persistence.HistoryDataManager;
import com.randomappsinc.randomnumbergeneratorplus.persistence.PreferencesManager;
import com.randomappsinc.randomnumbergeneratorplus.utils.ShakeManager;
import com.randomappsinc.randomnumbergeneratorplus.utils.ToastUtil;
import com.randomappsinc.randomnumbergeneratorplus.utils.UIUtils;
import com.squareup.seismic.ShakeDetector;

import butterknife.BindArray;
import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnPageChange;

public class MainActivity extends StandardActivity implements ShakeDetector.Listener {

    @BindView(R.id.parent) View parent;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.tab_layout) TabLayout homeTabs;
    @BindView(R.id.view_pager) ViewPager homePager;
    @BindArray(R.array.homepage_options) String[] homepageTabStrings;
    @BindColor(R.color.app_blue) int blue;

    private SoundPlayer soundPlayer;
    private ShakeDetector shakeDetector;
    private boolean disableGeneration;
    private ShakeManager shakeManager;
    private PreferencesManager preferencesManager;

    private HistoryDialog rngHistoryDialog;
    private HistoryDialog diceHistoryDialog;
    private HistoryDialog lottoHistoryDialog;
    private HistoryDialog coinsHistoryDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homepage);
        ButterKnife.bind(this);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        setSupportActionBar(toolbar);
        setActionBarColors();

        SoundPlayer.Listener soundListener = new SoundPlayer.Listener() {
            @Override
            public void onAudioComplete() {
                disableGeneration = false;
            }

            @Override
            public void onAudioError() {
                ToastUtil.showLongToast(R.string.sound_fail, MainActivity.this);
            }
        };
        soundPlayer = new SoundPlayer(this, soundListener);

        preferencesManager = new PreferencesManager(this);

        HomepageTabsAdapter tabsAdapter = new HomepageTabsAdapter(getSupportFragmentManager(), homepageTabStrings);
        homePager.setAdapter(tabsAdapter);
        homePager.setOffscreenPageLimit(3);
        homeTabs.setupWithViewPager(homePager);

        shakeManager = ShakeManager.get();
        shakeDetector = new ShakeDetector(this);

        if (preferencesManager.shouldAskForRating()) {
            new MaterialDialog.Builder(this)
                    .theme(isDarkModeEnabled() ? Theme.DARK : Theme.LIGHT)
                    .content(R.string.please_rate)
                    .negativeText(R.string.decline_rating)
                    .positiveText(R.string.will_rate)
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            Uri uri = Uri.parse(
                                    "market://details?id=" + getApplicationContext().getPackageName());
                            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                            if (!(getPackageManager().queryIntentActivities(intent, 0).size() > 0)) {
                                UIUtils.showSnackbar(parent, getString(R.string.play_store_error), MainActivity.this);
                                return;
                            }
                            startActivity(intent);
                        }
                    })
                    .show();
        } else if (preferencesManager.shouldShowShake()) {
            new MaterialDialog.Builder(this)
                    .theme(isDarkModeEnabled() ? Theme.DARK : Theme.LIGHT)
                    .title(R.string.shake_it)
                    .content(R.string.shake_now_supported)
                    .positiveText(android.R.string.yes)
                    .cancelable(false)
                    .show();
        }

        rngHistoryDialog = new HistoryDialog(this, RNGType.NUMBER, isDarkModeEnabled());
        diceHistoryDialog = new HistoryDialog(this, RNGType.DICE, isDarkModeEnabled());
        lottoHistoryDialog = new HistoryDialog(this, RNGType.LOTTO, isDarkModeEnabled());
        coinsHistoryDialog = new HistoryDialog(this, RNGType.COINS, isDarkModeEnabled());
        HistoryDataManager.get(this).getInitialHistory();
    }

    @Override
    public void onThemeChanged(boolean darkModeEnabled) {
        super.onThemeChanged(darkModeEnabled);
        rngHistoryDialog.resetDialog(this, darkModeEnabled);
        diceHistoryDialog.resetDialog(this, darkModeEnabled);
        lottoHistoryDialog.resetDialog(this, darkModeEnabled);
        coinsHistoryDialog.resetDialog(this, darkModeEnabled);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        if (preferencesManager.isShakeEnabled()) {
            shakeDetector.stop();
        }
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (preferencesManager.isShakeEnabled()) {
            shakeDetector.start((SensorManager) getSystemService(SENSOR_SERVICE));
        }
    }

    public void showSnackbar(String message) {
        UIUtils.showSnackbar(parent, message, this);
    }

    @OnPageChange(R.id.view_pager)
    public void onPageChange() {
        UIUtils.hideKeyboard(this);
    }

    public void playSound(@RNGType int rngType) {
        if (!preferencesManager.shouldPlaySounds()) {
            return;
        }
        soundPlayer.playSound(rngType);
        if (preferencesManager.shouldAskForMute()) {
            askToMute();
        }
    }

    public void askToMute() {
        Snackbar snackbar = Snackbar.make(
                parent,
                R.string.dislike_sound,
                Snackbar.LENGTH_LONG);
        View rootView = snackbar.getView();
        snackbar.getView().setBackgroundColor(blue);
        TextView tv = rootView.findViewById(android.support.design.R.id.snackbar_text);
        tv.setTextColor(Color.WHITE);
        snackbar.setAction(R.string.turn_off, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                preferencesManager.setPlaySounds(false);
                showSnackbar(getString(R.string.sounds_muted));
            }
        });
        snackbar.setActionTextColor(Color.WHITE);
        snackbar.show();
    }

    @Override
    public void hearShake() {
        if (preferencesManager.shouldPlaySounds()) {
            if (!disableGeneration) {
                disableGeneration = true;
                shakeManager.onShakeDetected(homePager.getCurrentItem());
            }
        } else {
            shakeManager.onShakeDetected(homePager.getCurrentItem());
        }
    }

    private void showProperHistoryDialog() {
        switch (homePager.getCurrentItem()) {
            case 0:
                rngHistoryDialog.show();
                break;
            case 1:
                diceHistoryDialog.show();
                break;
            case 2:
                lottoHistoryDialog.show();
                break;
            case 3:
                coinsHistoryDialog.show();
                break;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (preferencesManager.isShakeEnabled()) {
            shakeDetector.stop();
        }
        soundPlayer.silence();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        UIUtils.loadMenuIcon(menu, R.id.history, FontAwesomeIcons.fa_history, this);
        UIUtils.loadMenuIcon(menu, R.id.settings, IoniconsIcons.ion_android_settings, this);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.history:
                showProperHistoryDialog();
                return true;
            case R.id.settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
