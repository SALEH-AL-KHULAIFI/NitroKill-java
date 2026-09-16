package com.isx3i.nitrokill.util;

import android.content.Context;
import android.content.res.Configuration;

import java.util.Locale;

/**
 * NitroKill's language switch is an in-app override (not tied to the phone's
 * system language), so every Context that needs it wraps itself with the
 * chosen Locale via attachBaseContext(). This is the only method needed:
 * with minSdk 26, Configuration.setLocale()/setLayoutDirection() always
 * apply directly — there's no pre-Android-7 fallback branch to carry around.
 */
public final class LocaleHelper {

    private LocaleHelper() {
    }

    public static Context wrap(Context context, String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);
        Configuration config = new Configuration(context.getResources().getConfiguration());
        config.setLocale(locale);
        config.setLayoutDirection(locale);
        return context.createConfigurationContext(config);
    }
}
