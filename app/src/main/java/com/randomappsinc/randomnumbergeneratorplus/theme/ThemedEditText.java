package com.randomappsinc.randomnumbergeneratorplus.theme;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatEditText;
import androidx.core.content.ContextCompat;

import com.randomappsinc.randomnumbergeneratorplus.R;

public class ThemedEditText extends AppCompatEditText implements ThemeManager.Listener {

    private ThemeManager themeManager;
    private int normalModeTextColor;
    private int darkModeTextColor;
    private Drawable normalModeBackground;
    private Drawable darkModeBackground;
    private int normalModeHintTextColor;
    private int darkModeHintTextColor;

    public ThemedEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        themeManager = ThemeManager.get();
        normalModeTextColor = ContextCompat.getColor(context, R.color.dark_gray);
        darkModeTextColor = ContextCompat.getColor(context, R.color.white);
        normalModeBackground = ContextCompat.getDrawable(context, R.drawable.edittext_border_normal);
        darkModeBackground = ContextCompat.getDrawable(context, R.drawable.edittext_border_dark_mode);
        normalModeHintTextColor = ContextCompat.getColor(context, R.color.gray_300);
        darkModeHintTextColor = ContextCompat.getColor(context, R.color.half_white);

        setTextColor(themeManager.getDarkModeEnabled(context) ? darkModeTextColor : normalModeTextColor);
        setHintTextColor(themeManager.getDarkModeEnabled(context) ? darkModeHintTextColor : normalModeHintTextColor);
        setBackgroundDrawable(themeManager.getDarkModeEnabled(context) ? darkModeBackground : normalModeBackground);
    }

    @Override
    public void onThemeChanged(boolean darkModeEnabled) {
        setTextColor(darkModeEnabled ? darkModeTextColor : normalModeTextColor);
        setHintTextColor(darkModeEnabled ? darkModeHintTextColor : normalModeHintTextColor);
        setBackgroundDrawable(darkModeEnabled ? darkModeBackground : normalModeBackground);
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
