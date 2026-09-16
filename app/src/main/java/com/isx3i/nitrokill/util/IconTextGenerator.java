package com.isx3i.nitrokill.util;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Icon;

import java.util.Locale;

/**
 * Status-bar notification icons are tiny (roughly 24dp) and Android forces
 * them to render as a plain white silhouette — a static "speed" glyph would
 * look identical whether you're at 0 or 50 Mbps, which was the root cause of
 * the "icon too small / not clear" complaint. The standard fix (used by every
 * speed-meter app on the Play Store) is to draw the number itself as a bitmap
 * and hand that bitmap to the notification as its small icon, so what the
 * user sees next to the clock/battery IS the number.
 *
 * Uses the framework's android.graphics.drawable.Icon (available since API 23)
 * instead of androidx.core.graphics.drawable.IconCompat — minSdk 26 already
 * covers it natively, so no AndroidX dependency is needed just for this.
 */
public final class IconTextGenerator {

    private static final int SIZE = 96; // px, matches the largest density notification icons render at

    private IconTextGenerator() {
    }

    public static Icon forSpeed(long bytesPerSecond) {
        String[] label = shortLabel(bytesPerSecond);
        return render(label[0], label[1]);
    }

    /** Splits e.g. "12.4 MB/s" into a big number line and a small unit line so both stay legible. */
    private static String[] shortLabel(long bytesPerSecond) {
        double kbps = bytesPerSecond / 1024.0;
        if (kbps < 1.0) {
            return new String[]{"0", "K"};
        } else if (kbps < 1000.0) {
            return new String[]{String.format(Locale.US, "%.0f", kbps), "K"};
        } else {
            return new String[]{String.format(Locale.US, "%.1f", kbps / 1024.0), "M"};
        }
    }

    private static Icon render(String value, String unit) {
        Bitmap bitmap = Bitmap.createBitmap(SIZE, SIZE, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        Paint numberPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        numberPaint.setColor(Color.WHITE);
        numberPaint.setTypeface(Typeface.create(Typeface.DEFAULT_BOLD, Typeface.BOLD));
        numberPaint.setTextAlign(Paint.Align.CENTER);
        numberPaint.setTextSize(value.length() > 2 ? SIZE * 0.42f : SIZE * 0.52f);

        Paint unitPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        unitPaint.setColor(Color.WHITE);
        unitPaint.setTypeface(Typeface.create(Typeface.DEFAULT_BOLD, Typeface.BOLD));
        unitPaint.setTextAlign(Paint.Align.CENTER);
        unitPaint.setTextSize(SIZE * 0.24f);

        float numberY = SIZE * 0.52f;
        float unitY = SIZE * 0.86f;
        canvas.drawText(value, SIZE / 2f, numberY, numberPaint);
        canvas.drawText(unit, SIZE / 2f, unitY, unitPaint);

        return Icon.createWithBitmap(bitmap);
    }
}
