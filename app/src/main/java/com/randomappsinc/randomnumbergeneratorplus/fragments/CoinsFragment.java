package com.randomappsinc.randomnumbergeneratorplus.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.randomappsinc.randomnumbergeneratorplus.R;
import com.randomappsinc.randomnumbergeneratorplus.activities.MainActivity;
import com.randomappsinc.randomnumbergeneratorplus.constants.RNGType;
import com.randomappsinc.randomnumbergeneratorplus.persistence.HistoryDataManager;
import com.randomappsinc.randomnumbergeneratorplus.persistence.PreferencesManager;
import com.randomappsinc.randomnumbergeneratorplus.utils.RandUtils;
import com.randomappsinc.randomnumbergeneratorplus.utils.TextUtils;
import com.randomappsinc.randomnumbergeneratorplus.utils.UIUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class CoinsFragment extends Fragment {

    @BindView(R.id.focal_point) View focalPoint;
    @BindView(R.id.num_coins) EditText numCoinsInput;
    @BindView(R.id.results_container) View resultsContainer;
    @BindView(R.id.results) TextView results;

    private final TextUtils.SnackbarDisplay snackbarDisplay = new TextUtils.SnackbarDisplay() {
        @Override
        public void showSnackbar(String message) {
            ((MainActivity) getActivity()).showSnackbar(message);
        }
    };

    private HistoryDataManager historyDataManager = HistoryDataManager.get();
    private Unbinder unbinder;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.coins_page, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        numCoinsInput.setText(String.valueOf(PreferencesManager.get().getNumCoins()));
        return rootView;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        saveSettings();
    }

    @OnClick(R.id.flip)
    public void flip() {
        if (verifyForm()) {
            if (PreferencesManager.get().shouldPlaySounds()) {
                ((MainActivity) getActivity()).playSound("coin_flip.wav");
            }
            int numCoins = Integer.parseInt(numCoinsInput.getText().toString());
            List<Integer> flips = RandUtils.getNumbers(
                    0,
                    1,
                    numCoins,
                    false,
                    new ArrayList<Integer>());
            resultsContainer.setVisibility(View.VISIBLE);
            String flipText = RandUtils.getCoinResults(flips);
            historyDataManager.addHistoryRecord(RNGType.COINS, flipText);
            UIUtils.animateResults(results, Html.fromHtml(flipText));
        }
    }

    public boolean verifyForm() {
        UIUtils.hideKeyboard(getActivity());
        focalPoint.requestFocus();

        String numCoins = numCoinsInput.getText().toString();
        try {
            if (Integer.parseInt(numCoins) <= 0) {
                snackbarDisplay.showSnackbar(getString(R.string.zero_coins));
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
        TextUtils.copyResultsToClipboard(numbersText, snackbarDisplay);
    }

    private void saveSettings() {
        PreferencesManager.get().saveNumCoins(numCoinsInput.getText().toString());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        saveSettings();
        unbinder.unbind();
    }
}
