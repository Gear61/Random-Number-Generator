package com.randomappsinc.randomnumbergeneratorplus.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import com.randomappsinc.randomnumbergeneratorplus.R;
import com.randomappsinc.randomnumbergeneratorplus.persistence.PreferencesManager;
import com.randomappsinc.randomnumbergeneratorplus.utils.UIUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettingsAdapter extends RecyclerView.Adapter<SettingsAdapter.SettingViewHolder> {

    public interface ItemSelectionListener {
        void onItemClick(int position);
    }

    @NonNull
    private ItemSelectionListener itemSelectionListener;
    private Context context;
    private String[] options;
    private String[] icons;

    public SettingsAdapter(Context context, @NonNull ItemSelectionListener itemSelectionListener) {
        this.itemSelectionListener = itemSelectionListener;
        this.context = context;
        this.options = context.getResources().getStringArray(R.array.settings_options);
        this.icons = context.getResources().getStringArray(R.array.settings_icons);
    }

    @Override
    @NonNull
    public SettingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(
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
                    UIUtils.setCheckedImmediately(toggle, PreferencesManager.get().isShakeEnabled());
                    toggle.setVisibility(View.VISIBLE);
                    break;
                case 1:
                    UIUtils.setCheckedImmediately(toggle, PreferencesManager.get().shouldPlaySounds());
                    toggle.setVisibility(View.VISIBLE);
                    break;
                default:
                    toggle.setVisibility(View.GONE);
                    break;
            }
        }

        @OnClick(R.id.toggle)
        public void onToggle() {
            if (getAdapterPosition() == 0) {
                PreferencesManager.get().setShakeEnabled(toggle.isChecked());
            } else if (getAdapterPosition() == 1) {
                PreferencesManager.get().setPlaySounds(toggle.isChecked());
            }
        }

        @OnClick(R.id.parent)
        public void onSettingSelected() {
            itemSelectionListener.onItemClick(getAdapterPosition());
        }
    }
}
