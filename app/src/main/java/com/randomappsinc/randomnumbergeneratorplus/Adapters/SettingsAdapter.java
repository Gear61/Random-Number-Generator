package com.randomappsinc.randomnumbergeneratorplus.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.randomappsinc.randomnumbergeneratorplus.Persistence.PreferencesManager;
import com.randomappsinc.randomnumbergeneratorplus.R;
import com.rey.material.widget.Switch;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by alexanderchiou on 12/30/15.
 */
public class SettingsAdapter extends BaseAdapter {
    private String[] itemNames;
    private String[] itemIcons;
    private Context context;

    public SettingsAdapter(Context context) {
        this.context = context;
        this.itemNames = context.getResources().getStringArray(R.array.settings_options);
        this.itemIcons = context.getResources().getStringArray(R.array.settings_icons);
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
        @Bind(R.id.icon) TextView itemIcon;
        @Bind(R.id.option) TextView itemName;
        @Bind(R.id.sound_toggle) Switch soundToggle;

        private int position;

        public SettingsViewHolder(View view) {
            ButterKnife.bind(this, view);
        }

        public void loadSetting(int position) {
            this.position = position;

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

        @OnClick(R.id.parent)
        public void toggleSound() {
            if (position == 0) {
                boolean currentState = soundToggle.isChecked();
                soundToggle.setChecked(!currentState);
                PreferencesManager.get().setPlaySounds(!currentState);
            }
        }
    }

    public View getView(int position, View view, ViewGroup parent) {
        SettingsViewHolder holder;
        if (view == null) {
            LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
