package com.randomappsinc.randomnumbergeneratorplus.adapters;

import android.os.Handler;
import android.os.Looper;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.randomappsinc.randomnumbergeneratorplus.R;
import com.randomappsinc.randomnumbergeneratorplus.utils.TextUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryItemViewHolder> {

    protected List<CharSequence> items = new ArrayList<>();
    private boolean forLotto;

    public HistoryAdapter(boolean forLotto) {
        this.forLotto = forLotto;
    }

    public void setInitialItems(final List<CharSequence> initialItems) {
        Handler uiHandler = new Handler(Looper.getMainLooper());
        uiHandler.post(new Runnable() {
            @Override
            public void run() {
                items.addAll(initialItems);
                notifyDataSetChanged();
            }
        });
    }

    public void addItem(CharSequence item) {
        items.add(0, item);
        notifyDataSetChanged();
    }

    public void clear() {
        items.clear();
        notifyDataSetChanged();
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
            itemText.setText(forLotto ? items.get(position) : Html.fromHtml(items.get(position).toString()));
        }

        @OnClick(R.id.item_text)
        public void onItemClicked(View view) {
            CharSequence text = items.get(getAdapterPosition());
            TextUtils.copyHistoryRecordToClipboard(
                    forLotto ? text.toString() : TextUtils.stripHtml(text.toString()),
                    view.getContext());
        }
    }
}
