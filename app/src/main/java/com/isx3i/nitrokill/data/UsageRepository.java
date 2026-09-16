package com.isx3i.nitrokill.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.TrafficStats;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Rough daily data-usage counter for the on-screen table (date / mobile /
 * transfer / total). This is a lightweight approximation using TrafficStats
 * baselines reset at midnight — it is NOT a metered, carrier-accurate
 * counter, which is exactly why the app's disclaimer mentions the numbers
 * can be inaccurate if the app itself gets closed or the phone reboots
 * between readings (the baseline is only saved while NitroKill is running).
 */
public class UsageRepository {

    private static final String PREFS_NAME = "nitrokill_usage";
    private static final String KEY_BASELINE_DAY = "baseline_day";
    private static final String KEY_BASELINE_MOBILE = "baseline_mobile";
    private static final String KEY_BASELINE_TOTAL = "baseline_total";

    private final SharedPreferences prefs;
    private final SimpleDateFormat dayFormat = new SimpleDateFormat("d/M/yyyy", Locale.US);

    public UsageRepository(Context context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public static class DailyUsage {
        public final String date;
        public final long mobileBytes;
        public final long transferBytes;

        DailyUsage(String date, long mobileBytes, long transferBytes) {
            this.date = date;
            this.mobileBytes = mobileBytes;
            this.transferBytes = transferBytes;
        }

        public long getTotalBytes() {
            return mobileBytes + transferBytes;
        }
    }

    /** Call periodically (from the monitor service) to roll baselines at day change. */
    public void ensureBaselineForToday() {
        String today = dayFormat.format(new Date());
        if (!today.equals(prefs.getString(KEY_BASELINE_DAY, null))) {
            prefs.edit()
                    .putString(KEY_BASELINE_DAY, today)
                    .putLong(KEY_BASELINE_MOBILE, currentMobileBytes())
                    .putLong(KEY_BASELINE_TOTAL, currentTotalBytes())
                    .apply();
        }
    }

    public DailyUsage getTodayUsage() {
        ensureBaselineForToday();
        String today = prefs.getString(KEY_BASELINE_DAY, dayFormat.format(new Date()));
        long baseMobile = prefs.getLong(KEY_BASELINE_MOBILE, currentMobileBytes());
        long baseTotal = prefs.getLong(KEY_BASELINE_TOTAL, currentTotalBytes());

        long mobile = Math.max(currentMobileBytes() - baseMobile, 0L);
        long total = Math.max(currentTotalBytes() - baseTotal, 0L);
        long transfer = Math.max(total - mobile, 0L);

        return new DailyUsage(today, mobile, transfer);
    }

    private long currentMobileBytes() {
        long rx = TrafficStats.getMobileRxBytes();
        long tx = TrafficStats.getMobileTxBytes();
        return (rx == TrafficStats.UNSUPPORTED || tx == TrafficStats.UNSUPPORTED) ? 0L : rx + tx;
    }

    private long currentTotalBytes() {
        long rx = TrafficStats.getTotalRxBytes();
        long tx = TrafficStats.getTotalTxBytes();
        return (rx == TrafficStats.UNSUPPORTED || tx == TrafficStats.UNSUPPORTED) ? 0L : rx + tx;
    }

    /** Formats bytes the way the UI expects: whole megabytes, or GB above 1024MB. */
    public static String formatBytes(long bytes) {
        double mb = bytes / (1024.0 * 1024.0);
        if (mb >= 1024.0) {
            return String.format(Locale.US, "%.2f GB", mb / 1024.0);
        } else {
            return String.format(Locale.US, "%.0f MB", mb);
        }
    }
}
