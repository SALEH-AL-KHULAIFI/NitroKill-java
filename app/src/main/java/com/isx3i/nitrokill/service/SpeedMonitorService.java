package com.isx3i.nitrokill.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Icon;
import android.net.TrafficStats;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;

import com.isx3i.nitrokill.MainActivity;
import com.isx3i.nitrokill.NitroKillApp;
import com.isx3i.nitrokill.R;
import com.isx3i.nitrokill.data.UsageRepository;
import com.isx3i.nitrokill.util.IconTextGenerator;

public class SpeedMonitorService extends Service {

    private static final int NOTIFICATION_ID = 1001;
    private static final long TICK_MS = 1000L;

    public static void start(Context context) {
        context.startForegroundService(new Intent(context, SpeedMonitorService.class));
    }

    public static void stop(Context context) {
        context.stopService(new Intent(context, SpeedMonitorService.class));
    }

    private final Handler handler = new Handler(Looper.getMainLooper());
    private long lastBytes = 0L;
    private UsageRepository usageRepository;

    private final Runnable tick = new Runnable() {
        @Override
        public void run() {
            updateSpeed();
            usageRepository.ensureBaselineForToday();
            handler.postDelayed(this, TICK_MS);
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        usageRepository = new UsageRepository(this);
        lastBytes = totalBytesNow();
        startForeground(NOTIFICATION_ID, buildNotification(0L));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        handler.removeCallbacks(tick);
        handler.post(tick);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        handler.removeCallbacks(tick);
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private long totalBytesNow() {
        long rx = TrafficStats.getTotalRxBytes();
        long tx = TrafficStats.getTotalTxBytes();
        return (rx == TrafficStats.UNSUPPORTED || tx == TrafficStats.UNSUPPORTED) ? 0L : rx + tx;
    }

    private void updateSpeed() {
        long now = totalBytesNow();
        long bytesPerSecond = Math.max(now - lastBytes, 0L) * 1000L / TICK_MS;
        lastBytes = now;

        Notification notification = buildNotification(bytesPerSecond);
        getSystemService(NotificationManager.class).notify(NOTIFICATION_ID, notification);
    }

    private Notification buildNotification(long bytesPerSecond) {
        Intent openAppIntent = new Intent(this, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(
                this, 0, openAppIntent, PendingIntent.FLAG_IMMUTABLE);

        String speedText = UsageRepository.formatBytes(bytesPerSecond) + "/s";
        Icon icon = IconTextGenerator.forSpeed(bytesPerSecond);

        return new Notification.Builder(this, NitroKillApp.SPEED_CHANNEL_ID)
                .setSmallIcon(icon)
                .setContentTitle(getString(R.string.notification_title))
                .setContentText(speedText)
                .setOngoing(true)
                .setOnlyAlertOnce(true)
                .setContentIntent(contentIntent)
                .build();
        // No .setPriority()/.setSilent(): the notification channel is created
        // with IMPORTANCE_LOW, which is what actually controls sound/visibility
        // on API 26+ — per-notification priority is ignored once a channel exists.
    }
}
