package com.randomappsinc.randomnumbergeneratorplus.utils;

import com.randomappsinc.randomnumbergeneratorplus.persistence.database.ExcludedNumber;

import java.util.ArrayList;
import java.util.List;

import io.realm.RealmList;

public class ConversionUtils {

    public static RealmList<ExcludedNumber> getRealmExcludes(List<Integer> excludedNums) {
        RealmList<ExcludedNumber> realmExcludedNums = new RealmList<>();
        for (Integer number : excludedNums) {
            ExcludedNumber excludedNumber = new ExcludedNumber();
            excludedNumber.setNumber(number);
            realmExcludedNums.add(excludedNumber);
        }
        return realmExcludedNums;
    }

    public static ArrayList<Integer> getPlainExcludes(RealmList<ExcludedNumber> realmExcludedNums) {
        ArrayList<Integer> excludedNumbers = new ArrayList<>();
        for (ExcludedNumber excludedNumber : realmExcludedNums) {
            excludedNumbers.add(excludedNumber.getNumber());
        }
        return excludedNumbers;
    }
}
