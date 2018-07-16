package com.randomappsinc.randomnumbergeneratorplus.models;

import com.randomappsinc.randomnumbergeneratorplus.constants.SortType;

import java.util.ArrayList;

public class RNGSettings {

    private int minimum;
    private int maximum;
    private int numNumbers;
    private ArrayList<Integer> excludedNumbers;
    private @SortType int sortType;
    private boolean noDupes;
    private boolean showSum;
    private boolean hideExcluded;

    public int getMinimum() {
        return minimum;
    }

    public void setMinimum(int minimum) {
        this.minimum = minimum;
    }

    public int getMaximum() {
        return maximum;
    }

    public void setMaximum(int maximum) {
        this.maximum = maximum;
    }

    public int getNumNumbers() {
        return numNumbers;
    }

    public void setNumNumbers(int numNumbers) {
        this.numNumbers = numNumbers;
    }

    public ArrayList<Integer> getExcludedNumbers() {
        return excludedNumbers;
    }

    public void setExcludedNumbers(ArrayList<Integer> excludedNumbers) {
        this.excludedNumbers = excludedNumbers;
    }

    public int getSortType() {
        return sortType;
    }

    public void setSortType(int sortType) {
        this.sortType = sortType;
    }

    public boolean isNoDupes() {
        return noDupes;
    }

    public void setNoDupes(boolean noDupes) {
        this.noDupes = noDupes;
    }

    public boolean isShowSum() {
        return showSum;
    }

    public void setShowSum(boolean showSum) {
        this.showSum = showSum;
    }

    public boolean isHideExcluded() {
        return hideExcluded;
    }

    public void setHideExcluded(boolean hideExcluded) {
        this.hideExcluded = hideExcluded;
    }
}
