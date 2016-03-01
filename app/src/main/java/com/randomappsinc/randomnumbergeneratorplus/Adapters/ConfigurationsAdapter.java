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
import com.randomappsinc.randomnumbergeneratorplus.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by alexanderchiou on 1/1/16.
 */
public class ConfigurationsAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<String> content;
    private View noContent;
    private String configHint;

    public ConfigurationsAdapter(Context context, View noContent) {
        this.context = context;
        this.content = new ArrayList<>(Arrays.asList(DatabaseManager.get().getAllConfigs()));
        this.noContent = noContent;
        this.configHint = context.getString(R.string.config_name);
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
                        removeConfig(position);
                    }
                })
                .show();
    }

    public void showRenameDialog(final int position) {
        final String currentName = content.get(position);

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
                .onAny(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        if (which == DialogAction.POSITIVE) {
                            String newName = dialog.getInputEditText().getText().toString();
                            DatabaseManager.get().renameSet(currentName, newName);
                            changeName(position, newName);
                        }
                    }
                })
                .show();
    }

    public class ConfigViewHolder {
        @Bind(R.id.config_name) TextView configName;
        @Bind(R.id.edit_icon) View editIcon;
        @Bind(R.id.delete_icon) View deleteIcon;

        public ConfigViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    public View getView(final int position, View view, ViewGroup parent) {
        ConfigViewHolder holder;
        if (view == null) {
            LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = vi.inflate(R.layout.configuration_cell, parent, false);
            holder = new ConfigViewHolder(view);
            view.setTag(holder);
        }
        else {
            holder = (ConfigViewHolder) view.getTag();
        }

        holder.configName.setText(content.get(position));
        holder.editIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRenameDialog(position);
            }
        });
        holder.deleteIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteDialog(position);
            }
        });
        return view;
    }
}
