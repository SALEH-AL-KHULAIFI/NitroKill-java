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
     * Larger canvas for the status-bar indicator.
     */
    private static final int SIZE = 128;

    /*
     * Vertical stretch factor.
     *
     * 1.00f = normal height
     * 1.20f = 20% taller
     * 1.30f = 30% taller
     */
    private static final float VERTICAL_SCALE = 1.30f;

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
     * 500 KB/s -> 500 + KB/s
     * 1.5 MB/s -> 1.5 + MB/s
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
         * Keep the horizontal size controlled.
         * The vertical scale below will make the numbers taller
         * without making them wider.
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
         * UNIT
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
         * Large unit.
         */
        unitPaint.setTextSize(SIZE * 0.34f);

        /*
         * =========================
         * VERTICAL POSITIONS
         * =========================
         */
        Paint.FontMetrics numberMetrics =
                numberPaint.getFontMetrics();

        float numberY =
                (SIZE * 0.45f)
                        - (numberMetrics.ascent
                        + numberMetrics.descent) / 2f;

        Paint.FontMetrics unitMetrics =
                unitPaint.getFontMetrics();

        float unitY =
                (SIZE * 0.84f)
                        - (unitMetrics.ascent
                        + unitMetrics.descent) / 2f;

        /*
         * =========================
         * DRAW NUMBER
         * =========================
         *
         * X scale = 1.00 -> no horizontal stretching.
         * Y scale = 1.30 -> 30% vertical stretching.
         */
        canvas.save();

        canvas.scale(
                1.0f,
                VERTICAL_SCALE,
                SIZE / 2f,
                numberY
        );

        canvas.drawText(
                value,
                SIZE / 2f,
                numberY,
                numberPaint
        );

        canvas.restore();

        /*
         * =========================
         * DRAW UNIT
         * =========================
         *
         * The same 1.30× vertical stretch is applied
         * to KB/s and MB/s.
         */
        canvas.save();

        canvas.scale(
                1.0f,
                VERTICAL_SCALE,
                SIZE / 2f,
                unitY
        );

        canvas.drawText(
                unit,
                SIZE / 2f,
                unitY,
                unitPaint
        );

        canvas.restore();

        return Icon.createWithBitmap(bitmap);
    }
}
