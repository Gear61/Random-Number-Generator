package com.randomappsinc.randomnumbergeneratorplus.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;
import com.randomappsinc.randomnumbergeneratorplus.Persistence.PreferencesManager;
import com.randomappsinc.randomnumbergeneratorplus.R;
import com.randomappsinc.randomnumbergeneratorplus.Utils.FormUtils;
import com.randomappsinc.randomnumbergeneratorplus.Utils.RandUtils;
import com.rey.material.widget.CheckBox;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;

public class MainActivity extends StandardActivity {
    @Bind(R.id.parent) View parent;
    @Bind(R.id.minimum) EditText minimumInput;
    @Bind(R.id.maximum) EditText maximumInput;
    @Bind(R.id.quantity) EditText quantityInput;
    @Bind(R.id.duplicates_toggle) CheckBox dupesToggle;
    @Bind(R.id.results) TextView results;

    private ArrayList<Integer> excludedNumbers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homepage);
        ButterKnife.bind(this);

        excludedNumbers = new ArrayList<>();

        if (PreferencesManager.get().isFirstTimeUser()) {
            PreferencesManager.get().rememberWelcome();
            new MaterialDialog.Builder(this)
                    .title(R.string.welcome)
                    .content(R.string.ask_for_help)
                    .positiveText(android.R.string.yes)
                    .show();
        }
    }

    @OnClick(R.id.edit_excluded)
    public void editExcluded(View view) {
        Intent intent = new Intent(this, EditExcludedActivity.class);
        intent.putExtra(EditExcludedActivity.MINIMUM_KEY, Integer.parseInt(minimumInput.getText().toString()));
        intent.putExtra(EditExcludedActivity.MAXIMUM_KEY, Integer.parseInt(maximumInput.getText().toString()));
        intent.putIntegerArrayListExtra(EditExcludedActivity.EXCLUDED_NUMBERS_KEY, excludedNumbers);
        startActivityForResult(intent, 1);
    }

    @OnTextChanged(value = R.id.minimum, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void minChanged(Editable s) {
        excludedNumbers.clear();
    }

    @OnTextChanged(value = R.id.maximum, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void maxChanged(Editable s) {
        excludedNumbers.clear();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            excludedNumbers = data.getIntegerArrayListExtra(EditExcludedActivity.EXCLUDED_NUMBERS_KEY);
        }
    }

    @OnClick(R.id.generate)
    public void generate(View view) {
        FormUtils.hideKeyboard(this);
        String minimum = minimumInput.getText().toString();
        String maximum = maximumInput.getText().toString();
        String quantity = quantityInput.getText().toString();
        int numAvailable = Integer.parseInt(maximum) - Integer.parseInt(minimum) + 1;
        int quantityRestriction = dupesToggle.isChecked() ? Integer.parseInt(quantity) : 1;
        if (minimum.isEmpty() || maximum.isEmpty() || quantity.isEmpty()) {
            FormUtils.showSnackbar(parent, getString(R.string.missing_input));
        }
        else if (Integer.parseInt(maximum) < Integer.parseInt(minimum)) {
            FormUtils.showSnackbar(parent, getString(R.string.bigger_min));
        }
        else if (Integer.parseInt(quantity) <= 0) {
            FormUtils.showSnackbar(parent, getString(R.string.non_zero_quantity));
        }
        else if (numAvailable < quantityRestriction + excludedNumbers.size()) {
            FormUtils.showSnackbar(parent, getString(R.string.overlimited_range));
        }
        else {
            List<Integer> generatedNums = RandUtils.getNumbers(Integer.parseInt(minimum), Integer.parseInt(maximum),
                    Integer.parseInt(quantity), dupesToggle.isChecked(), excludedNumbers);
            String resultsString = RandUtils.getResultsString(generatedNums);
            results.setVisibility(View.VISIBLE);
            results.setText(resultsString);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menu.findItem(R.id.load_config).setIcon(
                new IconDrawable(this, FontAwesomeIcons.fa_upload)
                        .colorRes(R.color.white)
                        .actionBarSize());
        menu.findItem(R.id.save_config).setIcon(
                new IconDrawable(this, FontAwesomeIcons.fa_save)
                        .colorRes(R.color.white)
                        .actionBarSize());
        menu.findItem(R.id.settings).setIcon(
                new IconDrawable(this, FontAwesomeIcons.fa_gear)
                        .colorRes(R.color.white)
                        .actionBarSize());
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
