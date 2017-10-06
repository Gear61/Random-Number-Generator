package com.randomappsinc.randomnumbergeneratorplus.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.randomappsinc.randomnumbergeneratorplus.R;
import com.randomappsinc.randomnumbergeneratorplus.adapters.ConfigurationsAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnItemClick;

public class EditConfigurationsActivity extends StandardActivity {

    @BindView(R.id.parent) View parent;
    @BindView(R.id.configs) ListView configs;
    @BindView(R.id.no_configs) View noConfigs;

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
