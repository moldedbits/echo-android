package com.moldedbits.echo.chat.utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.TypedValue;

import moldedbits.com.echo.R;

/**
 * This class used for changing color of elements of app in runtime,according to the AppTheme
 * color Available in styles.
 */

public class ColorUtils {

    public static int fetchAccentColor(final Context context) {
        TypedValue typedValue = new TypedValue();

        TypedArray a = context
                .obtainStyledAttributes(typedValue.data, new int[]{R.attr.colorAccent});
        int color = a.getColor(0, 0);

        a.recycle();

        return color;
    }

    public static int fetchPrimaryColor(final Context context) {
        TypedValue typedValue = new TypedValue();

        TypedArray a = context
                .obtainStyledAttributes(typedValue.data, new int[]{R.attr.colorPrimary});
        int color = a.getColor(0, 0);

        a.recycle();

        return color;
    }
}
