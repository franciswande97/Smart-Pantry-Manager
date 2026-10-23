package com.example.smartpantry;

import android.content.Context;
import android.content.SharedPreferences;

/** One place that reads and writes the app's saved settings (SharedPreferences). */
public final class AppPreferences {

    private static final String FILE_NAME = "smart_pantry_prefs";
    private static final String KEY_EXPIRY_HIGHLIGHT = "expiry_highlight";

    private AppPreferences() { }

    private static SharedPreferences prefs(Context context) {
        return context.getApplicationContext().getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
    }

    /** Defaults to ON the first time the app runs. */
    public static boolean isExpiryHighlightEnabled(Context context) {
        return prefs(context).getBoolean(KEY_EXPIRY_HIGHLIGHT, true);
    }

    public static void setExpiryHighlightEnabled(Context context, boolean enabled) {
        prefs(context).edit().putBoolean(KEY_EXPIRY_HIGHLIGHT, enabled).apply();
    }
}