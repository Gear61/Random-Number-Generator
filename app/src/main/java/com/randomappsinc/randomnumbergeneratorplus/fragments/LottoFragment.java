package com.randomappsinc.randomnumbergeneratorplus.fragments;

import android.os.Bundle;
import android.text.SpannedString;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.randomappsinc.randomnumbergeneratorplus.R;
import com.randomappsinc.randomnumbergeneratorplus.activities.MainActivity;
import com.randomappsinc.randomnumbergeneratorplus.constants.RNGType;
import com.randomappsinc.randomnumbergeneratorplus.persistence.HistoryDataManager;
import com.randomappsinc.randomnumbergeneratorplus.utils.RandUtils;
import com.randomappsinc.randomnumbergeneratorplus.utils.ShakeManager;
import com.randomappsinc.randomnumbergeneratorplus.utils.TextUtils;
import com.randomappsinc.randomnumbergeneratorplus.utils.UIUtils;

import butterknife.BindColor;
import butterknife.BindInt;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class LottoFragment extends Fragment {

    @BindView(R.id.lotto_options) Spinner lottoSpinner;
    @BindView(R.id.results_container) View resultsContainer;
    @BindView(R.id.results) TextView results;

    @BindColor(R.color.green) int green;
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
            if (currentRngPage == RNGType.LOTTO) {
                generateTickets();
            }
        }
    };

    private HistoryDataManager historyDataManager;
    private ShakeManager shakeManager = ShakeManager.get();
    private Unbinder unbinder;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.lotto_page, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        results.setGravity(Gravity.CENTER_HORIZONTAL);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        historyDataManager = HistoryDataManager.get(getActivity());
        String[] lottoOptions = getResources().getStringArray(R.array.lotto_options);
        lottoSpinner.setAdapter(new ArrayAdapter<>(getActivity(), R.layout.spinner_item, lottoOptions));
        shakeManager.registerListener(shakeListener);
    }

    @OnClick(R.id.generate)
    public void generateTickets() {
        MainActivity mainActivity = (MainActivity) getActivity();
        if (mainActivity != null) {
            mainActivity.playSound(RNGType.LOTTO);
        }
        resultsContainer.setVisibility(View.VISIBLE);
        SpannedString lottoResults = RandUtils.getLottoResults(lottoSpinner.getSelectedItemPosition(), green);
        historyDataManager.addHistoryRecord(RNGType.LOTTO, lottoResults.toString());
        UIUtils.animateResults(results, lottoResults, resultsAnimationLength);
    }

    @OnClick(R.id.copy_results)
    public void copyNumbers() {
        String numbersText = results.getText().toString();
        TextUtils.copyResultsToClipboard(numbersText, snackbarDisplay, getActivity());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        shakeManager.unregisterListener(shakeListener);
        unbinder.unbind();
    }
}
