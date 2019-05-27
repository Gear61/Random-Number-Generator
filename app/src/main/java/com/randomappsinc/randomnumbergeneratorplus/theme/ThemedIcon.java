package com.randomappsinc.randomnumbergeneratorplus.theme;

import android.content.Context;
import android.util.AttributeSet;

import androidx.core.content.ContextCompat;

import com.joanzapata.iconify.widget.IconTextView;
import com.randomappsinc.randomnumbergeneratorplus.R;

public class ThemedIcon extends IconTextView implements ThemeManager.Listener {

    private ThemeManager themeManager;
    private int normalModeColor;
    private int darkModeColor;

    public ThemedIcon(Context context, AttributeSet attrs) {
        super(context, attrs);
        themeManager = ThemeManager.get();
        normalModeColor = ContextCompat.getColor(context, R.color.dark_gray);
        darkModeColor = ContextCompat.getColor(context, R.color.white);

        setTextColor(themeManager.getDarkModeEnabled(context) ? darkModeColor : normalModeColor);
    }

    @Override
    public void onThemeChanged(boolean darkModeEnabled) {
        setTextColor(darkModeEnabled ? darkModeColor : normalModeColor);
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
