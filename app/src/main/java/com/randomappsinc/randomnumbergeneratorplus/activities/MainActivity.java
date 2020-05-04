package com.randomappsinc.randomnumbergeneratorplus.activities;

import android.content.Intent;
import android.graphics.Color;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;
import com.joanzapata.iconify.fonts.IoniconsIcons;
import com.randomappsinc.randomnumbergeneratorplus.R;
import com.randomappsinc.randomnumbergeneratorplus.adapters.HomepageTabsAdapter;
import com.randomappsinc.randomnumbergeneratorplus.audio.SoundPlayer;
import com.randomappsinc.randomnumbergeneratorplus.constants.RNGType;
import com.randomappsinc.randomnumbergeneratorplus.dialogs.HistoryDialog;
import com.randomappsinc.randomnumbergeneratorplus.persistence.HistoryDataManager;
import com.randomappsinc.randomnumbergeneratorplus.persistence.PreferencesManager;
import com.randomappsinc.randomnumbergeneratorplus.utils.DialogUtils;
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

        DialogUtils.showHomepageDialog(this, preferencesManager, isDarkModeEnabled(), themeManager);

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
    public void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
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
        SpannableStringBuilder spannableString = new SpannableStringBuilder(
                getString(R.string.dislike_sound));
        spannableString.setSpan(
                new ForegroundColorSpan(Color.WHITE),
                0,
                spannableString.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        Snackbar snackbar = Snackbar.make(
                parent,
                spannableString,
                Snackbar.LENGTH_LONG);
        snackbar.getView().setBackgroundColor(blue);
        snackbar.setAction(R.string.turn_off, v -> {
            preferencesManager.setPlaySounds(false);
            showSnackbar(getString(R.string.sounds_muted));
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
