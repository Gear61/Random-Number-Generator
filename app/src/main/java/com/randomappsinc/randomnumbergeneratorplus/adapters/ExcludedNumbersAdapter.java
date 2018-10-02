package com.randomappsinc.randomnumbergeneratorplus.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.randomappsinc.randomnumbergeneratorplus.R;

import java.util.ArrayList;
import java.util.Collections;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ExcludedNumbersAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Integer> excludedNumbers;
    private View noExcludes;

    public ExcludedNumbersAdapter(Context context, ArrayList<Integer> excludedNumbers, View noExcludes) {
        this.context = context;
        this.excludedNumbers = excludedNumbers;
        this.noExcludes = noExcludes;
        setNoContent();
    }

    public ArrayList<Integer> getExcludedNumbers() {
        return excludedNumbers;
    }

    private void setNoContent() {
        noExcludes.setVisibility(getCount() == 0 ? View.VISIBLE : View.GONE);
    }

    public boolean containsNumber(Integer number) {
        return excludedNumbers.contains(number);
    }

    public void addNumber(Integer number) {
        excludedNumbers.add(number);
        Collections.sort(excludedNumbers);
        notifyDataSetChanged();
        setNoContent();
    }

    private void removeNumber(int position) {
        excludedNumbers.remove(position);
        Collections.sort(excludedNumbers);
        notifyDataSetChanged();
        setNoContent();
    }

    @Override
    public int getCount() {
        return excludedNumbers.size();
    }

    @Override
    public Integer getItem(int position) {
        return excludedNumbers.get(position);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).hashCode();
    }

    class ExcludedNumberViewHolder {
        @BindView(R.id.excluded_number) TextView excludedNumber;
        @BindView(R.id.delete_icon) View deleteIcon;

        public ExcludedNumberViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        ExcludedNumberViewHolder holder;
        if (view == null) {
            LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = vi.inflate(R.layout.excluded_number_cell, parent, false);
            holder = new ExcludedNumberViewHolder(view);
            view.setTag(holder);
        }
        else {
            holder = (ExcludedNumberViewHolder) view.getTag();
        }

        holder.excludedNumber.setText(String.valueOf(getItem(position)));
        holder.deleteIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeNumber(position);
            }
        });

        return view;
    }
}
