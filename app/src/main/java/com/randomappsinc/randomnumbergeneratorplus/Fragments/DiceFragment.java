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

import com.joanzapata.iconify.fonts.FontAwesomeIcons;
import com.randomappsinc.randomnumbergeneratorplus.Activities.SettingsActivity;
import com.randomappsinc.randomnumbergeneratorplus.R;
import com.randomappsinc.randomnumbergeneratorplus.Utils.UIUtils;

import butterknife.ButterKnife;

/**
 * Created by alexanderchiou on 1/8/17.
 */

public class DiceFragment extends Fragment {
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

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        UIUtils.hideKeyboard(getActivity());
        super.startActivityForResult(intent, requestCode);
        getActivity().overridePendingTransition(R.anim.slide_left_out, R.anim.slide_left_in);
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
