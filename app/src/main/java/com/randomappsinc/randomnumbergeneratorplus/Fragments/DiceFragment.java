package com.randomappsinc.randomnumbergeneratorplus.Fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.joanzapata.iconify.fonts.FontAwesomeIcons;
import com.randomappsinc.randomnumbergeneratorplus.Activities.MainActivity;
import com.randomappsinc.randomnumbergeneratorplus.Activities.SettingsActivity;
import com.randomappsinc.randomnumbergeneratorplus.R;
import com.randomappsinc.randomnumbergeneratorplus.Utils.RandUtils;
import com.randomappsinc.randomnumbergeneratorplus.Utils.UIUtils;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by alexanderchiou on 1/8/17.
 */

public class DiceFragment extends Fragment {
    @Bind(R.id.num_sides) EditText numSidesInput;
    @Bind(R.id.num_dice) EditText numDiceInput;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dice_page, container, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    private void showSnackbar(String message) {
        ((MainActivity) getActivity()).showSnackbar(message);
    }

    @OnClick(R.id.roll)
    public void roll() {
        if (verifyForm()) {
            int numSides = Integer.parseInt(numSidesInput.getText().toString());
            int numDice = Integer.parseInt(numDiceInput.getText().toString());
            RandUtils.showDiceDialog(numSides, numDice, getActivity());
        }
    }

    public boolean verifyForm() {
        UIUtils.hideKeyboard(getActivity());
        String numSides = numSidesInput.getText().toString();
        String numDice = numDiceInput.getText().toString();
        try {
            if (Integer.parseInt(numSides) <= 0) {
                showSnackbar(getString(R.string.zero_sides));
                return false;
            } else if (Integer.parseInt(numDice) <= 0) {
                showSnackbar(getString(R.string.zero_dice));
                return false;
            }
        } catch (NumberFormatException exception) {
            showSnackbar(getString(R.string.not_a_number));
            return false;
        }
        return true;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.dice_menu, menu);
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
