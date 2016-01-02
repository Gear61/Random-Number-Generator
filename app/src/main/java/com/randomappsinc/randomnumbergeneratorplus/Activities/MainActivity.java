package com.randomappsinc.randomnumbergeneratorplus.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

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
                    .title(R.string.welcome)
                    .content(R.string.ask_for_help)
                    .positiveText(android.R.string.yes)
                    .show();
        }
    }

    @OnClick(R.id.edit_excluded)
    public void editExcluded(View view) {
        Intent intent = new Intent(this, EditExcludedActivity.class);
        intent.putExtra(EditExcludedActivity.MINIMUM_KEY, Integer.parseInt(minimumInput.getText().toString()));
        intent.putExtra(EditExcludedActivity.MAXIMUM_KEY, Integer.parseInt(maximumInput.getText().toString()));
        intent.putIntegerArrayListExtra(EditExcludedActivity.EXCLUDED_NUMBERS_KEY, excludedNumbers);
        startActivityForResult(intent, 1);
    }

    @OnTextChanged(value = R.id.minimum, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void minChanged(Editable s) {
        excludedNumbers.clear();
    }

    @OnTextChanged(value = R.id.maximum, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void maxChanged(Editable s) {
        excludedNumbers.clear();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            excludedNumbers = data.getIntegerArrayListExtra(EditExcludedActivity.EXCLUDED_NUMBERS_KEY);
        }
    }

    @OnClick(R.id.generate)
    public void generate(View view) {
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
        }
        else if (Integer.parseInt(maximum) < Integer.parseInt(minimum)) {
            FormUtils.showSnackbar(parent, getString(R.string.bigger_min));
            return false;
        }
        else if (Integer.parseInt(quantity) <= 0) {
            FormUtils.showSnackbar(parent, getString(R.string.non_zero_quantity));
            return false;
        }
        else if (numAvailable < quantityRestriction + excludedNumbers.size()) {
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
                    .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                        @Override
                        public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                            return true;
                        }
                    })
                    .positiveText(R.string.load)
                    .negativeText(android.R.string.no)
                    .show();
        }
        else {
            FormUtils.showSnackbar(parent, getString(R.string.no_configs));
        }
    }

    public void showSaveDialog() {
        String currentConfigName = currentConfiguration != null ? currentConfiguration : "";
        new MaterialDialog.Builder(this)
                .title(R.string.save_config)
                .input(configHint, currentConfigName, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                        saveConfiguration(input.toString());
                    }
                })
                .positiveText(R.string.save)
                .negativeText(android.R.string.no)
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
        FormUtils.showSnackbar(parent, getString(R.string.config_saved));
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
