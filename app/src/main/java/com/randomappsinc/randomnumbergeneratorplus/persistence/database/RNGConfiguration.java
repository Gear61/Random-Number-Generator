package com.randomappsinc.randomnumbergeneratorplus.persistence.database;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * Created by alexanderchiou on 1/1/16.
 */
public class RNGConfiguration extends RealmObject {
    @PrimaryKey
    @Required
    private String configName;

    private int minimum;
    private int maximum;
    private int quantity;
    private boolean noDupes;
    private RealmList<ExcludedNumber> excludedNumbers;
    private int sortIndex;
    private boolean showSum;
    private boolean hideExcludes;

    public String getConfigName() {
        return configName;
    }

    public void setConfigName(String configName) {
        this.configName = configName;
    }

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

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public boolean isNoDupes() {
        return noDupes;
    }

    public void setNoDupes(boolean noDupes) {
        this.noDupes = noDupes;
    }

    public RealmList<ExcludedNumber> getExcludedNumbers() {
        return excludedNumbers;
    }

    public void setExcludedNumbers(RealmList<ExcludedNumber> excludedNumbers) {
        this.excludedNumbers = excludedNumbers;
    }

    public int getSortIndex() {
        return sortIndex;
    }

    public void setSortIndex(int sortIndex) {
        this.sortIndex = sortIndex;
    }

    public boolean isShowSum() {
        return showSum;
    }

    public void setShowSum(boolean showSum) {
        this.showSum = showSum;
    }

    public boolean isHideExcludes() {
        return hideExcludes;
    }

    public void setHideExcludes(boolean hideExcludes) {
        this.hideExcludes = hideExcludes;
    }
}
