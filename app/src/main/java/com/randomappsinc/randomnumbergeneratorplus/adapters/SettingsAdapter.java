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
        @BindView(R.id.icon) TextView itemIcon;
        @BindView(R.id.option) TextView itemName;
        @BindView(R.id.sound_toggle) Switch soundToggle;

        public SettingsViewHolder(View view) {
            ButterKnife.bind(this, view);
        }

        public void loadSetting(int position) {
            itemName.setText(itemNames[position]);
            itemIcon.setText(itemIcons[position]);

            if (position == 0) {
                soundToggle.setCheckedImmediately(PreferencesManager.get().shouldPlaySounds());
                soundToggle.setVisibility(View.VISIBLE);
            } else {
                soundToggle.setVisibility(View.GONE);
            }
        }

        @OnClick(R.id.sound_toggle)
        public void onSoundToggle() {
            PreferencesManager.get().setPlaySounds(soundToggle.isChecked());
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
