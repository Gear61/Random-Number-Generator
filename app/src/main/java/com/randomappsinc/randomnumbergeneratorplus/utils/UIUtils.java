package com.randomappsinc.randomnumbergeneratorplus.utils;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.joanzapata.iconify.Icon;
import com.joanzapata.iconify.IconDrawable;
import com.randomappsinc.randomnumbergeneratorplus.R;

public class UIUtils {

    public static void showSnackbar(View parent, String message, Context context) {
        SpannableStringBuilder spannableString = new SpannableStringBuilder(message);
        spannableString.setSpan(
                new ForegroundColorSpan(Color.WHITE),
                0,
                spannableString.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        Snackbar snackbar = Snackbar.make(parent, spannableString, Snackbar.LENGTH_LONG);
        snackbar.getView().setBackgroundColor(context.getResources().getColor(R.color.app_blue));
        snackbar.show();
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity
                .getSystemService(Activity.INPUT_METHOD_SERVICE);
        // Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        // If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        if (inputMethodManager != null) {
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static void loadMenuIcon(Menu menu, int itemId, Icon icon, Context context) {
        menu.findItem(itemId).setIcon(
                new IconDrawable(context, icon)
                        .colorRes(R.color.white)
                        .actionBarSize());
    }

    /**
     * Animates a results text view to indicate that the app is doing something.
     * This makes the case where the app generates the same thing twice less awkward.
     */
    public static void animateResults(final TextView resultsText, final CharSequence newText, final int animLength) {
        if (resultsText.getAnimation() == null || resultsText.getAnimation().hasEnded()) {
            ObjectAnimator animX = ObjectAnimator.ofFloat(resultsText, "scaleX", 0.75f);
            ObjectAnimator animY = ObjectAnimator.ofFloat(resultsText, "scaleY", 0.75f);
            ObjectAnimator fadeOut = ObjectAnimator.ofFloat(resultsText, "alpha", 0.0f);
            AnimatorSet shrink = new AnimatorSet();
            shrink.playTogether(animX, animY, fadeOut);
            shrink.setDuration(animLength);
            shrink.setInterpolator(new AccelerateInterpolator());
            shrink.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {}

                @Override
                public void onAnimationEnd(Animator animation) {
                    resultsText.setText(newText);

                    ObjectAnimator animX = ObjectAnimator.ofFloat(resultsText, "scaleX", 1.0f);
                    ObjectAnimator animY = ObjectAnimator.ofFloat(resultsText, "scaleY", 1.0f);
                    ObjectAnimator fadeIn = ObjectAnimator.ofFloat(resultsText, "alpha", 1.0f);
                    AnimatorSet grow = new AnimatorSet();
                    grow.playTogether(animX, animY, fadeIn);
                    grow.setDuration(animLength);
                    grow.setInterpolator(new AnticipateOvershootInterpolator());
                    grow.start();
                }

                @Override
                public void onAnimationCancel(Animator animation) {}

                @Override
                public void onAnimationRepeat(Animator animation) {}
            });
            shrink.start();
        }
    }

    public static void setCheckedImmediately(CompoundButton checkableView, boolean checked) {
        checkableView.setChecked(checked);
        checkableView.jumpDrawablesToCurrentState();
    }
}
