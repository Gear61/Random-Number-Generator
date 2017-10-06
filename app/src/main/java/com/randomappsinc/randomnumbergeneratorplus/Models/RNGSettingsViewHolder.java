package com.randomappsinc.randomnumbergeneratorplus.Models;

import android.content.Context;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.randomappsinc.randomnumbergeneratorplus.Persistence.Database.RNGConfiguration;
import com.randomappsinc.randomnumbergeneratorplus.R;
import com.randomappsinc.randomnumbergeneratorplus.Utils.MyApplication;
import com.rey.material.widget.CheckBox;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RNGSettingsViewHolder {

    @BindView(R.id.sort_options) Spinner sortOptions;
    @BindView(R.id.duplicates_toggle) CheckBox blockDupes;
    @BindView(R.id.show_sum) CheckBox showSum;
    @BindView(R.id.hide_excludes) CheckBox hideExcludes;

    public RNGSettingsViewHolder(View view, Context context) {
        ButterKnife.bind(this, view);

        String[] sortChoices = MyApplication.getAppContext().getResources().getStringArray(R.array.sort_options);
        sortOptions.setAdapter(new ArrayAdapter<>(context, R.layout.spinner_item, sortChoices));
    }

    public void updateConfig(RNGConfiguration configuration) {
        configuration.setSortIndex(sortOptions.getSelectedItemPosition());
        configuration.setNoDupes(blockDupes.isChecked());
        configuration.setShowSum(showSum.isChecked());
        configuration.setHideExcludes(showSum.isChecked());
    }

    public void loadConfig(RNGConfiguration configuration) {
        sortOptions.setSelection(configuration.getSortIndex());
        blockDupes.setCheckedImmediately(configuration.isNoDupes());
        showSum.setCheckedImmediately(configuration.isShowSum());
        hideExcludes.setCheckedImmediately(configuration.isHideExcludes());
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

    public boolean getHideExcludes() {
        return hideExcludes.isChecked();
    }
}
