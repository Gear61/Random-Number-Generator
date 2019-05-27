package com.randomappsinc.randomnumbergeneratorplus.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.randomappsinc.randomnumbergeneratorplus.R;
import com.randomappsinc.randomnumbergeneratorplus.persistence.PreferencesManager;
import com.randomappsinc.randomnumbergeneratorplus.theme.ThemeManager;
import com.randomappsinc.randomnumbergeneratorplus.utils.UIUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettingsAdapter extends RecyclerView.Adapter<SettingsAdapter.SettingViewHolder> {

    public interface ItemSelectionListener {
        void onItemClick(int position);
    }

    @NonNull private ItemSelectionListener itemSelectionListener;
    private String[] options;
    private String[] icons;
    private PreferencesManager preferencesManager;
    private ThemeManager themeManager;

    public SettingsAdapter(Context context, @NonNull ItemSelectionListener itemSelectionListener) {
        this.itemSelectionListener = itemSelectionListener;
        this.options = context.getResources().getStringArray(R.array.settings_options);
        this.icons = context.getResources().getStringArray(R.array.settings_icons);
        this.preferencesManager = new PreferencesManager(context);
        this.themeManager = ThemeManager.get();
    }

    @Override
    @NonNull
    public SettingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.settings_item_cell,
                parent,
                false);
        return new SettingViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull SettingViewHolder holder, int position) {
        holder.loadSetting(position);
    }

    @Override
    public int getItemCount() {
        return options.length;
    }

    class SettingViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.icon) TextView icon;
        @BindView(R.id.option) TextView option;
        @BindView(R.id.toggle) Switch toggle;

        SettingViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        void loadSetting(int position) {
            option.setText(options[position]);
            icon.setText(icons[position]);

            switch (position) {
                case 0:
                    UIUtils.setCheckedImmediately(toggle, preferencesManager.isShakeEnabled());
                    toggle.setVisibility(View.VISIBLE);
                    break;
                case 1:
                    UIUtils.setCheckedImmediately(toggle, preferencesManager.shouldPlaySounds());
                    toggle.setVisibility(View.VISIBLE);
                    break;
                case 2:
                    UIUtils.setCheckedImmediately(toggle, preferencesManager.getDarkModeEnabled());
                    toggle.setVisibility(View.VISIBLE);
                    break;
                default:
                    toggle.setVisibility(View.GONE);
                    break;
            }
        }

        @OnClick(R.id.toggle)
        void onToggle() {
            switch (getAdapterPosition()) {
                case 0:
                    preferencesManager.setShakeEnabled(toggle.isChecked());
                    break;
                case 1:
                    preferencesManager.setPlaySounds(toggle.isChecked());
                    break;
                case 2:
                    themeManager.setDarkModeEnabled(toggle.getContext(), toggle.isChecked());
                    break;
            }
        }

        @OnClick(R.id.parent)
        void onSettingSelected() {
            itemSelectionListener.onItemClick(getAdapterPosition());
        }
    }
}
