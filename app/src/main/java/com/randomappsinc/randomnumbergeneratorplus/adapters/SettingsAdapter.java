package com.randomappsinc.randomnumbergeneratorplus.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.randomappsinc.randomnumbergeneratorplus.R;
import com.randomappsinc.randomnumbergeneratorplus.activities.SettingsActivity;
import com.randomappsinc.randomnumbergeneratorplus.persistence.PreferencesManager;
import com.rey.material.widget.Switch;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettingsAdapter extends BaseAdapter {

    private String[] itemNames;
    private String[] itemIcons;
    private SettingsActivity activity;

    public SettingsAdapter(SettingsActivity activity) {
        this.activity = activity;
        this.itemNames = activity.getResources().getStringArray(R.array.settings_options);
        this.itemIcons = activity.getResources().getStringArray(R.array.settings_icons);
    }

    @Override
    public int getCount() {
        return itemNames.length;
    }

    @Override
    public String getItem(int position) {
        return itemNames[position];
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public class SettingsViewHolder {
        @BindView(R.id.icon) TextView mItemIcon;
        @BindView(R.id.option) TextView mItemName;
        @BindView(R.id.toggle) Switch mToggle;

        private int mPosition;

        public SettingsViewHolder(View view) {
            ButterKnife.bind(this, view);
        }

        public void loadSetting(int position) {
            mPosition = position;

            mItemName.setText(itemNames[mPosition]);
            mItemIcon.setText(itemIcons[mPosition]);

            switch (mPosition) {
                case 0:
                    mToggle.setCheckedImmediately(PreferencesManager.get().isShakeEnabled());
                    mToggle.setVisibility(View.VISIBLE);
                    break;
                case 1:
                    mToggle.setCheckedImmediately(PreferencesManager.get().shouldPlaySounds());
                    mToggle.setVisibility(View.VISIBLE);
                    break;
                default:
                    mToggle.setVisibility(View.GONE);
                    break;
            }
        }

        @OnClick(R.id.toggle)
        public void onToggleClicked() {
            switch (mPosition) {
                case 0:
                    PreferencesManager.get().setShakeEnabled(mToggle.isChecked());
                    break;
                case 1:
                    PreferencesManager.get().setPlaySounds(mToggle.isChecked());
                    break;
            }
        }
    }

    public View getView(int position, View view, ViewGroup parent) {
        SettingsViewHolder holder;
        if (view == null) {
            LayoutInflater vi = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = vi.inflate(R.layout.settings_item_cell, parent, false);
            holder = new SettingsViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (SettingsViewHolder) view.getTag();
        }
        holder.loadSetting(position);
        return view;
    }
}
