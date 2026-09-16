package com.isx3i.nitrokill.util;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Icon;

import java.util.Locale;

public final class IconTextGenerator {

    /*
     * Larger canvas for a larger status-bar indicator.
     */
    private static final int SIZE = 128;

    private IconTextGenerator() {
    }

    public static Icon forSpeed(long bytesPerSecond) {
        String[] label = shortLabel(bytesPerSecond);
        return render(label[0], label[1]);
    }

    /**
     * Converts bytes/sec to a compact display value.
     *
     * Examples:
     * 500 KB/s  -> 500 + KB/s
     * 1.5 MB/s  -> 1.5 + MB/s
     */
    private static String[] shortLabel(long bytesPerSecond) {

        double kbps = bytesPerSecond / 1024.0;

        if (kbps < 1.0) {
            return new String[]{"0", "KB/s"};

        } else if (kbps < 1000.0) {
            return new String[]{
                    String.format(Locale.US, "%.0f", kbps),
                    "KB/s"
            };

        } else {
            return new String[]{
                    String.format(Locale.US, "%.1f", kbps / 1024.0),
                    "MB/s"
            };
        }
    }

    private static Icon render(String value, String unit) {

        Bitmap bitmap = Bitmap.createBitmap(
                SIZE,
                SIZE,
                Bitmap.Config.ARGB_8888
        );

        Canvas canvas = new Canvas(bitmap);

        /*
         * =========================
         * SPEED NUMBER
         * =========================
         */
        Paint numberPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        numberPaint.setColor(Color.WHITE);

        numberPaint.setTypeface(
                Typeface.create(
                        Typeface.DEFAULT_BOLD,
                        Typeface.BOLD
                )
        );

        numberPaint.setTextAlign(Paint.Align.CENTER);

        /*
         * Large speed number.
         */
        if (value.length() <= 2) {
            numberPaint.setTextSize(SIZE * 0.68f);
        } else if (value.length() == 3) {
            numberPaint.setTextSize(SIZE * 0.57f);
        } else {
            numberPaint.setTextSize(SIZE * 0.47f);
        }

        /*
         * =========================
         * SPEED UNIT
         * =========================
         */
        Paint unitPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        unitPaint.setColor(Color.WHITE);

        unitPaint.setTypeface(
                Typeface.create(
                        Typeface.DEFAULT_BOLD,
                        Typeface.BOLD
                )
        );

        unitPaint.setTextAlign(Paint.Align.CENTER);

        /*
         * Larger unit:
         *
         * KB/s
         * MB/s
         */
        unitPaint.setTextSize(SIZE * 0.34f);

        /*
         * =========================
         * NUMBER POSITION
         * =========================
         */
        Paint.FontMetrics numberMetrics =
                numberPaint.getFontMetrics();

        float numberY =
                (SIZE * 0.47f)
                        - (numberMetrics.ascent
                        + numberMetrics.descent) / 2f;

        /*
         * =========================
         * UNIT POSITION
         * =========================
         */
        Paint.FontMetrics unitMetrics =
                unitPaint.getFontMetrics();

        float unitY =
                (SIZE * 0.86f)
                        - (unitMetrics.ascent
                        + unitMetrics.descent) / 2f;

        /*
         * Draw speed number.
         */
        canvas.drawText(
                value,
                SIZE / 2f,
                numberY,
                numberPaint
        );

        /*
         * Draw KB/s or MB/s.
         */
        canvas.drawText(
                unit,
                SIZE / 2f,
                unitY,
                unitPaint
        );

        return Icon.createWithBitmap(bitmap);
    }
}
