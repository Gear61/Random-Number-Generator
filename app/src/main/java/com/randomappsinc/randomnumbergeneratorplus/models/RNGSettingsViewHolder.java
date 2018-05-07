package com.randomappsinc.randomnumbergeneratorplus.models;

import android.content.Context;
import android.support.v7.widget.AppCompatCheckBox;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.randomappsinc.randomnumbergeneratorplus.R;
import com.randomappsinc.randomnumbergeneratorplus.persistence.database.RNGConfiguration;
import com.randomappsinc.randomnumbergeneratorplus.utils.MyApplication;
import com.randomappsinc.randomnumbergeneratorplus.utils.UIUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RNGSettingsViewHolder {

    @BindView(R.id.sort_options) Spinner sortOptions;
    @BindView(R.id.duplicates_toggle) AppCompatCheckBox blockDupes;
    @BindView(R.id.show_sum) AppCompatCheckBox showSum;
    @BindView(R.id.hide_excludes) AppCompatCheckBox hideExcludes;

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
        UIUtils.setCheckedImmediately(blockDupes, configuration.isNoDupes());
        UIUtils.setCheckedImmediately(showSum, configuration.isShowSum());
        UIUtils.setCheckedImmediately(hideExcludes, configuration.isHideExcludes());
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
