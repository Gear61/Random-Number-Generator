package com.randomappsinc.randomnumbergeneratorplus.Utils;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.randomappsinc.randomnumbergeneratorplus.Persistence.PreferencesManager;
import com.randomappsinc.randomnumbergeneratorplus.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * Created by alexanderchiou on 12/31/15.
 */
public class RandUtils {
    public static List<Integer> getNumbers(int min, int max, int quantity, boolean noDupes, List<Integer> excludedNums) {
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

    public static int generateNumInRange(int min, int max) {
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

    public static int generateNumInPosRange(int min, int max) {
        Random random = MyApplication.getRandom();
        return random.nextInt((max - min) + 1) + min;
    }

    public static String getResultsString(List<Integer> numbers) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < numbers.size(); i++) {
            if (i != 0) {
                stringBuilder.append(", ");
            }
            stringBuilder.append(String.valueOf(numbers.get(i)));
        }
        return stringBuilder.toString();
    }

    public static void copyNumsToClipboard(String numbers, View parent) {
        Context context = MyApplication.getAppContext();
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Activity.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(context.getString(R.string.generated_numbers), numbers);
        clipboard.setPrimaryClip(clip);
        FormUtils.showSnackbar(parent, context.getString(R.string.copy_confirmation));
    }

    public static void showResultsDialog(String results, Context context) {
        new MaterialDialog.Builder(context)
                .title(R.string.generated_numbers)
                .content(results)
                .positiveText(android.R.string.yes)
                .show();
    }

    public static String getExcludedList(List<Integer> excludedNums) {
        StringBuilder excludedList = new StringBuilder();
        if (excludedNums.isEmpty()) {
            return MyApplication.getAppContext().getString(R.string.no_excluded_numbers);
        }
        for (Integer excludedNum : excludedNums) {
            if (excludedList.length() != 0) {
                excludedList.append("\n");
            }
            excludedList.append(String.valueOf(excludedNum));
        }
        return excludedList.toString();
    }

    public static String[] getConfigOptions(String config) {
        Context context = MyApplication.getAppContext();
        List<String> options = new ArrayList<>();
        if (!PreferencesManager.get().getDefaultConfig().equals(config)) {
            options.add(context.getString(R.string.load_on_start));
        }
        options.add(context.getString(R.string.rename_config));
        options.add(context.getString(R.string.delete_config));
        return options.toArray(new String[options.size()]);
    }
}
