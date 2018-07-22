package com.randomappsinc.randomnumbergeneratorplus.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.SpannedString;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.randomappsinc.randomnumbergeneratorplus.R;
import com.randomappsinc.randomnumbergeneratorplus.activities.MainActivity;
import com.randomappsinc.randomnumbergeneratorplus.persistence.PreferencesManager;
import com.randomappsinc.randomnumbergeneratorplus.utils.RandUtils;
import com.randomappsinc.randomnumbergeneratorplus.utils.TextUtils;
import com.randomappsinc.randomnumbergeneratorplus.utils.UIUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class LottoFragment extends Fragment {

    @BindView(R.id.lotto_options) Spinner lottoSpinner;
    @BindView(R.id.results_container) View resultsContainer;
    @BindView(R.id.results) TextView results;

    private final TextUtils.SnackbarDisplay snackbarDisplay = new TextUtils.SnackbarDisplay() {
        @Override
        public void showSnackbar(String message) {
            ((MainActivity) getActivity()).showSnackbar(message);
        }
    };

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
        String[] lottoOptions = getResources().getStringArray(R.array.lotto_options);
        lottoSpinner.setAdapter(new ArrayAdapter<>(getActivity(), R.layout.spinner_item, lottoOptions));
    }

    @OnClick(R.id.generate)
    public void generateTickets() {
        if (PreferencesManager.get().shouldPlaySounds()) {
            ((MainActivity) getActivity()).playSound("lotto_scratch.wav");
        }
        resultsContainer.setVisibility(View.VISIBLE);
        SpannedString lottoResults = RandUtils.getLottoResults(lottoSpinner.getSelectedItemPosition());
        UIUtils.animateResults(results, lottoResults);
    }

    @OnClick(R.id.copy_results)
    public void copyNumbers() {
        String numbersText = results.getText().toString();
        TextUtils.copyResultsToClipboard(numbersText, snackbarDisplay);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
