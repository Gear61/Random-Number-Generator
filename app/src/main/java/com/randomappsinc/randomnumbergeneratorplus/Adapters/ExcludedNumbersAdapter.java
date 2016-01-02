package com.randomappsinc.randomnumbergeneratorplus.Adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;

/**
 * Created by alexanderchiou on 1/1/16.
 */
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

    public void setNoContent() {
        if (getCount() == 0) {
            noExcludes.setVisibility(View.VISIBLE);
        }
        else {
            noExcludes.setVisibility(View.GONE);
        }
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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }
}
