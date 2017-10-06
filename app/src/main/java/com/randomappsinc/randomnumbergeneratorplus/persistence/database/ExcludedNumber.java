package com.randomappsinc.randomnumbergeneratorplus.persistence.database;

import io.realm.RealmObject;

/**
 * Created by alexanderchiou on 1/1/16.
 */
public class ExcludedNumber extends RealmObject{
    private int number;

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }
}
