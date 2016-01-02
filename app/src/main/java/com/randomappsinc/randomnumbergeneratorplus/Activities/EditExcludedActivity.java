package com.randomappsinc.randomnumbergeneratorplus.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;
import com.randomappsinc.randomnumbergeneratorplus.Adapters.ExcludedNumbersAdapter;
import com.randomappsinc.randomnumbergeneratorplus.R;
import com.randomappsinc.randomnumbergeneratorplus.Utils.FormUtils;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by alexanderchiou on 12/31/15.
 */
public class EditExcludedActivity extends StandardActivity {
    public static final String EXCLUDED_NUMBERS_KEY = "excludedNumbers";
    public static final String MINIMUM_KEY = "minimum";
    public static final String MAXIMUM_KEY = "maximum";

    @Bind(R.id.parent) View parent;
    @Bind(R.id.add_icon) ImageView addIcon;
    @Bind(R.id.no_excluded_numbers) View noExcluded;
    @Bind(R.id.excluded_numbers) ListView excludedList;
    @Bind(R.id.excluded_number_input) EditText excludedInput;

    private ExcludedNumbersAdapter adapter;
    private int minimum;
    private int maximum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_excluded);
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        addIcon.setImageDrawable(new IconDrawable(this, FontAwesomeIcons.fa_plus)
                .colorRes(R.color.white)
                .actionBarSize());

        minimum = getIntent().getIntExtra(MINIMUM_KEY, 0);
        maximum = getIntent().getIntExtra(MAXIMUM_KEY, 0);
        ArrayList<Integer> excludedNumbers = getIntent().getIntegerArrayListExtra(EXCLUDED_NUMBERS_KEY);
        adapter = new ExcludedNumbersAdapter(this, excludedNumbers, noExcluded);
        excludedList.setAdapter(adapter);
    }

    @OnClick(R.id.add_excluded)
    public void addExcluded(View view) {
        FormUtils.hideKeyboard(this);
        String enteredExcluded = excludedInput.getText().toString();
        excludedInput.setText("");
        if (enteredExcluded.isEmpty()) {
            FormUtils.showSnackbar(parent, getString(R.string.not_a_number));
        }
        else if (Integer.parseInt(enteredExcluded) > maximum || Integer.parseInt(enteredExcluded) < minimum) {
            String range = "(" + String.valueOf(minimum) + " to " + String.valueOf(maximum) + ")";
            FormUtils.showSnackbar(parent, getString(R.string.not_in_range) + range);
        }
        else if (adapter.containsNumber(Integer.parseInt(enteredExcluded))) {
            FormUtils.showSnackbar(parent, getString(R.string.already_excluded));
        }
        else {
            adapter.addNumber(Integer.parseInt(enteredExcluded));
        }
    }

    @Override
    public void finish() {
        Intent returnIntent = new Intent();
        returnIntent.putIntegerArrayListExtra(EXCLUDED_NUMBERS_KEY, adapter.getExcludedNumbers());
        setResult(RESULT_OK, returnIntent);
        super.finish();
    }
}
