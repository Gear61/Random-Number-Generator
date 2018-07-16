package com.randomappsinc.randomnumbergeneratorplus.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.randomappsinc.randomnumbergeneratorplus.R;
import com.randomappsinc.randomnumbergeneratorplus.activities.EditExcludedActivity;
import com.randomappsinc.randomnumbergeneratorplus.activities.MainActivity;
import com.randomappsinc.randomnumbergeneratorplus.models.RNGSettings;
import com.randomappsinc.randomnumbergeneratorplus.models.RNGSettingsViewHolder;
import com.randomappsinc.randomnumbergeneratorplus.persistence.PreferencesManager;
import com.randomappsinc.randomnumbergeneratorplus.utils.RandUtils;
import com.randomappsinc.randomnumbergeneratorplus.utils.TextUtils;
import com.randomappsinc.randomnumbergeneratorplus.utils.UIUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import butterknife.Unbinder;

public class RNGFragment extends Fragment {

    @BindView(R.id.focal_point) View focalPoint;
    @BindView(R.id.minimum) EditText minimumInput;
    @BindView(R.id.maximum) EditText maximumInput;
    @BindView(R.id.quantity) EditText quantityInput;
    @BindView(R.id.excluded_numbers) TextView excludedNumsDisplay;
    @BindView(R.id.results_container) View resultsContainer;
    @BindView(R.id.results) TextView results;

    @BindColor(R.color.app_blue) int blue;

    private final TextUtils.SnackbarDisplay snackbarDisplay = new TextUtils.SnackbarDisplay() {
        @Override
        public void showSnackbar(String message) {
            ((MainActivity) getActivity()).showSnackbar(message);
        }
    };

    private PreferencesManager preferencesManager = PreferencesManager.get();
    private RNGSettings rngSettings;
    private MaterialDialog settingsDialog;
    private RNGSettingsViewHolder moreSettingsViewHolder;
    private Unbinder unbinder;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.rng_page, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
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
        rngSettings = preferencesManager.getRNGSettings();

        // Setting the value of min/max clears the excluded numbers, so we have to save them
        ArrayList<Integer> excludedCopy = new ArrayList<>(rngSettings.getExcludedNumbers());
        minimumInput.setText(String.valueOf(rngSettings.getMinimum()));
        maximumInput.setText(String.valueOf(rngSettings.getMaximum()));
        rngSettings.setExcludedNumbers(excludedCopy);

        quantityInput.setText(String.valueOf(rngSettings.getNumNumbers()));
        moreSettingsViewHolder = new RNGSettingsViewHolder(settingsDialog.getCustomView(), getActivity(), rngSettings);
        loadExcludedNumbers();

        focalPoint.requestFocus();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        saveRNGSettings();
    }

    private void saveRNGSettings() {
        try {
            rngSettings.setMinimum(Integer.valueOf(minimumInput.getText().toString()));
        } catch (NumberFormatException ignored) {}
        try {
            rngSettings.setMaximum(Integer.valueOf(maximumInput.getText().toString()));
        } catch (NumberFormatException ignored) {}
        try {
            rngSettings.setNumNumbers(Integer.valueOf(quantityInput.getText().toString()));
        } catch (NumberFormatException ignored) {}
        rngSettings.setSortType(moreSettingsViewHolder.getSortIndex());
        rngSettings.setNoDupes(moreSettingsViewHolder.getNoDupes());
        rngSettings.setShowSum(moreSettingsViewHolder.getShowSum());
        rngSettings.setHideExcluded(moreSettingsViewHolder.getHideExcludes());
        preferencesManager.saveRNGSettings(rngSettings);
    }

    private void loadExcludedNumbers() {
        ArrayList<Integer> excludedNumbers = rngSettings.getExcludedNumbers();
        if (excludedNumbers.isEmpty()) {
            excludedNumsDisplay.setText(R.string.none);
        } else {
            if (moreSettingsViewHolder.getHideExcludes()) {
                excludedNumsDisplay.setText(R.string.ellipsis);
            } else {
                excludedNumsDisplay.setText(RandUtils.getExcludedList(excludedNumbers));
            }
        }
    }

    @OnClick({R.id.excluded_numbers_container, R.id.excluded_numbers})
    public void editExcluded() {
        final ArrayList<Integer> excludedNumbers = rngSettings.getExcludedNumbers();
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
                            snackbarDisplay.showSnackbar(getString(R.string.excluded_clear));
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
            intent.putIntegerArrayListExtra(
                    EditExcludedActivity.EXCLUDED_NUMBERS_KEY,
                    rngSettings.getExcludedNumbers());
            startActivityForResult(intent, 1);
            getActivity().overridePendingTransition(R.anim.slide_left_out, R.anim.slide_left_in);
        } catch (NumberFormatException exception) {
            snackbarDisplay.showSnackbar(getString(R.string.not_a_number));
        }
    }

    @OnClick(R.id.rng_settings)
    public void showRNGSettings() {
        settingsDialog.show();
    }

    @OnTextChanged(value = R.id.minimum, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void minChanged() {
        rngSettings.getExcludedNumbers().clear();
        loadExcludedNumbers();
    }

    @OnTextChanged(value = R.id.maximum, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void maxChanged() {
        rngSettings.getExcludedNumbers().clear();
        loadExcludedNumbers();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            rngSettings.setExcludedNumbers(data.getIntegerArrayListExtra(
                    EditExcludedActivity.EXCLUDED_NUMBERS_KEY));
            loadExcludedNumbers();
            snackbarDisplay.showSnackbar(getString(R.string.excluded_updated));
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
                    moreSettingsViewHolder.getNoDupes(), rngSettings.getExcludedNumbers());
            switch (moreSettingsViewHolder.getSortIndex()) {
                case 1:
                    Collections.sort(generatedNums);
                    break;
                case 2:
                    Collections.sort(generatedNums);
                    Collections.reverse(generatedNums);
                    break;
            }
            resultsContainer.setVisibility(View.VISIBLE);
            String resultsString = RandUtils.getResultsString(generatedNums, moreSettingsViewHolder.getShowSum());
            UIUtils.animateResults(results, Html.fromHtml(resultsString));
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
            int quantityRestriction = moreSettingsViewHolder.getNoDupes() ? Integer.parseInt(quantity) : 1;
            if (minimum.isEmpty() || maximum.isEmpty() || quantity.isEmpty()) {
                snackbarDisplay.showSnackbar(getString(R.string.missing_input));
                return false;
            } else if (Integer.parseInt(maximum) < Integer.parseInt(minimum)) {
                snackbarDisplay.showSnackbar(getString(R.string.bigger_min));
                return false;
            } else if (Integer.parseInt(quantity) <= 0) {
                snackbarDisplay.showSnackbar(getString(R.string.non_zero_quantity));
                return false;
            } else if (numAvailable < quantityRestriction + rngSettings.getExcludedNumbers().size()) {
                snackbarDisplay.showSnackbar(getString(R.string.overlimited_range));
                return false;
            }
        } catch (NumberFormatException exception) {
            snackbarDisplay.showSnackbar(getString(R.string.not_a_number));
            return false;
        }
        return true;
    }

    @OnClick(R.id.copy_results)
    public void copyNumbers() {
        String numbersText = results.getText().toString();
        TextUtils.copyTextToClipboard(numbersText, snackbarDisplay);
    }

    @Override
    public void onDestroyView() {
        saveRNGSettings();
        super.onDestroyView();
        unbinder.unbind();
    }
}
