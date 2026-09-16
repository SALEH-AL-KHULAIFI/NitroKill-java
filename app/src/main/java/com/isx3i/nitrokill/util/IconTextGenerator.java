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
     * حجم الأيقونة الأساسي.
     */
    private static final int SIZE = 140;

    /*
     * التمديد الرأسي.
     */
    private static final float VERTICAL_SCALE = 1.30f;

    /*
     * التنحيف الأفقي.
     */
    private static final float HORIZONTAL_SCALE = 0.75f;

    /*
     * اللون البرتقالي.
     */
    private static final int INDICATOR_COLOR =
            Color.rgb(255, 145, 0);

    /*
     * هامش أمان داخل الأيقونة.
     */
    private static final float SAFE_MARGIN =
            SIZE * 0.05f;

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
         * إعداد الرقم
         * =================================
         */
        Paint numberPaint =
                createPaint();

        /*
         * =================================
         * إعداد الوحدة
         * =================================
         */
        Paint unitPaint =
                createPaint();

        /*
         * =================================
         * المساحة المتاحة
         * =================================
         *
         * نقسم الأيقونة إلى منطقتين:
         *
         * الرقم:
         * الجزء العلوي.
         *
         * الوحدة:
         * الجزء السفلي.
         */
        float numberTop =
                SAFE_MARGIN;

        float numberBottom =
                SIZE * 0.64f;

        float unitTop =
                SIZE * 0.67f;

        float unitBottom =
                SIZE - SAFE_MARGIN;

        /*
         * =================================
         * حساب حجم الرقم تلقائيًا
         * =================================
         *
         * الحجم يتغير حسب:
         *
         * 1. طول الرقم.
         * 2. عرض الأيقونة.
         * 3. الارتفاع المتاح.
         * 4. التمديد الرأسي.
         * 5. التنحيف الأفقي.
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
         * حساب حجم الوحدة تلقائيًا
         * =================================
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

        canvas.scale(
                HORIZONTAL_SCALE,
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
     * إنشاء Paint موحد.
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

        return paint;
    }

    /*
     * =====================================
     * حساب حجم النص بشكل متجاوب
     * =====================================
     *
     * نبدأ بحجم كبير ثم نصغره تدريجيًا
     * حتى يدخل النص بالكامل داخل المساحة.
     */
    private static float calculateTextSize(
            String text,
            Paint paint,
            float top,
            float bottom,
            boolean isNumber
    ) {

        /*
         * الحجم الابتدائي.
         */
        float maxSize =
                isNumber
                        ? SIZE * 0.75f
                        : SIZE * 0.40f;

        /*
         * الحد الأدنى.
         */
        float minSize =
                isNumber
                        ? SIZE * 0.25f
                        : SIZE * 0.18f;

        /*
         * المساحة الرأسية المتاحة
         * قبل التمديد.
         */
        float availableHeight =
                bottom - top;

        /*
         * لأن النص سيتم تمديده رأسيًا،
         * نحتاج إلى أخذ VERTICAL_SCALE
         * في الحسبان.
         */
        float maximumHeight =
                availableHeight
                        / VERTICAL_SCALE;

        /*
         * الحد الأقصى للعرض.
         *
         * التنحيف الأفقي 0.75 يجعل
         * النص النهائي أضيق.
         */
        float maximumWidth =
                (SIZE - SAFE_MARGIN * 2f)
                        / HORIZONTAL_SCALE;

        float size =
                maxSize;

        /*
         * تقليل الحجم حتى يدخل
         * النص داخل الحدود.
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

        /*
         * حماية إضافية.
         */
        if (size < minSize) {
            size = minSize;
        }

        return size;
    }
}
