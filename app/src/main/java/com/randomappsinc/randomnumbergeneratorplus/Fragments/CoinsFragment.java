package com.randomappsinc.randomnumbergeneratorplus.Fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.joanzapata.iconify.fonts.FontAwesomeIcons;
import com.randomappsinc.randomnumbergeneratorplus.Activities.MainActivity;
import com.randomappsinc.randomnumbergeneratorplus.Activities.SettingsActivity;
import com.randomappsinc.randomnumbergeneratorplus.Persistence.PreferencesManager;
import com.randomappsinc.randomnumbergeneratorplus.R;
import com.randomappsinc.randomnumbergeneratorplus.Utils.RandUtils;
import com.randomappsinc.randomnumbergeneratorplus.Utils.UIUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by alexanderchiou on 5/9/17.
 */

public class CoinsFragment extends Fragment {
    @Bind(R.id.focal_point) View focalPoint;
    @Bind(R.id.num_coins) EditText numCoinsInput;
    @Bind(R.id.results_container) View resultsContainer;
    @Bind(R.id.results) TextView resultsText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.coins_page, container, false);
        ButterKnife.bind(this, rootView);

        numCoinsInput.setText(String.valueOf(PreferencesManager.get().getNumCoins()));
        return rootView;
    }

    private void showSnackbar(String message) {
        ((MainActivity) getActivity()).showSnackbar(message);
    }

    @OnClick(R.id.flip)
    public void flip() {
        if (verifyForm()) {
            int numCoins = Integer.parseInt(numCoinsInput.getText().toString());
            List<Integer> flips = RandUtils.getNumbers(0, 1, numCoins, false, new ArrayList<Integer>());
            String results = RandUtils.getCoinResults(flips);
            resultsContainer.setVisibility(View.VISIBLE);
            resultsText.setText(Html.fromHtml(results));
        }
    }

    public boolean verifyForm() {
        UIUtils.hideKeyboard(getActivity());
        focalPoint.requestFocus();

        String numCoins = numCoinsInput.getText().toString();
        try {
            if (Integer.parseInt(numCoins) <= 0) {
                showSnackbar(getString(R.string.zero_coins));
                return false;
            }
        } catch (NumberFormatException exception) {
            showSnackbar(getString(R.string.not_a_number));
            return false;
        }
        return true;
    }

    @OnClick(R.id.copy_results)
    public void copyNumbers() {
        String numbersText = resultsText.getText().toString();
        ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Activity.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(getString(R.string.generated_numbers), numbersText);
        clipboard.setPrimaryClip(clip);
        showSnackbar(getString(R.string.copied_to_clipboard));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        PreferencesManager.get().saveNumCoins(numCoinsInput.getText().toString());
        ButterKnife.unbind(this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.regular_menu, menu);
        UIUtils.loadMenuIcon(menu, R.id.additional_settings, FontAwesomeIcons.fa_gears);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.additional_settings:
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                getActivity().overridePendingTransition(R.anim.slide_left_out, R.anim.slide_left_in);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
