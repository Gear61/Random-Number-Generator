package com.randomappsinc.randomnumbergeneratorplus.models;

import android.content.Context;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.appcompat.widget.AppCompatCheckBox;

import com.randomappsinc.randomnumbergeneratorplus.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RNGSettingsViewHolder {

    @BindView(R.id.sort_options) Spinner sortOptions;
    @BindView(R.id.duplicates_toggle) AppCompatCheckBox blockDupes;
    @BindView(R.id.show_sum) AppCompatCheckBox showSum;
    @BindView(R.id.hide_excludes) AppCompatCheckBox hideExcludes;

    public RNGSettingsViewHolder(View view, Context context, RNGSettings rngSettings) {
        ButterKnife.bind(this, view);

        String[] sortChoices = context.getResources().getStringArray(R.array.sort_options);
        sortOptions.setAdapter(new ArrayAdapter<>(context, R.layout.spinner_item_rng_settings, sortChoices));

        sortOptions.setSelection(rngSettings.getSortType());
        blockDupes.setChecked(rngSettings.isNoDupes());
        showSum.setChecked(rngSettings.isShowSum());
        hideExcludes.setChecked(rngSettings.isHideExcluded());
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
