package com.randomappsinc.randomnumbergeneratorplus.utils;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.SpannedString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;

import com.randomappsinc.randomnumbergeneratorplus.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Set;

public class RandUtils {

    // Lotto
    private static final int NUM_NORMAL_BALLS = 5;
    private static final int POWERBALL_NORMAL = 69;
    private static final int POWERBALL_SPECIAL = 26;
    private static final int MEGA_MILLIONS_NORMAL = 70;
    private static final int MEGA_MILLIONS_SPECIAL = 25;

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

    private static int generateNumInRange(int min, int max) {
        if (min >= 0 && max >= 0) {
            return generateNumInPosRange(min, max);
        } else if (min <= 0 && max <= 0) {
            int posNum = generateNumInPosRange(max * -1, min * -1);
            return posNum * -1;
        } else {
            int deficit = min * -1;
            int preShift = generateNumInPosRange(min + deficit, max + deficit);
            return preShift - deficit;
        }
    }

    private static int generateNumInPosRange(int min, int max) {
        Random random = new Random();
        return random.nextInt((max - min) + 1) + min;
    }

    public static String getResultsString(List<Integer> numbers, boolean showSum, String numbersPrefix, String sumPrefix) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<b>");
        stringBuilder.append(numbersPrefix);
        stringBuilder.append("</b>");

        long sum = 0;
        for (int i = 0; i < numbers.size(); i++) {
            if (i != 0) {
                stringBuilder.append(", ");
            }
            stringBuilder.append(String.valueOf(numbers.get(i)));

            sum += numbers.get(i);
        }

        if (showSum) {
            stringBuilder.append("<br><br><b>");
            stringBuilder.append(sumPrefix);
            stringBuilder.append("</b>");
            stringBuilder.append(String.valueOf(sum));
        }

        return stringBuilder.toString();
    }

    public static String getDiceResults(List<Integer> rolls, String rollsPrefix, String sumPrefix) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<b>");
        stringBuilder.append(rollsPrefix);
        stringBuilder.append("</b>");

        long sum = 0;
        for (int i = 0; i < rolls.size(); i++) {
            if (i != 0) {
                stringBuilder.append(", ");
            }
            stringBuilder.append(String.valueOf(rolls.get(i)));

            sum += rolls.get(i);
        }

        stringBuilder.append("<br><br><b>");
        stringBuilder.append(sumPrefix);
        stringBuilder.append("</b>");
        stringBuilder.append(String.valueOf(sum));

        return stringBuilder.toString();
    }

    public static String getExcludedList(List<Integer> excludedNums, String noExcludedNumbers) {
        StringBuilder excludedList = new StringBuilder();
        if (excludedNums.isEmpty()) {
            return noExcludedNumbers;
        }
        for (Integer excludedNum : excludedNums) {
            if (excludedList.length() != 0) {
                excludedList.append(", ");
            }
            excludedList.append(String.valueOf(excludedNum));
        }
        return excludedList.toString();
    }

    public static SpannedString getLottoResults(int spinnerIndex, int specialColor) {
        switch (spinnerIndex) {
            // Powerball
            case 0:
                return getLottoTickets(POWERBALL_NORMAL, POWERBALL_SPECIAL, specialColor);
            // Mega Millions
            case 1:
                return getLottoTickets(MEGA_MILLIONS_NORMAL, MEGA_MILLIONS_SPECIAL, specialColor);
            default:
                return getLottoTickets(POWERBALL_NORMAL, POWERBALL_SPECIAL, specialColor);
        }
    }

    private static SpannedString getLottoTickets(int normalMax, int specialMax, int specialColor) {
        Spannable ticket1 = getLottoTicket(normalMax, specialMax, true, specialColor);
        Spannable ticket2 = getLottoTicket(normalMax, specialMax, true, specialColor);
        Spannable ticket3 = getLottoTicket(normalMax, specialMax, true, specialColor);
        Spannable ticket4 = getLottoTicket(normalMax, specialMax, true, specialColor);
        Spannable ticket5 = getLottoTicket(normalMax, specialMax, false, specialColor);
        return (SpannedString) TextUtils.concat(ticket1, ticket2, ticket3, ticket4, ticket5);
    }

    private static Spannable getLottoTicket(int normalMax, int specialMax, boolean addNewLine, int specialColor) {
        List<Integer> normals = getNumbers(1, normalMax, NUM_NORMAL_BALLS, true, new ArrayList<Integer>());
        int special = getNumbers(1, specialMax, 1, false, new ArrayList<Integer>()).get(0);

        StringBuilder ticket = new StringBuilder();
        for (int i = 0; i < NUM_NORMAL_BALLS; i++) {
            if (i != 0) {
                ticket.append("  ");
            }
            ticket.append(String.format(Locale.US, "%02d", normals.get(i)));
        }
        ticket.append("        ");
        ticket.append(String.format(Locale.US, "%02d", special));
        if (addNewLine) {
            ticket.append("\n");
        }

        SpannableString ticketFormatted = new SpannableString(ticket.toString());

        ticketFormatted.setSpan(new ForegroundColorSpan(specialColor), 26, 28, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        return ticketFormatted;
    }

    public static String getCoinResults(List<Integer> flips, Context context) {
        String heads = context.getString(R.string.heads);
        String tails = context.getString(R.string.tails);

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<b>");
        stringBuilder.append(context.getString(R.string.sides_prefix));
        stringBuilder.append("</b>");

        int numHeads = 0;
        int numTails = 0;
        for (int i = 0; i < flips.size(); i++) {
            if (i != 0) {
                stringBuilder.append(", ");
            }
            stringBuilder.append(flips.get(i) == 0 ? heads : tails);

            if (flips.get(i) == 0) {
                numHeads++;
            } else {
                numTails++;
            }
        }

        stringBuilder.append("<br><br><b>");
        stringBuilder.append(context.getString(R.string.num_heads_prefix));
        stringBuilder.append("</b>");
        stringBuilder.append(String.valueOf(numHeads));

        stringBuilder.append("<br><br><b>");
        stringBuilder.append(context.getString(R.string.num_tails_prefix));
        stringBuilder.append("</b>");
        stringBuilder.append(String.valueOf(numTails));

        return stringBuilder.toString();
    }
}
