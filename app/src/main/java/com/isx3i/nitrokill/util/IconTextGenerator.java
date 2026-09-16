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
     * حجم الأيقونة — تكبير مرتفع
     */
    private static final int SIZE = 180;

    /*
     * تمديد الرقم رأسيًا
     */
    private static final float VERTICAL_SCALE = 1.30f;

    /*
     * تنحيف النص أفقيًا
     */
    private static final float HORIZONTAL_SCALE = 0.72f;

    /*
     * اللون الأبيض
     */
    private static final int INDICATOR_COLOR =
            Color.WHITE;

    /*
     * هامش أمان داخل الأيقونة
     */
    private static final float SAFE_MARGIN =
            SIZE * 0.04f;

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
     *
     * أقل من 1000 KB/s:
     * 500 KB/s
     *
     * فوق 1000 KB/s:
     * 1.5 MB/s
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
         * تقسيم الأيقونة
         * =================================
         *
         * الرقم يأخذ الجزء الأكبر.
         * الوحدة بحجم متوسط في الأسفل.
         */
        float numberTop =
                SAFE_MARGIN;

        float numberBottom =
                SIZE * 0.67f;

        float unitTop =
                SIZE * 0.70f;

        float unitBottom =
                SIZE - SAFE_MARGIN;

        /*
         * =================================
         * حساب حجم الرقم تلقائيًا
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
         * حساب حجم الوحدة
         * =================================
         *
         * حجم متوسط، وليس بحجم الرقم.
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
         * رسم الوحدة KB/s أو MB/s
         * =================================
         */
        canvas.save();

        /*
         * الوحدة أنحف قليلًا،
         * لكن حجمها متوسط وواضح.
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

        /*
         * تحسين وضوح الحواف.
         */
        paint.setDither(true);

        return paint;
    }

    /*
     * =================================
     * حساب حجم النص بشكل متجاوب
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
         * الرقم يبدأ بحجم كبير جدًا.
         */
        float maxSize =
                isNumber
                        ? SIZE * 0.82f
                        : SIZE * 0.32f;

        /*
         * الحد الأدنى.
         */
        float minSize =
                isNumber
                        ? SIZE * 0.25f
                        : SIZE * 0.14f;

        /*
         * الارتفاع المتاح.
         */
        float availableHeight =
                bottom - top;

        /*
         * الارتفاع الفعلي قبل التمديد.
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
         * تصغير تدريجي حتى يدخل
         * النص بالكامل داخل الأيقونة.
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

            size -= 1f;
        }

        if (size < minSize) {
            size = minSize;
        }

        return size;
    }
}
