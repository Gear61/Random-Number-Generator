package com.randomappsinc.randomnumbergeneratorplus.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.randomappsinc.randomnumbergeneratorplus.R;
import com.randomappsinc.randomnumbergeneratorplus.activities.EditExcludedActivity;
import com.randomappsinc.randomnumbergeneratorplus.activities.MainActivity;
import com.randomappsinc.randomnumbergeneratorplus.constants.RNGType;
import com.randomappsinc.randomnumbergeneratorplus.models.RNGSettings;
import com.randomappsinc.randomnumbergeneratorplus.models.RNGSettingsViewHolder;
import com.randomappsinc.randomnumbergeneratorplus.persistence.HistoryDataManager;
import com.randomappsinc.randomnumbergeneratorplus.persistence.PreferencesManager;
import com.randomappsinc.randomnumbergeneratorplus.theme.ThemeManager;
import com.randomappsinc.randomnumbergeneratorplus.utils.RandUtils;
import com.randomappsinc.randomnumbergeneratorplus.utils.ShakeManager;
import com.randomappsinc.randomnumbergeneratorplus.utils.TextUtils;
import com.randomappsinc.randomnumbergeneratorplus.utils.UIUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindColor;
import butterknife.BindInt;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import butterknife.Unbinder;

public class RNGFragment extends Fragment implements ThemeManager.Listener {

    @BindView(R.id.focal_point) View focalPoint;
    @BindView(R.id.minimum) EditText minimumInput;
    @BindView(R.id.maximum) EditText maximumInput;
    @BindView(R.id.quantity) EditText quantityInput;
    @BindView(R.id.excluded_numbers) TextView excludedNumsDisplay;
    @BindView(R.id.results_container) View resultsContainer;
    @BindView(R.id.results) TextView results;

    @BindString(R.string.numbers_prefix) String numbersPrefix;
    @BindString(R.string.sum_prefix) String sumPrefix;
    @BindString(R.string.no_excluded_numbers) String noExcludedNumbers;
    @BindColor(R.color.app_blue) int blue;
    @BindInt(R.integer.shorter_anim_length) int resultsAnimationLength;

    private final TextUtils.SnackbarDisplay snackbarDisplay = message ->
            ((MainActivity) getActivity()).showSnackbar(message);

    private final ShakeManager.Listener shakeListener = currentRngPage -> {
        if (currentRngPage == RNGType.NUMBER) {
            generate();
        }
    };

    private PreferencesManager preferencesManager;
    private RNGSettings rngSettings;
    private MaterialDialog settingsDialog;
    private MaterialDialog excludedDialog;
    private RNGSettingsViewHolder moreSettingsViewHolder;
    private HistoryDataManager historyDataManager;
    private ShakeManager shakeManager = ShakeManager.get();
    private ThemeManager themeManager = ThemeManager.get();
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
        historyDataManager = HistoryDataManager.get(getActivity());
        preferencesManager = new PreferencesManager(getActivity());
        rngSettings = preferencesManager.getRNGSettings();

        // Setting the value of min/max clears the excluded numbers, so we have to save them
        ArrayList<Integer> excludedCopy = new ArrayList<>(rngSettings.getExcludedNumbers());
        minimumInput.setText(String.valueOf(rngSettings.getMinimum()));
        maximumInput.setText(String.valueOf(rngSettings.getMaximum()));
        rngSettings.setExcludedNumbers(excludedCopy);
        setSettingsDialog();
        setExcludedDialog();
        themeManager.registerListener(this);

        quantityInput.setText(String.valueOf(rngSettings.getNumNumbers()));
        loadExcludedNumbers();

        focalPoint.requestFocus();
        shakeManager.registerListener(shakeListener);
    }

    @Override
    public void onThemeChanged(boolean darkModeEnabled) {
        saveRNGSettings();
        setSettingsDialog();
        setExcludedDialog();
    }

    private void setSettingsDialog() {
        settingsDialog = new MaterialDialog.Builder(getActivity())
                .theme(themeManager.getDarkModeEnabled(getActivity()) ? Theme.DARK : Theme.LIGHT)
                .title(R.string.rng_settings)
                .customView(R.layout.rng_settings, true)
                .positiveText(android.R.string.yes)
                .onPositive((dialog, which) -> loadExcludedNumbers())
                .build();
        moreSettingsViewHolder = new RNGSettingsViewHolder(settingsDialog.getCustomView(), getActivity(), rngSettings);
    }

    private void setExcludedDialog() {
        ArrayList<Integer> excludedNumbers = rngSettings.getExcludedNumbers();
        excludedDialog = new MaterialDialog.Builder(getActivity())
                .theme(themeManager.getDarkModeEnabled(getActivity()) ? Theme.DARK : Theme.LIGHT)
                .title(R.string.excluded_numbers)
                .content(RandUtils.getExcludedList(excludedNumbers, noExcludedNumbers))
                .positiveText(android.R.string.yes)
                .negativeText(R.string.edit)
                .onAny((dialog, which) -> {
                    if (which == DialogAction.NEGATIVE) {
                        editExcludedNumbers();
                    } else if (which == DialogAction.NEUTRAL) {
                        rngSettings.getExcludedNumbers().clear();
                        loadExcludedNumbers();
                        snackbarDisplay.showSnackbar(getString(R.string.excluded_clear));
                    }
                })
                .build();
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
                excludedNumsDisplay.setText(RandUtils.getExcludedList(excludedNumbers, noExcludedNumbers));
            }
        }
    }

    @OnClick({R.id.excluded_numbers_container, R.id.excluded_numbers})
    void editExcluded() {
        ArrayList<Integer> excludedNumbers = rngSettings.getExcludedNumbers();
        excludedDialog.setContent(RandUtils.getExcludedList(excludedNumbers, noExcludedNumbers));
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
    void showRNGSettings() {
        settingsDialog.show();
    }

    @OnTextChanged(value = R.id.minimum, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    void minChanged() {
        rngSettings.getExcludedNumbers().clear();
        loadExcludedNumbers();
    }

    @OnTextChanged(value = R.id.maximum, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    void maxChanged() {
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
    void generate() {
        if (verifyForm()) {
            MainActivity mainActivity = (MainActivity) getActivity();
            if (mainActivity != null) {
                mainActivity.playSound(RNGType.NUMBER);
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
            String resultsString = RandUtils.getResultsString(
                    generatedNums,
                    moreSettingsViewHolder.getShowSum(),
                    numbersPrefix,
                    sumPrefix);
            historyDataManager.addHistoryRecord(RNGType.NUMBER, resultsString);
            UIUtils.animateResults(results, Html.fromHtml(resultsString), resultsAnimationLength);
        }
    }

    private boolean verifyForm() {
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
    void copyNumbers() {
        String numbersText = results.getText().toString();
        TextUtils.copyResultsToClipboard(numbersText, snackbarDisplay, getActivity());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        themeManager.unregisterListener(this);
        shakeManager.unregisterListener(shakeListener);
        saveRNGSettings();
        unbinder.unbind();
    }
}
