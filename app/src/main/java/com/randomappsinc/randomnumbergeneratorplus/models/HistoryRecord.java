package com.randomappsinc.randomnumbergeneratorplus.models;

import com.randomappsinc.randomnumbergeneratorplus.constants.RNGType;

public class HistoryRecord {

    private @RNGType int rngType;
    private CharSequence recordText;

    public int getRngType() {
        return rngType;
    }

    public void setRngType(int rngType) {
        this.rngType = rngType;
    }

    public CharSequence getRecordText() {
        return recordText;
    }

    public void setRecordText(CharSequence recordText) {
        this.recordText = recordText;
    }
}
