package com.randomappsinc.randomnumbergeneratorplus.Fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannedString;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.joanzapata.iconify.fonts.FontAwesomeIcons;
import com.randomappsinc.randomnumbergeneratorplus.Activities.SettingsActivity;
import com.randomappsinc.randomnumbergeneratorplus.R;
import com.randomappsinc.randomnumbergeneratorplus.Utils.RandUtils;
import com.randomappsinc.randomnumbergeneratorplus.Utils.UIUtils;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by alexanderchiou on 1/14/17.
 */

public class LottoFragment extends Fragment {
    @Bind(R.id.lotto_options) Spinner lottoSpinner;
    @Bind(R.id.results) TextView results;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.lotto_page, container, false);
        ButterKnife.bind(this, rootView);

        String[] lottoOptions = getResources().getStringArray(R.array.lotto_options);
        lottoSpinner.setAdapter(new ArrayAdapter<>(getActivity(), R.layout.spinner_item, lottoOptions));
        return rootView;
    }

    @OnClick(R.id.generate)
    public void generateTickets() {
        SpannedString lottoResults = RandUtils.getLottoResults(lottoSpinner.getSelectedItemPosition());
        results.setVisibility(View.VISIBLE);
        results.setText(lottoResults);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
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
