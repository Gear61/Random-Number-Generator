package com.randomappsinc.randomnumbergeneratorplus.Activities;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;
import com.randomappsinc.randomnumbergeneratorplus.Models.RNGSettingsViewHolder;
import com.randomappsinc.randomnumbergeneratorplus.Persistence.Database.DatabaseManager;
import com.randomappsinc.randomnumbergeneratorplus.Persistence.Database.RNGConfiguration;
import com.randomappsinc.randomnumbergeneratorplus.Persistence.PreferencesManager;
import com.randomappsinc.randomnumbergeneratorplus.R;
import com.randomappsinc.randomnumbergeneratorplus.Utils.ConversionUtils;
import com.randomappsinc.randomnumbergeneratorplus.Utils.FormUtils;
import com.randomappsinc.randomnumbergeneratorplus.Utils.RandUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.BindColor;
import butterknife.BindString;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;

public class MainActivity extends StandardActivity {
    @Bind(R.id.parent) View parent;
    @Bind(R.id.minimum) EditText minimumInput;
    @Bind(R.id.maximum) EditText maximumInput;
    @Bind(R.id.quantity) EditText quantityInput;
    @BindString(R.string.config_name) String configHint;
    @BindColor(R.color.app_blue) int blue;

    private ArrayList<Integer> excludedNumbers;
    private String currentConfiguration;
    private MaterialDialog settingsDialog;
    private RNGSettingsViewHolder viewHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homepage);
        ButterKnife.bind(this);

        excludedNumbers = new ArrayList<>();

        if (PreferencesManager.get().shouldAskForRating()) {
            new MaterialDialog.Builder(this)
                    .content(R.string.please_rate)
                    .negativeText(R.string.decline_rating)
                    .positiveText(R.string.will_rate)
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            Uri uri =  Uri.parse("market://details?id=" + getApplicationContext().getPackageName());
                            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                            if (!(getPackageManager().queryIntentActivities(intent, 0).size() > 0)) {
                                FormUtils.showSnackbar(parent, getString(R.string.play_store_error));
                                return;
                            }
                            startActivity(intent);
                        }
                    })
                    .show();
        }

        settingsDialog = new MaterialDialog.Builder(this)
                .title(R.string.rng_settings)
                .customView(R.layout.rng_settings, true)
                .positiveText(R.string.apply)
                .cancelable(false)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        FormUtils.showSnackbar(parent, getString(R.string.settings_applied));
                    }
                })
                .build();
        viewHolder = new RNGSettingsViewHolder(settingsDialog.getCustomView());

        String defaultConfig = PreferencesManager.get().getDefaultConfig();
        if (!defaultConfig.isEmpty()) {
            loadConfig(defaultConfig, false);
        }
    }

    @OnClick(R.id.edit_excluded)
    public void editExcluded() {
        MaterialDialog excludedDialog = new MaterialDialog.Builder(this)
                .title(R.string.excluded_numbers)
                .content(RandUtils.getExcludedList(excludedNumbers))
                .positiveText(android.R.string.yes)
                .negativeText(R.string.edit)
                .onAny(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        if (which == DialogAction.NEGATIVE) {
                            editExcludedNumbers();
                        } else if (which == DialogAction.NEUTRAL) {
                            excludedNumbers.clear();
                            FormUtils.showSnackbar(parent, getString(R.string.excluded_clear));
                        }
                    }
                })
                .build();
        if (!excludedNumbers.isEmpty()) {
            excludedDialog.setActionButton(DialogAction.NEUTRAL, R.string.clear);
        }
        excludedDialog.show();
    }

    private void editExcludedNumbers() {
        Intent intent = new Intent(this, EditExcludedActivity.class);
        intent.putExtra(EditExcludedActivity.MINIMUM_KEY, Integer.parseInt(minimumInput.getText().toString()));
        intent.putExtra(EditExcludedActivity.MAXIMUM_KEY, Integer.parseInt(maximumInput.getText().toString()));
        intent.putIntegerArrayListExtra(EditExcludedActivity.EXCLUDED_NUMBERS_KEY, excludedNumbers);
        startActivityForResult(intent, 1);
    }

    @OnTextChanged(value = R.id.minimum, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void minChanged() {
        excludedNumbers.clear();
    }

    @OnTextChanged(value = R.id.maximum, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void maxChanged() {
        excludedNumbers.clear();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            excludedNumbers = data.getIntegerArrayListExtra(EditExcludedActivity.EXCLUDED_NUMBERS_KEY);
            FormUtils.showSnackbar(parent, getString(R.string.excluded_updated));
        }
    }

    @OnClick(R.id.generate)
    public void generate() {
        if (verifyForm()) {
            int minimum = Integer.parseInt(minimumInput.getText().toString());
            int maximum = Integer.parseInt(maximumInput.getText().toString());
            int quantity = Integer.parseInt(quantityInput.getText().toString());
            List<Integer> generatedNums = RandUtils.getNumbers(minimum, maximum, quantity,
                    viewHolder.getNoDupes(), excludedNumbers);
            String resultsString = RandUtils.getResultsString(generatedNums);
            RandUtils.showResultsDialog(resultsString, this, parent);
        }
    }

    public boolean verifyForm() {
        FormUtils.hideKeyboard(this);
        String minimum = minimumInput.getText().toString();
        String maximum = maximumInput.getText().toString();
        String quantity = quantityInput.getText().toString();
        int numAvailable = Integer.parseInt(maximum) - Integer.parseInt(minimum) + 1;
        int quantityRestriction = viewHolder.getNoDupes() ? Integer.parseInt(quantity) : 1;
        if (minimum.isEmpty() || maximum.isEmpty() || quantity.isEmpty()) {
            FormUtils.showSnackbar(parent, getString(R.string.missing_input));
            return false;
        } else if (Integer.parseInt(maximum) < Integer.parseInt(minimum)) {
            FormUtils.showSnackbar(parent, getString(R.string.bigger_min));
            return false;
        } else if (Integer.parseInt(quantity) <= 0) {
            FormUtils.showSnackbar(parent, getString(R.string.non_zero_quantity));
            return false;
        } else if (numAvailable < quantityRestriction + excludedNumbers.size()) {
            FormUtils.showSnackbar(parent, getString(R.string.overlimited_range));
            return false;
        }
        return true;
    }

    public void showLoadDialog() {
        String[] rngConfigs = DatabaseManager.get().getAllConfigs();
        if (rngConfigs.length > 0) {
            new MaterialDialog.Builder(this)
                    .title(R.string.load_config)
                    .items(rngConfigs)
                    .itemsCallback(new MaterialDialog.ListCallback() {
                        @Override
                        public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                            loadConfig(text.toString(), true);
                        }
                    })
                    .show();
        } else {
            FormUtils.showSnackbar(parent, getString(R.string.no_configs));
        }
    }

    public void loadConfig(String configName, boolean verbose) {
        RNGConfiguration config = DatabaseManager.get().getConfig(configName);
        minimumInput.setText(String.valueOf(config.getMinimum()));
        maximumInput.setText(String.valueOf(config.getMaximum()));
        quantityInput.setText(String.valueOf(config.getQuantity()));
        viewHolder.loadConfig(config);
        excludedNumbers = ConversionUtils.getPlainExcludes(config.getExcludedNumbers());
        currentConfiguration = configName;
        if (verbose) {
            confirmConfigAction(getString(R.string.config_loaded), configName);
        }
    }

    public void showSaveDialog() {
        String currentConfigName = currentConfiguration != null ? currentConfiguration : "";
        new MaterialDialog.Builder(this)
                .title(R.string.save_config)
                .input(configHint, currentConfigName, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                        boolean submitEnabled = !(input.toString().trim().isEmpty());
                        dialog.getActionButton(DialogAction.POSITIVE).setEnabled(submitEnabled);
                    }
                })
                .alwaysCallInputCallback()
                .positiveText(R.string.save)
                .negativeText(android.R.string.no)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        String configName = dialog.getInputEditText().getText().toString();
                        if (DatabaseManager.get().doesConfigExist(configName)) {
                            showOverwriteConfirmDialog(configName);
                        } else {
                            saveConfiguration(configName);
                        }
                    }
                })
                .show();
    }

    public void showOverwriteConfirmDialog(final String configName) {
        String confirmOverwrite = "You already have a RNG configuration named \"" + configName + "\". " +
                "Would you like to overwrite it?";
        new MaterialDialog.Builder(this)
                .title(R.string.confirm_overwrite)
                .content(confirmOverwrite)
                .positiveText(android.R.string.yes)
                .negativeText(android.R.string.no)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        saveConfiguration(configName);
                    }
                })
                .show();
    }

    public void saveConfiguration(String configName) {
        RNGConfiguration configuration = new RNGConfiguration();
        configuration.setConfigName(configName);
        configuration.setMinimum(Integer.parseInt(minimumInput.getText().toString()));
        configuration.setMaximum(Integer.parseInt(maximumInput.getText().toString()));
        configuration.setQuantity(Integer.parseInt(quantityInput.getText().toString()));
        viewHolder.updateConfig(configuration);
        configuration.setExcludedNumbers(ConversionUtils.getRealmExcludes(excludedNumbers));
        DatabaseManager.get().addOrUpdateConfig(configuration);
        currentConfiguration = configName;
        confirmConfigAction(getString(R.string.config_saved), configName);
    }

    public void confirmConfigAction(String messageBase, final String configName) {
        if (!PreferencesManager.get().getDefaultConfig().equals(configName)) {
            Snackbar snackbar = Snackbar.make(parent, messageBase + getString(R.string.set_preload),
                    Snackbar.LENGTH_INDEFINITE);
            View rootView = snackbar.getView();
            snackbar.getView().setBackgroundColor(blue);
            TextView textview = (TextView) rootView.findViewById(android.support.design.R.id.snackbar_text);
            textview.setTextColor(Color.WHITE);
            snackbar.setAction(android.R.string.yes, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PreferencesManager.get().setDefaultConfig(configName);
                    FormUtils.showSnackbar(parent, getString(R.string.preload_confirm));
                }
            });
            snackbar.setActionTextColor(Color.WHITE);
            snackbar.show();
        } else {
            FormUtils.showSnackbar(parent, messageBase);
        }
    }

    private void showConfigOptions() {
        new MaterialDialog.Builder(this)
                .items(R.array.config_options)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        switch (which) {
                            case 0:
                                showLoadDialog();
                                break;
                            case 1:
                                if (verifyForm()) {
                                    showSaveDialog();
                                }
                                break;
                        }
                    }
                })
                .show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menu.findItem(R.id.config_options).setIcon(
                new IconDrawable(this, FontAwesomeIcons.fa_list_ol)
                        .colorRes(R.color.white)
                        .actionBarSize());
        menu.findItem(R.id.rng_settings).setIcon(
                new IconDrawable(this, FontAwesomeIcons.fa_gear)
                        .colorRes(R.color.white)
                        .actionBarSize());
        menu.findItem(R.id.additional_settings).setIcon(
                new IconDrawable(this, FontAwesomeIcons.fa_gears)
                        .colorRes(R.color.white)
                        .actionBarSize());
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.config_options:
                showConfigOptions();
                return true;
            case R.id.rng_settings:
                settingsDialog.show();
                return true;
            case R.id.additional_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
