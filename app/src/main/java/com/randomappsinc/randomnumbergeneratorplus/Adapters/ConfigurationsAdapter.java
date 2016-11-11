package com.randomappsinc.randomnumbergeneratorplus.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.randomappsinc.randomnumbergeneratorplus.Persistence.Database.DatabaseManager;
import com.randomappsinc.randomnumbergeneratorplus.Persistence.PreferencesManager;
import com.randomappsinc.randomnumbergeneratorplus.R;
import com.randomappsinc.randomnumbergeneratorplus.Utils.FormUtils;
import com.randomappsinc.randomnumbergeneratorplus.Utils.RandUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by alexanderchiou on 1/1/16.
 */
public class ConfigurationsAdapter extends BaseAdapter {
    private Context context;
    private List<String> content;
    private View noContent;
    private String configHint;
    private View parent;

    public ConfigurationsAdapter(Context context, View noContent, View parent) {
        this.context = context;
        this.content = new ArrayList<>(Arrays.asList(DatabaseManager.get().getAllConfigs()));
        this.noContent = noContent;
        this.configHint = context.getString(R.string.config_name);
        this.parent = parent;
        setNoContent();
    }

    public void setNoContent() {
        int viewVisibility = content.isEmpty() ? View.VISIBLE : View.GONE;
        noContent.setVisibility(viewVisibility);
    }

    public void removeConfig(int index) {
        DatabaseManager.get().deleteConfig(getItem(index));
        content.remove(index);
        notifyDataSetChanged();
        setNoContent();
    }

    public void changeName(int position, String newName) {
        content.set(position, newName);
        Collections.sort(content);
        notifyDataSetChanged();
    }

    public int getCount()
    {
        return content.size();
    }

    public String getItem(int position)
    {
        return content.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public void showDeleteDialog(final int position) {
        String confirmDeletionMessage = "Are you sure you want to delete the RNG configuration \""
                + getItem(position) + "\"?";

        new MaterialDialog.Builder(context)
                .title(R.string.confirm_delete)
                .content(confirmDeletionMessage)
                .positiveText(android.R.string.yes)
                .negativeText(android.R.string.no)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        if (PreferencesManager.get().getDefaultConfig().equals(getItem(position))) {
                            PreferencesManager.get().setDefaultConfig("");
                        }
                        removeConfig(position);
                    }
                })
                .show();
    }

    public void showRenameDialog(final int position) {
        final String currentName = getItem(position);
        new MaterialDialog.Builder(context)
                .title(R.string.rename_config)
                .input(configHint, currentName, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                        boolean submitEnabled = !(input.toString().trim().isEmpty()
                                || DatabaseManager.get().doesConfigExist(input.toString()));
                        dialog.getActionButton(DialogAction.POSITIVE).setEnabled(submitEnabled);
                    }
                })
                .alwaysCallInputCallback()
                .negativeText(android.R.string.no)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        String newName = dialog.getInputEditText().getText().toString();
                        if (PreferencesManager.get().getDefaultConfig().equals(currentName)) {
                            PreferencesManager.get().setDefaultConfig(newName);
                        }
                        DatabaseManager.get().renameSet(currentName, newName);
                        changeName(position, newName);
                    }
                })
                .show();
    }

    public void showOptions(final int position) {
        new MaterialDialog.Builder(context)
                .title(getItem(position))
                .items(RandUtils.getConfigOptions(getItem(position)))
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        if (text.toString().equals(context.getString(R.string.load_on_start))) {
                            PreferencesManager.get().setDefaultConfig(getItem(position));
                            notifyDataSetChanged();
                            FormUtils.showSnackbar(parent, context.getString(R.string.start_config_changed));
                        } else if (text.toString().equals(context.getString(R.string.rename_config))) {
                            showRenameDialog(position);
                        } else if (text.toString().equals(context.getString(R.string.delete_config))) {
                            showDeleteDialog(position);
                        }
                    }
                })
                .show();
    }

    public class ConfigViewHolder {
        @Bind(R.id.config_name) TextView configName;
        @Bind(R.id.check_icon) View checkIcon;

        public ConfigViewHolder(View view) {
            ButterKnife.bind(this, view);
        }

        public void loadConfig(int position) {
            configName.setText(getItem(position));
            if (PreferencesManager.get().getDefaultConfig().equals(getItem(position))) {
                checkIcon.setAlpha(1);
            } else {
                checkIcon.setAlpha(0);
            }
        }
    }

    public View getView(int position, View view, ViewGroup parent) {
        ConfigViewHolder holder;
        if (view == null) {
            LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = vi.inflate(R.layout.config_cell, parent, false);
            holder = new ConfigViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (ConfigViewHolder) view.getTag();
        }
        holder.loadConfig(position);
        return view;
    }
}
