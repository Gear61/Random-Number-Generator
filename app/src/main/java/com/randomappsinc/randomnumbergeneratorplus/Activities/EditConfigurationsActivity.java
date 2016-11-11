package com.randomappsinc.randomnumbergeneratorplus.Activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.randomappsinc.randomnumbergeneratorplus.Adapters.ConfigurationsAdapter;
import com.randomappsinc.randomnumbergeneratorplus.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnItemClick;

/**
 * Created by alexanderchiou on 1/1/16.
 */
public class EditConfigurationsActivity extends StandardActivity {
    @Bind(R.id.parent) View parent;
    @Bind(R.id.configs) ListView configs;
    @Bind(R.id.no_configs) View noConfigs;

    private ConfigurationsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_configurations);
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        adapter = new ConfigurationsAdapter(this, noConfigs, parent);
        configs.setAdapter(adapter);
    }

    @OnItemClick(R.id.configs)
    public void onConfigSelected(int position) {
        adapter.showOptions(position);
    }
}
