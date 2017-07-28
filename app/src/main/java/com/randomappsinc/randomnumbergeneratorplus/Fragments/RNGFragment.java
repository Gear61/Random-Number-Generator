package com.randomappsinc.randomnumbergeneratorplus.Fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;
import com.joanzapata.iconify.fonts.IoniconsIcons;
import com.randomappsinc.randomnumbergeneratorplus.Activities.EditExcludedActivity;
import com.randomappsinc.randomnumbergeneratorplus.Activities.MainActivity;
import com.randomappsinc.randomnumbergeneratorplus.Activities.SettingsActivity;
import com.randomappsinc.randomnumbergeneratorplus.Models.RNGSettingsViewHolder;
import com.randomappsinc.randomnumbergeneratorplus.Persistence.Database.DatabaseManager;
import com.randomappsinc.randomnumbergeneratorplus.Persistence.Database.RNGConfiguration;
import com.randomappsinc.randomnumbergeneratorplus.Persistence.PreferencesManager;
import com.randomappsinc.randomnumbergeneratorplus.R;
import com.randomappsinc.randomnumbergeneratorplus.Utils.ConversionUtils;
import com.randomappsinc.randomnumbergeneratorplus.Utils.RandUtils;
import com.randomappsinc.randomnumbergeneratorplus.Utils.UIUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import butterknife.BindColor;
import butterknife.BindString;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;

/**
 * Created by alexanderchiou on 1/1/17.
 */

public class RNGFragment extends Fragment {
    public static final String TAG = RNGFragment.class.getSimpleName();

    @Bind(R.id.focal_point) View focalPoint;
    @Bind(R.id.minimum) EditText minimumInput;
    @Bind(R.id.maximum) EditText maximumInput;
    @Bind(R.id.quantity) EditText quantityInput;
    @Bind(R.id.excluded_numbers) TextView excludedNumsDisplay;
    @Bind(R.id.results_container) View resultsContainer;
    @Bind(R.id.results) TextView results;

    @BindString(R.string.config_name) String configHint;
    @BindColor(R.color.app_blue) int blue;

