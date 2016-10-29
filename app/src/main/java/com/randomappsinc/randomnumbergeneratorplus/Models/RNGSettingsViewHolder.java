package com.randomappsinc.randomnumbergeneratorplus.Models;

import android.view.View;
import android.widget.Spinner;

import com.randomappsinc.randomnumbergeneratorplus.Persistence.Database.RNGConfiguration;
import com.randomappsinc.randomnumbergeneratorplus.R;
import com.rey.material.widget.CheckBox;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by alexanderchiou on 10/28/16.
 */

public class RNGSettingsViewHolder {
    @Bind(R.id.sort_options) Spinner sortOptions;
    @Bind(R.id.duplicates_toggle) CheckBox blockDupes;
    @Bind(R.id.show_sum) CheckBox showSum;

    public RNGSettingsViewHolder(View view) {
        ButterKnife.bind(this, view);
    }

    public void updateConfig(RNGConfiguration configuration) {
        configuration.setSortIndex(sortOptions.getSelectedItemPosition());
        configuration.setNoDupes(blockDupes.isChecked());
        configuration.setShowSum(showSum.isChecked());
    }

    public void loadConfig(RNGConfiguration configuration) {
        sortOptions.setSelection(configuration.getSortIndex());
        blockDupes.setCheckedImmediately(configuration.isNoDupes());
        showSum.setCheckedImmediately(configuration.isShowSum());
    }

    public boolean getNoDupes() {
        return blockDupes.isChecked();
    }

    public int getSortIndex() {
        return sortOptions.getSelectedItemPosition();
    }

    public boolean getShowSum() {
        return showSum.isChecked();
    }
}
