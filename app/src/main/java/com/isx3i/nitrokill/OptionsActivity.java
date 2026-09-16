package com.isx3i.nitrokill;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;

import com.isx3i.nitrokill.data.PrefsManager;
import com.isx3i.nitrokill.util.LocaleHelper;

public class OptionsActivity extends Activity {

    private PrefsManager prefs;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.wrap(newBase, PrefsManager.readLanguageBlocking(newBase)));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

        prefs = new PrefsManager(this);

        findViewById(R.id.btn_back).setOnClickListener(v -> finish());
        findViewById(R.id.btn_lang_ar).setOnClickListener(v -> applyLanguageAndRestart("ar"));
        findViewById(R.id.btn_lang_en).setOnClickListener(v -> applyLanguageAndRestart("en"));
        findViewById(R.id.btn_notification_perm).setOnClickListener(v -> openNotificationSettings());
    }

    private void applyLanguageAndRestart(String lang) {
        prefs.setLanguage(lang);
        // Clear the whole task and start fresh so every screen — including
        // this one — is rebuilt with attachBaseContext() picking up the new
        // language, instead of leaving a stale-locale Activity behind.
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void openNotificationSettings() {
        Intent intent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
        intent.putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
        startActivity(intent);
    }
}
