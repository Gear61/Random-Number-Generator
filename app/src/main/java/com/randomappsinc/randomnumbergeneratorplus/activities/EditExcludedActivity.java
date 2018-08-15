package com.randomappsinc.randomnumbergeneratorplus.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.IoniconsIcons;
import com.randomappsinc.randomnumbergeneratorplus.R;
import com.randomappsinc.randomnumbergeneratorplus.adapters.ExcludedNumbersAdapter;
import com.randomappsinc.randomnumbergeneratorplus.utils.UIUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class EditExcludedActivity extends StandardActivity {

    public static final String EXCLUDED_NUMBERS_KEY = "excludedNumbers";
    public static final String MINIMUM_KEY = "minimum";
    public static final String MAXIMUM_KEY = "maximum";

    @BindView(R.id.parent) View parent;
    @BindView(R.id.add_icon) ImageView addIcon;
    @BindView(R.id.no_excluded_numbers) View noExcluded;
    @BindView(R.id.excluded_numbers) ListView excludedList;
    @BindView(R.id.excluded_number_input) EditText excludedInput;

    private ExcludedNumbersAdapter adapter;
    private int minimum;
    private int maximum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_excluded);
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        addIcon.setImageDrawable(new IconDrawable(this, IoniconsIcons.ion_android_add)
                .colorRes(R.color.white)
                .actionBarSize());

        minimum = getIntent().getIntExtra(MINIMUM_KEY, 0);
        maximum = getIntent().getIntExtra(MAXIMUM_KEY, 0);
        ArrayList<Integer> excludedNumbers = getIntent().getIntegerArrayListExtra(EXCLUDED_NUMBERS_KEY);
        adapter = new ExcludedNumbersAdapter(this, excludedNumbers, noExcluded);
        excludedList.setAdapter(adapter);
    }

    @OnClick(R.id.add_excluded)
    public void addExcluded() {
        String enteredExcluded = excludedInput.getText().toString();
        excludedInput.setText("");
        try {
            if (enteredExcluded.isEmpty()) {
                UIUtils.showSnackbar(parent, getString(R.string.not_a_number), this);
            } else if (Integer.parseInt(enteredExcluded) > maximum || Integer.parseInt(enteredExcluded) < minimum) {
                String range = "(" + String.valueOf(minimum) + " to " + String.valueOf(maximum) + ")";
                UIUtils.showSnackbar(parent, getString(R.string.not_in_range) + range, this);
            } else if (adapter.containsNumber(Integer.parseInt(enteredExcluded))) {
                UIUtils.showSnackbar(parent, getString(R.string.already_excluded), this);
            } else {
                adapter.addNumber(Integer.parseInt(enteredExcluded));
            }
        } catch (NumberFormatException exception) {
            UIUtils.showSnackbar(parent, getString(R.string.not_a_number), this);
        }
    }

    @Override
    public void finish() {
        Intent returnIntent = new Intent();
        returnIntent.putIntegerArrayListExtra(EXCLUDED_NUMBERS_KEY, adapter.getExcludedNumbers());
        setResult(RESULT_OK, returnIntent);
        super.finish();
    }

    @OnClick(R.id.submit)
    public void submit() {
        finish();
    }
}
