package com.randomappsinc.randomnumbergeneratorplus.fragments;

import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.randomappsinc.randomnumbergeneratorplus.R;
import com.randomappsinc.randomnumbergeneratorplus.activities.MainActivity;
import com.randomappsinc.randomnumbergeneratorplus.constants.RNGType;
import com.randomappsinc.randomnumbergeneratorplus.persistence.HistoryDataManager;
import com.randomappsinc.randomnumbergeneratorplus.persistence.PreferencesManager;
import com.randomappsinc.randomnumbergeneratorplus.utils.RandUtils;
import com.randomappsinc.randomnumbergeneratorplus.utils.ShakeManager;
import com.randomappsinc.randomnumbergeneratorplus.utils.TextUtils;
import com.randomappsinc.randomnumbergeneratorplus.utils.UIUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindInt;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class CoinsFragment extends Fragment {

    @BindView(R.id.focal_point) View focalPoint;
    @BindView(R.id.num_coins) EditText numCoinsInput;
    @BindView(R.id.results_container) View resultsContainer;
    @BindView(R.id.results) TextView results;

    @BindInt(R.integer.shorter_anim_length) int resultsAnimationLength;

    private final TextUtils.SnackbarDisplay snackbarDisplay = new TextUtils.SnackbarDisplay() {
        @Override
        public void showSnackbar(String message) {
            ((MainActivity) getActivity()).showSnackbar(message);
        }
    };

    private final ShakeManager.Listener shakeListener = new ShakeManager.Listener() {
        @Override
        public void onShakeDetected(int currentRngPage) {
            if (currentRngPage == RNGType.COINS) {
                flip();
            }
        }
    };

    private HistoryDataManager historyDataManager;
    private ShakeManager shakeManager = ShakeManager.get();
    private PreferencesManager preferencesManager;
    private Unbinder unbinder;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.coins_page, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        shakeManager.registerListener(shakeListener);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        historyDataManager = HistoryDataManager.get(getActivity());
        preferencesManager = new PreferencesManager(getActivity());
        numCoinsInput.setText(String.valueOf(preferencesManager.getNumCoins()));
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        saveSettings();
    }

    @OnClick(R.id.flip)
    public void flip() {
        if (verifyForm()) {
            MainActivity mainActivity = (MainActivity) getActivity();
            if (mainActivity != null) {
                mainActivity.playSound(RNGType.COINS);
            }
            int numCoins = Integer.parseInt(numCoinsInput.getText().toString());
            List<Integer> flips = RandUtils.getNumbers(
                    0,
                    1,
                    numCoins,
                    false,
                    new ArrayList<Integer>());
            resultsContainer.setVisibility(View.VISIBLE);
            String flipText = RandUtils.getCoinResults(flips, getActivity());
            historyDataManager.addHistoryRecord(RNGType.COINS, flipText);
            UIUtils.animateResults(results, Html.fromHtml(flipText), resultsAnimationLength);
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
        TextUtils.copyResultsToClipboard(numbersText, snackbarDisplay, getActivity());
    }

    private void saveSettings() {
        preferencesManager.saveNumCoins(numCoinsInput.getText().toString());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        shakeManager.unregisterListener(shakeListener);
        saveSettings();
        unbinder.unbind();
    }
}
