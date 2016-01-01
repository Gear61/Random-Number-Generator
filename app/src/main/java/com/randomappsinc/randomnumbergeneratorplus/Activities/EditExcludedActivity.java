package com.randomappsinc.randomnumbergeneratorplus.Activities;

import android.content.Intent;
import android.os.Bundle;

import com.randomappsinc.randomnumbergeneratorplus.R;

import java.util.ArrayList;

import butterknife.ButterKnife;

/**
 * Created by alexanderchiou on 12/31/15.
 */
public class EditExcludedActivity extends StandardActivity {
    public static final String EXCLUDED_NUMBERS_KEY = "excludedNumbers";

    private ArrayList<Integer> excludedNumbers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_excluded);
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        excludedNumbers = getIntent().getIntegerArrayListExtra(EXCLUDED_NUMBERS_KEY);
    }

    @Override
    public void finish() {
        Intent returnIntent = new Intent();
        returnIntent.putIntegerArrayListExtra(EXCLUDED_NUMBERS_KEY, excludedNumbers);
        setResult(RESULT_OK, returnIntent);
        super.finish();
    }
}
