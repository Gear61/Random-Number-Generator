package com.randomappsinc.randomnumbergeneratorplus.Utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * Created by alexanderchiou on 12/31/15.
 */
public class RandUtils {
    public List<Integer> getNumbers(int min, int max, int quantity, boolean noDupes, List<Integer> excludedNums) {
        List<Integer> numbers = new ArrayList<>();
        Set<Integer> excludedNumsSet = new HashSet<>(excludedNums);
        int numAdded = 0;
        while (numAdded < quantity) {
            int attempt = generateNumInRange(min, max);
            if (!excludedNumsSet.contains(attempt)) {
                numbers.add(attempt);
                if (noDupes) {
                    excludedNumsSet.add(attempt);
                }
                numAdded++;
            }
        }
        return numbers;
    }

    public int generateNumInRange(int min, int max) {
        if (min >= 0 && max >= 0) {
            return generateNumInPosRange(min, max);
        }
        else if (min <= 0 && max <= 0) {
            int posNum = generateNumInPosRange(max * -1, min * -1);
            return posNum * -1;
        }
        else {
            int deficit = min * -1;
            int preShift = generateNumInPosRange(min + deficit, max + deficit);
            return preShift - deficit;
        }
    }

    public int generateNumInPosRange(int min, int max) {
        Random random = MyApplication.getRandom();
        return random.nextInt((max - min) + 1) + min;
    }
}
