package com.isx3i.nitrokill;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;

import com.isx3i.nitrokill.data.PrefsManager;
import com.isx3i.nitrokill.util.LocaleHelper;

public class NitroKillApp extends Application {

    public static final String SPEED_CHANNEL_ID = "nitrokill_speed_channel";

    @Override
    protected void attachBaseContext(Context base) {
        // Apply the user's saved language choice before any resources are resolved.
        String savedLang = PrefsManager.readLanguageBlocking(base);
        super.attachBaseContext(LocaleHelper.wrap(base, savedLang));
    }

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
    }

    private void createNotificationChannel() {
        NotificationManager manager = getSystemService(NotificationManager.class);
        NotificationChannel channel = new NotificationChannel(
                SPEED_CHANNEL_ID,
                getString(R.string.notification_channel_name),
                NotificationManager.IMPORTANCE_LOW // low = no sound, stays visible & silent
        );
        channel.setDescription(getString(R.string.notification_channel_desc));
        channel.setShowBadge(false);
        manager.createNotificationChannel(channel);
    }
}
