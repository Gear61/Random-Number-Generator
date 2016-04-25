package com.randomappsinc.randomnumbergeneratorplus.Activities;

import android.content.Context;
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
import com.randomappsinc.randomnumbergeneratorplus.Persistence.Database.DatabaseManager;
import com.randomappsinc.randomnumbergeneratorplus.Persistence.Database.RNGConfiguration;
import com.randomappsinc.randomnumbergeneratorplus.Persistence.PreferencesManager;
import com.randomappsinc.randomnumbergeneratorplus.R;
import com.randomappsinc.randomnumbergeneratorplus.Utils.ConversionUtils;
import com.randomappsinc.randomnumbergeneratorplus.Utils.FormUtils;
import com.randomappsinc.randomnumbergeneratorplus.Utils.RandUtils;
import com.rey.material.widget.CheckBox;

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
    @Bind(R.id.duplicates_toggle) CheckBox dupesToggle;
    @Bind(R.id.results) TextView results;
    @BindString(R.string.config_name) String configHint;
    @BindColor(R.color.app_blue) int blue;

    private ArrayList<Integer> excludedNumbers;
    private String currentConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homepage);
        ButterKnife.bind(this);

        excludedNumbers = new ArrayList<>();

        if (PreferencesManager.get().isFirstTimeUser()) {
            PreferencesManager.get().rememberWelcome();
            new MaterialDialog.Builder(this)
                    .title(R.string.instructions_title)
                    .content(R.string.instructions)
                    .positiveText(android.R.string.yes)
                    .show();
        }

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

        String defaultConfig = PreferencesManager.get().getDefaultConfig();
        if (!defaultConfig.isEmpty()) {
            loadConfig(defaultConfig, false);
        }
    }

    @OnClick(R.id.edit_excluded)
    public void editExcluded() {
        new MaterialDialog.Builder(this)
                .title(R.string.excluded_numbers)
                .content(RandUtils.getExcludedList(excludedNumbers))
                .neutralText(R.string.edit)
                .positiveText(android.R.string.yes)
                .onNeutral(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        editExcludedNumbers();
                    }
                })
                .show();
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
                    dupesToggle.isChecked(), excludedNumbers);
            String resultsString = RandUtils.getResultsString(generatedNums);
            results.setVisibility(View.VISIBLE);
            results.setText(resultsString);
        }
    }

    public boolean verifyForm() {
        FormUtils.hideKeyboard(this);
        String minimum = minimumInput.getText().toString();
        String maximum = maximumInput.getText().toString();
        String quantity = quantityInput.getText().toString();
        int numAvailable = Integer.parseInt(maximum) - Integer.parseInt(minimum) + 1;
        int quantityRestriction = dupesToggle.isChecked() ? Integer.parseInt(quantity) : 1;
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
        String[] configNames = DatabaseManager.get().getAllConfigs();
        if (configNames.length > 0) {
            new MaterialDialog.Builder(this)
                    .title(R.string.load_config)
                    .items(configNames)
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
        dupesToggle.setCheckedImmediately(config.isNoDupes());
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
        configuration.setNoDupes(dupesToggle.isChecked());
        configuration.setExcludedNumbers(ConversionUtils.getRealmExcludes(excludedNumbers));
        DatabaseManager.get().addOrUpdateConfig(configuration);
        currentConfiguration = configName;
        confirmConfigAction(getString(R.string.config_saved), configName);
    }

    public void confirmConfigAction(String messageBase, final String configName) {
        Snackbar snackbar = Snackbar.make(parent, messageBase + getString(R.string.set_preload), 7000);
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
    }

    @OnClick(R.id.results)
    public void showResultsOptions() {
        final Context context = this;
        new MaterialDialog.Builder(this)
                .items(R.array.results_options)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        switch (which) {
                            case 0:
                                RandUtils.copyNumsToClipboard(results.getText().toString(), parent);
                                break;
                            case 1:
                                RandUtils.showResultsDialog(results.getText().toString(), context);
                        }
                    }
                })
                .show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menu.findItem(R.id.load_config).setIcon(
                new IconDrawable(this, FontAwesomeIcons.fa_upload)
                        .colorRes(R.color.white)
                        .actionBarSize());
        menu.findItem(R.id.save_config).setIcon(
                new IconDrawable(this, FontAwesomeIcons.fa_save)
                        .colorRes(R.color.white)
                        .actionBarSize());
        menu.findItem(R.id.settings).setIcon(
                new IconDrawable(this, FontAwesomeIcons.fa_gear)
                        .colorRes(R.color.white)
                        .actionBarSize());
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.load_config:
                showLoadDialog();
                return true;
            case R.id.save_config:
                if (verifyForm()) {
                    showSaveDialog();
                }
                return true;
            case R.id.settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
