package com.randomappsinc.randomnumbergeneratorplus.theme;

import android.content.Context;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;

import com.randomappsinc.randomnumbergeneratorplus.R;

public class ThemedSpinnerTextViewSpecial extends AppCompatTextView implements ThemeManager.Listener {

    private ThemeManager themeManager;
    private int normalModeTextColor;
    private int darkModeTextColor;
    private int normalModeBackgroundColor;

    public ThemedSpinnerTextViewSpecial(Context context, AttributeSet attrs) {
        super(context, attrs);
        themeManager = ThemeManager.get();
        normalModeTextColor = ContextCompat.getColor(context, R.color.dark_gray);
        darkModeTextColor = ContextCompat.getColor(context, R.color.white);
        normalModeBackgroundColor = ContextCompat.getColor(context, R.color.white);
        setColors();
    }

    @Override
    public void onThemeChanged(boolean darkModeEnabled) {
        setColors();
    }

    private void setColors() {
        setTextColor(themeManager.getDarkModeEnabled(getContext()) ? darkModeTextColor : normalModeTextColor);
        if (themeManager.getDarkModeEnabled(getContext())) {
            setBackgroundDrawable(null);
        } else {
            setBackgroundColor(normalModeBackgroundColor);
        }
    }

    @Override
    public void onAttachedToWindow() {
        themeManager.registerListener(this);
        super.onAttachedToWindow();
    }

    @Override
    public void onDetachedFromWindow() {
        themeManager.unregisterListener(this);
        super.onDetachedFromWindow();
    }
}
