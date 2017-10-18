package com.randomappsinc.randomnumbergeneratorplus.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.randomappsinc.randomnumbergeneratorplus.R;
import com.randomappsinc.randomnumbergeneratorplus.adapters.SettingsAdapter;
import com.randomappsinc.randomnumbergeneratorplus.persistence.PreferencesManager;
import com.randomappsinc.randomnumbergeneratorplus.utils.UIUtils;
import com.rey.material.widget.Switch;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnItemClick;

public class SettingsActivity extends StandardActivity {

    public static final String SUPPORT_EMAIL = "chessnone@gmail.com";
    public static final String OTHER_APPS_URL = "https://play.google.com/store/apps/dev?id=9093438553713389916";
    public static final String REPO_URL = "https://github.com/Gear61/Random-Number-Generator";

    @BindView(R.id.parent) View parent;
    @BindView(R.id.settings) ListView settingsOptions;
    @BindString(R.string.feedback_subject) String feedbackSubject;
    @BindString(R.string.send_email) String sendEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        settingsOptions.setAdapter(new SettingsAdapter(this));
    }

    @OnItemClick(R.id.settings)
    public void handleSettingsClick(int position) {
        Intent intent = null;
        switch (position) {
            case 0:
                View firstCell = settingsOptions.getChildAt(0);
                Switch shakeToggle = (Switch) firstCell.findViewById(R.id.toggle);
                boolean shakeStatus = shakeToggle.isChecked();
                shakeToggle.setChecked(!shakeStatus);
                PreferencesManager.get().setShakeEnabled(!shakeStatus);
                return;
            case 1:
                View secondCell = settingsOptions.getChildAt(1);
                Switch soundToggle = (Switch) secondCell.findViewById(R.id.toggle);
                boolean soundsEnabled = soundToggle.isChecked();
                soundToggle.setChecked(!soundsEnabled);
                PreferencesManager.get().setPlaySounds(!soundsEnabled);
                return;
            case 2:
                intent = new Intent(this, EditConfigurationsActivity.class);
                break;
            case 3:
                String uriText = "mailto:" + SUPPORT_EMAIL + "?subject=" + Uri.encode(feedbackSubject);
                Uri mailUri = Uri.parse(uriText);
                Intent sendIntent = new Intent(Intent.ACTION_SENDTO, mailUri);
                startActivity(Intent.createChooser(sendIntent, sendEmail));
                return;
            case 4:
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse(OTHER_APPS_URL));
                break;
            case 5:
                Uri uri =  Uri.parse("market://details?id=" + getApplicationContext().getPackageName());
                intent = new Intent(Intent.ACTION_VIEW, uri);
                if (!(getPackageManager().queryIntentActivities(intent, 0).size() > 0)) {
                    UIUtils.showSnackbar(parent, getString(R.string.play_store_error));
                    return;
                }
                break;
            case 6:
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse(REPO_URL));
                break;
        }
        startActivity(intent);
    }
}
