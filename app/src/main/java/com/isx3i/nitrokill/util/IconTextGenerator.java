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
     * مساحة رسم كبيرة جدًا.
     *
     * Android قد يصغر الأيقونة عند عرضها
     * في شريط الحالة، لكن هذه القيمة تعطي
     * النص أكبر مساحة ممكنة للرسم.
     */
    private static final int SIZE = 512;

    /*
     * تمديد رأسي قوي.
     */
    private static final float VERTICAL_SCALE = 1.30f;

    /*
     * تنحيف أفقي حتى لا تصبح الأرقام عريضة.
     */
    private static final float HORIZONTAL_SCALE = 0.70f;

    /*
     * اللون الأبيض.
     */
    private static final int INDICATOR_COLOR =
            Color.WHITE;

    /*
     * هامش أمان صغير جدًا.
     */
    private static final float SAFE_MARGIN =
            SIZE * 0.025f;

    private IconTextGenerator() {
    }

    public static Icon forSpeed(long bytesPerSecond) {

        String[] label =
                shortLabel(bytesPerSecond);

        return render(
                label[0],
                label[1]
        );
    }

    /*
     * تحويل السرعة إلى قيمة مختصرة.
     */
    private static String[] shortLabel(
            long bytesPerSecond
    ) {

        double kbps =
                bytesPerSecond / 1024.0;

        if (kbps < 1.0) {

            return new String[]{
                    "0",
                    "KB/s"
            };

        } else if (kbps < 1000.0) {

            return new String[]{
                    String.format(
                            Locale.US,
                            "%.0f",
                            kbps
                    ),
                    "KB/s"
            };

        } else {

            return new String[]{
                    String.format(
                            Locale.US,
                            "%.1f",
                            kbps / 1024.0
                    ),
                    "MB/s"
            };
        }
    }

    private static Icon render(
            String value,
            String unit
    ) {

        Bitmap bitmap =
                Bitmap.createBitmap(
                        SIZE,
                        SIZE,
                        Bitmap.Config.ARGB_8888
                );

        Canvas canvas =
                new Canvas(bitmap);

        /*
         * =================================
         * Paint الرقم
         * =================================
         */
        Paint numberPaint =
                createPaint();

        /*
         * =================================
         * Paint الوحدة
         * =================================
         */
        Paint unitPaint =
                createPaint();

        /*
         * =================================
         * مناطق الرسم
         * =================================
         */

        float numberTop =
                SAFE_MARGIN;

        float numberBottom =
                SIZE * 0.68f;

        float unitTop =
                SIZE * 0.72f;

        float unitBottom =
                SIZE - SAFE_MARGIN;

        /*
         * =================================
         * حجم الرقم المتجاوب
         * =================================
         */
        float numberSize =
                calculateTextSize(
                        value,
                        numberPaint,
                        numberTop,
                        numberBottom,
                        true
                );

        numberPaint.setTextSize(
                numberSize
        );

        /*
         * =================================
         * حجم الوحدة المتجاوب
         * =================================
         *
         * الوحدة أصغر من الرقم،
         * لكنها واضحة ومتوسطة الحجم.
         */
        float unitSize =
                calculateTextSize(
                        unit,
                        unitPaint,
                        unitTop,
                        unitBottom,
                        false
                );

        unitPaint.setTextSize(
                unitSize
        );

        /*
         * =================================
         * موضع الرقم
         * =================================
         */
        Paint.FontMetrics numberMetrics =
                numberPaint.getFontMetrics();

        float numberCenter =
                (numberTop + numberBottom) / 2f;

        float numberY =
                numberCenter
                        - (
                        numberMetrics.ascent
                                + numberMetrics.descent
                ) / 2f;

        /*
         * =================================
         * موضع الوحدة
         * =================================
         */
        Paint.FontMetrics unitMetrics =
                unitPaint.getFontMetrics();

        float unitCenter =
                (unitTop + unitBottom) / 2f;

        float unitY =
                unitCenter
                        - (
                        unitMetrics.ascent
                                + unitMetrics.descent
                ) / 2f;

        /*
         * =================================
         * رسم الرقم
         * =================================
         */
        canvas.save();

        canvas.scale(
                HORIZONTAL_SCALE,
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
         * =================================
         * رسم الوحدة
         * =================================
         */
        canvas.save();

        /*
         * الوحدة أعرض قليلًا من الرقم
         * حتى تكون KB/s و MB/s واضحة.
         */
        canvas.scale(
                0.80f,
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

        return Icon.createWithBitmap(
                bitmap
        );
    }

    /*
     * =================================
     * إنشاء Paint
     * =================================
     */
    private static Paint createPaint() {

        Paint paint =
                new Paint(
                        Paint.ANTI_ALIAS_FLAG
                                | Paint.SUBPIXEL_TEXT_FLAG
        );

        paint.setColor(
                INDICATOR_COLOR
        );

        paint.setTypeface(
                Typeface.create(
                        Typeface.DEFAULT,
                        Typeface.BOLD
                )
        );

        paint.setTextAlign(
                Paint.Align.CENTER
        );

        paint.setLinearText(true);

        paint.setDither(true);

        return paint;
    }

    /*
     * =================================
     * حساب الحجم المتجاوب
     * =================================
     */
    private static float calculateTextSize(
            String text,
            Paint paint,
            float top,
            float bottom,
            boolean isNumber
    ) {

        /*
         * الرقم يبدأ بحجم ضخم.
         */
        float maxSize =
                isNumber
                        ? SIZE * 0.90f
                        : SIZE * 0.34f;

        /*
         * الحد الأدنى.
         */
        float minSize =
                isNumber
                        ? SIZE * 0.20f
                        : SIZE * 0.12f;

        /*
         * الارتفاع المتاح.
         */
        float availableHeight =
                bottom - top;

        /*
         * حساب الارتفاع قبل التمديد.
         */
        float maximumHeight =
                availableHeight
                        / VERTICAL_SCALE;

        /*
         * العرض المتاح.
         */
        float maximumWidth =
                (SIZE - SAFE_MARGIN * 2f)
                        / HORIZONTAL_SCALE;

        float size =
                maxSize;

        /*
         * تصغير الحجم تلقائيًا حتى
         * يدخل النص بالكامل.
         */
        while (size > minSize) {

            paint.setTextSize(size);

            Paint.FontMetrics metrics =
                    paint.getFontMetrics();

            float textHeight =
                    metrics.descent
                            - metrics.ascent;

            float textWidth =
                    paint.measureText(text);

            boolean heightFits =
                    textHeight
                            <= maximumHeight;

            boolean widthFits =
                    textWidth
                            <= maximumWidth;

            if (heightFits && widthFits) {
                break;
            }

            size -= 2f;
        }

        if (size < minSize) {
            size = minSize;
        }

        return size;
    }
}
