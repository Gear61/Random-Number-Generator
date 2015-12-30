package com.randomappsinc.randomnumbergeneratorplus.Activities;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;
import com.randomappsinc.randomnumbergeneratorplus.R;

public class MainActivity extends StandardActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homepage);
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
            default:
                return super.onOptionsItemSelected(item);

        }
    }
}