    private ArrayList<Integer> excludedNumbers;
    private String currentConfiguration;
    private MaterialDialog settingsDialog;
    private RNGSettingsViewHolder viewHolder;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.rng_page, container, false);
        ButterKnife.bind(this, rootView);

        excludedNumbers = new ArrayList<>();
        settingsDialog = new MaterialDialog.Builder(getActivity())
                .title(R.string.rng_settings)
                .customView(R.layout.rng_settings, true)
                .positiveText(android.R.string.yes)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        loadExcludedNumbers();
                    }
                })
                .build();
        viewHolder = new RNGSettingsViewHolder(settingsDialog.getCustomView(), getActivity());

        String defaultConfig = PreferencesManager.get().getDefaultConfig();
        if (!defaultConfig.isEmpty()) {
            loadConfig(defaultConfig, false);
        }

        loadExcludedNumbers();

        return rootView;
    }

    private void loadExcludedNumbers() {
        if (excludedNumbers.isEmpty()) {
            excludedNumsDisplay.setText(R.string.none);
        } else {
            if (viewHolder.getHideExcludes()) {
                excludedNumsDisplay.setText(R.string.ellipsis);
            } else {
                excludedNumsDisplay.setText(RandUtils.getExcludedList(excludedNumbers));
            }
        }
    }

    private void showSnackbar(String message) {
        ((MainActivity) getActivity()).showSnackbar(message);
    }

    @OnClick({R.id.excluded_numbers_container, R.id.excluded_numbers})
    public void editExcluded() {
        MaterialDialog excludedDialog = new MaterialDialog.Builder(getActivity())
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
                            loadExcludedNumbers();
                            showSnackbar(getString(R.string.excluded_clear));
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
        try {
            Intent intent = new Intent(getActivity(), EditExcludedActivity.class);
            intent.putExtra(EditExcludedActivity.MINIMUM_KEY, Integer.parseInt(minimumInput.getText().toString()));
            intent.putExtra(EditExcludedActivity.MAXIMUM_KEY, Integer.parseInt(maximumInput.getText().toString()));
            intent.putIntegerArrayListExtra(EditExcludedActivity.EXCLUDED_NUMBERS_KEY, excludedNumbers);
            startActivityForResult(intent, 1);
            getActivity().overridePendingTransition(R.anim.slide_left_out, R.anim.slide_left_in);
        } catch (NumberFormatException exception) {
            showSnackbar(getString(R.string.not_a_number));
        }
    }

    @OnClick(R.id.rng_settings)
    public void showRNGSettings() {
        settingsDialog.show();
    }

    @OnTextChanged(value = R.id.minimum, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void minChanged() {
        excludedNumbers.clear();
        loadExcludedNumbers();
    }

    @OnTextChanged(value = R.id.maximum, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void maxChanged() {
        excludedNumbers.clear();
        loadExcludedNumbers();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            excludedNumbers = data.getIntegerArrayListExtra(EditExcludedActivity.EXCLUDED_NUMBERS_KEY);
            loadExcludedNumbers();
            showSnackbar(getString(R.string.excluded_updated));
        }
    }

    @OnClick(R.id.generate)
    public void generate() {
        if (verifyForm()) {
            if (PreferencesManager.get().shouldPlaySounds()) {
                ((MainActivity) getActivity()).playSound("rng_noise.wav");
            }
            int minimum = Integer.parseInt(minimumInput.getText().toString());
            int maximum = Integer.parseInt(maximumInput.getText().toString());
            int quantity = Integer.parseInt(quantityInput.getText().toString());
            List<Integer> generatedNums = RandUtils.getNumbers(minimum, maximum, quantity,
                    viewHolder.getNoDupes(), excludedNumbers);
            switch (viewHolder.getSortIndex()) {
                case 1:
                    Collections.sort(generatedNums);
                    break;
                case 2:
                    Collections.sort(generatedNums);
                    Collections.reverse(generatedNums);
                    break;
            }
            String resultsString = RandUtils.getResultsString(generatedNums, viewHolder.getShowSum());
            resultsContainer.setVisibility(View.VISIBLE);
            results.setText(Html.fromHtml(resultsString));
        }
    }

    public boolean verifyForm() {
        UIUtils.hideKeyboard(getActivity());
        focalPoint.requestFocus();

        String minimum = minimumInput.getText().toString();
        String maximum = maximumInput.getText().toString();
        String quantity = quantityInput.getText().toString();
        try {
            int numAvailable = Integer.parseInt(maximum) - Integer.parseInt(minimum) + 1;
            int quantityRestriction = viewHolder.getNoDupes() ? Integer.parseInt(quantity) : 1;
            if (minimum.isEmpty() || maximum.isEmpty() || quantity.isEmpty()) {
                showSnackbar(getString(R.string.missing_input));
                return false;
            } else if (Integer.parseInt(maximum) < Integer.parseInt(minimum)) {
                showSnackbar(getString(R.string.bigger_min));
                return false;
            } else if (Integer.parseInt(quantity) <= 0) {
                showSnackbar(getString(R.string.non_zero_quantity));
                return false;
            } else if (numAvailable < quantityRestriction + excludedNumbers.size()) {
                showSnackbar(getString(R.string.overlimited_range));
                return false;
            }
        } catch (NumberFormatException exception) {
            showSnackbar(getString(R.string.not_a_number));
            return false;
        }
        return true;
    }

    public void showLoadDialog() {
        String[] rngConfigs = DatabaseManager.get().getAllConfigs();
        if (rngConfigs.length > 0) {
            new MaterialDialog.Builder(getActivity())
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
            showSnackbar(getString(R.string.no_configs));
        }
    }

    public void loadConfig(String configName, boolean verbose) {
        RNGConfiguration config = DatabaseManager.get().getConfig(configName);
        if (config != null) {
            minimumInput.setText(String.valueOf(config.getMinimum()));
            maximumInput.setText(String.valueOf(config.getMaximum()));
            quantityInput.setText(String.valueOf(config.getQuantity()));
            viewHolder.loadConfig(config);
            excludedNumbers = ConversionUtils.getPlainExcludes(config.getExcludedNumbers());
            loadExcludedNumbers();
            currentConfiguration = configName;
            if (verbose) {
                confirmConfigAction(getString(R.string.config_loaded), configName);
            }
        } else {
            PreferencesManager.get().setDefaultConfig("");
        }
    }

    public void showSaveDialog() {
        String currentConfigName = currentConfiguration != null ? currentConfiguration : "";
        new MaterialDialog.Builder(getActivity())
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
        new MaterialDialog.Builder(getActivity())
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
            Snackbar snackbar = Snackbar.make(((MainActivity) getActivity()).getParentView(),
                    messageBase + getString(R.string.set_preload), Snackbar.LENGTH_INDEFINITE);
            View rootView = snackbar.getView();
            snackbar.getView().setBackgroundColor(blue);
            TextView textview = (TextView) rootView.findViewById(android.support.design.R.id.snackbar_text);
            textview.setTextColor(Color.WHITE);
            snackbar.setAction(android.R.string.yes, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PreferencesManager.get().setDefaultConfig(configName);
                    showSnackbar(getString(R.string.preload_confirm));
                }
            });
            snackbar.setActionTextColor(Color.WHITE);
            snackbar.show();
        } else {
            showSnackbar(messageBase);
        }
    }

    @OnClick(R.id.copy_results)
    public void copyNumbers() {
        String numbersText = results.getText().toString();
        ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Activity.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(getString(R.string.generated_numbers), numbersText);
        clipboard.setPrimaryClip(clip);
        showSnackbar(getString(R.string.copied_to_clipboard));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.rng_menu, menu);
        UIUtils.loadMenuIcon(menu, R.id.save_config, FontAwesomeIcons.fa_save);
        UIUtils.loadMenuIcon(menu, R.id.load_config, FontAwesomeIcons.fa_upload);
        UIUtils.loadMenuIcon(menu, R.id.settings, IoniconsIcons.ion_android_settings);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_config:
                if (verifyForm()) {
                    showSaveDialog();
                }
                return true;
            case R.id.load_config:
                showLoadDialog();
                return true;
            case R.id.settings:
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                getActivity().overridePendingTransition(R.anim.slide_left_out, R.anim.slide_left_in);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
