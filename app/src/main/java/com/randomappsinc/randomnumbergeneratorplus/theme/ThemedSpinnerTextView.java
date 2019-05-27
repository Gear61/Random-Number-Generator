package com.randomappsinc.randomnumbergeneratorplus.theme;

import android.content.Context;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;

import com.randomappsinc.randomnumbergeneratorplus.R;

public class ThemedSpinnerTextView extends AppCompatTextView implements ThemeManager.Listener {

    private ThemeManager themeManager;
    private int normalModeTextColor;
    private int darkModeTextColor;
    private int normalModeBackgroundColor;
    private int darkModeBackgroundColor;

    public ThemedSpinnerTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        themeManager = ThemeManager.get();
        normalModeTextColor = ContextCompat.getColor(context, R.color.dark_gray);
        darkModeTextColor = ContextCompat.getColor(context, R.color.white);
        normalModeBackgroundColor = ContextCompat.getColor(context, R.color.white);
        darkModeBackgroundColor = ContextCompat.getColor(context, R.color.dark_mode_black_lite);

        setTextColor(themeManager.getDarkModeEnabled(context) ? darkModeTextColor : normalModeTextColor);
        setBackgroundColor(themeManager.getDarkModeEnabled(context) ? darkModeBackgroundColor : normalModeBackgroundColor);
    }

    @Override
    public void onThemeChanged(boolean darkModeEnabled) {
        setTextColor(darkModeEnabled ? darkModeTextColor : normalModeTextColor);
        setBackgroundColor(darkModeEnabled ? darkModeBackgroundColor : normalModeBackgroundColor);
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
