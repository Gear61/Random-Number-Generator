package com.randomappsinc.randomnumbergeneratorplus.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.randomappsinc.randomnumbergeneratorplus.Adapters.SettingsAdapter;
import com.randomappsinc.randomnumbergeneratorplus.R;
import com.randomappsinc.randomnumbergeneratorplus.Utils.UIUtils;

import butterknife.Bind;
import butterknife.BindString;
import butterknife.ButterKnife;

/**
 * Created by alexanderchiou on 12/30/15.
 */
public class SettingsActivity extends StandardActivity {
    public static final String SUPPORT_EMAIL = "chessnone@gmail.com";
    public static final String OTHER_APPS_URL = "https://play.google.com/store/apps/dev?id=9093438553713389916";
    public static final String REPO_URL = "https://github.com/Gear61/Random-Number-Generator";

    @Bind(R.id.parent) View parent;
    @Bind(R.id.settings_options) ListView settingsOptions;
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

    public void handleSettingsClick(int position) {
        Intent intent = null;
        switch (position) {
            case 1:
                intent = new Intent(this, EditConfigurationsActivity.class);
                break;
            case 2:
                String uriText = "mailto:" + SUPPORT_EMAIL + "?subject=" + Uri.encode(feedbackSubject);
                Uri mailUri = Uri.parse(uriText);
                Intent sendIntent = new Intent(Intent.ACTION_SENDTO, mailUri);
                startActivity(Intent.createChooser(sendIntent, sendEmail));
                return;
            case 3:
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse(OTHER_APPS_URL));
                break;
            case 4:
                Uri uri =  Uri.parse("market://details?id=" + getApplicationContext().getPackageName());
                intent = new Intent(Intent.ACTION_VIEW, uri);
                if (!(getPackageManager().queryIntentActivities(intent, 0).size() > 0)) {
                    UIUtils.showSnackbar(parent, getString(R.string.play_store_error));
                    return;
                }
                break;
            case 5:
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse(REPO_URL));
                break;
        }
        startActivity(intent);
    }
}
