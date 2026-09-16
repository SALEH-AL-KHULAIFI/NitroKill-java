package com.isx3i.nitrokill.data;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Small synchronous wrapper around SharedPreferences for the two settings
 * NitroKill needs: a language code and one on/off flag. Plain
 * SharedPreferences (not DataStore) — nothing here justifies pulling in
 * DataStore/protobuf/coroutines just to persist two values.
 */
public class PrefsManager {

    private static final String PREFS_NAME = "nitrokill_prefs";
    private static final String KEY_LANGUAGE = "language";
    private static final String KEY_MONITOR_ENABLED = "monitor_enabled";

    private final SharedPreferences prefs;

    public PrefsManager(Context context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    /** Reads the saved language synchronously — used from attachBaseContext, before any UI exists. */
    public static String readLanguageBlocking(Context context) {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .getString(KEY_LANGUAGE, "ar");
    }

    public void setLanguage(String lang) {
        prefs.edit().putString(KEY_LANGUAGE, lang).apply();
    }

    /** Monitoring defaults to on, matching the app's original intended behaviour. */
    public boolean isMonitorEnabled() {
        return prefs.getBoolean(KEY_MONITOR_ENABLED, true);
    }

    public void setMonitorEnabled(boolean enabled) {
        prefs.edit().putBoolean(KEY_MONITOR_ENABLED, enabled).apply();
    }
}
