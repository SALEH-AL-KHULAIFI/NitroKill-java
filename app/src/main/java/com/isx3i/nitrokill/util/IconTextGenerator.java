package com.isx3i.nitrokill.util;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Icon;

import java.util.Locale;

/**
 * Generates the dynamic internet-speed notification icon.
 *
 * The speed value is rendered directly into the notification icon
 * so the current speed is visible next to the status-bar icons.
 */
public final class IconTextGenerator {

    /*
     * Larger canvas gives Android more pixels to work with when
     * rendering the notification icon.
     */
    private static final int SIZE = 128;

    private IconTextGenerator() {
    }

    public static Icon forSpeed(long bytesPerSecond) {
        String[] label = shortLabel(bytesPerSecond);
        return render(label[0], label[1]);
    }

    /**
     * Converts the speed into a compact number + unit.
     *
     * Examples:
     * 500 KB/s -> "500" + "K"
     * 1.5 MB/s -> "1.5" + "M"
     */
    private static String[] shortLabel(long bytesPerSecond) {

        double kbps = bytesPerSecond / 1024.0;

        if (kbps < 1.0) {
            return new String[]{"0", "K"};

        } else if (kbps < 1000.0) {
            return new String[]{
                    String.format(Locale.US, "%.0f", kbps),
                    "K"
            };

        } else {
            return new String[]{
                    String.format(Locale.US, "%.1f", kbps / 1024.0),
                    "M"
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
         * Larger number.
         *
         * Short values such as 0, 25, 120 become very large.
         * Longer values receive slightly smaller text so they
         * still fit horizontally.
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
         * Unit is also larger than before.
         */
        unitPaint.setTextSize(SIZE * 0.27f);

        /*
         * =========================
         * VERTICAL POSITION
         * =========================
         *
         * Move the number slightly upward and make it occupy
         * more of the vertical space.
         */
        Paint.FontMetrics numberMetrics = numberPaint.getFontMetrics();

        float numberY =
                (SIZE * 0.54f)
                        - (numberMetrics.ascent + numberMetrics.descent) / 2f;

        /*
         * Unit remains below the number with enough separation
         * to prevent overlap.
         */
        Paint.FontMetrics unitMetrics = unitPaint.getFontMetrics();

        float unitY =
                (SIZE * 0.89f)
                        - (unitMetrics.ascent + unitMetrics.descent) / 2f;

        /*
         * Draw the speed number.
         */
        canvas.drawText(
                value,
                SIZE / 2f,
                numberY,
                numberPaint
        );

        /*
         * Draw K or M.
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
