package com.randomappsinc.randomnumbergeneratorplus.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.randomappsinc.randomnumbergeneratorplus.R;
import com.randomappsinc.randomnumbergeneratorplus.utils.MyApplication;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryItemViewHolder> {

    protected List<String> items = new ArrayList<>();

    public void addItem(String item) {
        items.add(0, item);
    }

    @NonNull
    @Override
    public HistoryItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.history_list_item,
                parent,
                false);
        return new HistoryItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryItemViewHolder holder, int position) {
        holder.loadItem(position);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class HistoryItemViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.item_text) TextView itemText;

        HistoryItemViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        void loadItem(int position) {
            itemText.setText(Html.fromHtml(items.get(position)));
        }

        @OnClick(R.id.item_text)
        public void onItemClicked() {
            Toast.makeText(
                    MyApplication.getAppContext(),
                    "Item clicked: " + String.valueOf(getAdapterPosition()),
                    Toast.LENGTH_LONG).show();
        }
    }
}
