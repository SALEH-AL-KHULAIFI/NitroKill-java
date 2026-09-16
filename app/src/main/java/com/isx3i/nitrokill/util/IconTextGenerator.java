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
     * حجم المؤشر الأساسي
     */
    private static final int SIZE = 128;

    /*
     * التمديد الرأسي
     * 1.00 = طبيعي
     * 1.30 = أطول بنسبة 30%
     */
    private static final float VERTICAL_SCALE = 1.30f;

    /*
     * التنحيف الأفقي
     * 1.00 = العرض الطبيعي
     * 0.75 = أنحف بنسبة 25%
     */
    private static final float HORIZONTAL_SCALE = 0.75f;

    /*
     * لون المؤشر:
     * سماوي نيون
     */
    private static final int INDICATOR_COLOR =
            Color.rgb(0, 220, 255);

    private IconTextGenerator() {
    }

    public static Icon forSpeed(long bytesPerSecond) {
        String[] label = shortLabel(bytesPerSecond);
        return render(label[0], label[1]);
    }

    /**
     * تحويل السرعة إلى قيمة مختصرة.
     *
     * أمثلة:
     * 500 KB/s  -> 500 + KB/s
     * 1.5 MB/s  -> 1.5 + MB/s
     */
    private static String[] shortLabel(long bytesPerSecond) {

        double kbps = bytesPerSecond / 1024.0;

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

        Bitmap bitmap = Bitmap.createBitmap(
                SIZE,
                SIZE,
                Bitmap.Config.ARGB_8888
        );

        Canvas canvas = new Canvas(bitmap);

        /*
         * =========================
         * الرقم
         * =========================
         */
        Paint numberPaint =
                new Paint(Paint.ANTI_ALIAS_FLAG);

        numberPaint.setColor(INDICATOR_COLOR);

        numberPaint.setTypeface(
                Typeface.create(
                        Typeface.DEFAULT_BOLD,
                        Typeface.BOLD
                )
        );

        numberPaint.setTextAlign(
                Paint.Align.CENTER
        );

        /*
         * نحافظ على حجم الرقم كما هو.
         * لا نقوم بتصغير textSize.
         */
        if (value.length() <= 2) {

            numberPaint.setTextSize(
                    SIZE * 0.68f
            );

        } else if (value.length() == 3) {

            numberPaint.setTextSize(
                    SIZE * 0.57f
            );

        } else {

            numberPaint.setTextSize(
                    SIZE * 0.47f
            );
        }

        /*
         * =========================
         * الوحدة
         * =========================
         */
        Paint unitPaint =
                new Paint(Paint.ANTI_ALIAS_FLAG);

        unitPaint.setColor(INDICATOR_COLOR);

        unitPaint.setTypeface(
                Typeface.create(
                        Typeface.DEFAULT_BOLD,
                        Typeface.BOLD
                )
        );

        unitPaint.setTextAlign(
                Paint.Align.CENTER
        );

        /*
         * حجم الوحدة يبقى كبيرًا.
         */
        unitPaint.setTextSize(
                SIZE * 0.34f
        );

        /*
         * =========================
         * موضع الرقم
         * =========================
         */
        Paint.FontMetrics numberMetrics =
                numberPaint.getFontMetrics();

        float numberY =
                (SIZE * 0.45f)
                        - (
                        numberMetrics.ascent
                                + numberMetrics.descent
                ) / 2f;

        /*
         * =========================
         * موضع الوحدة
         * =========================
         */
        Paint.FontMetrics unitMetrics =
                unitPaint.getFontMetrics();

        float unitY =
                (SIZE * 0.84f)
                        - (
                        unitMetrics.ascent
                                + unitMetrics.descent
                ) / 2f;

        /*
         * =========================
         * رسم الرقم
         * =========================
         *
         * X = 0.75
         * يجعل الرقم أنحف أفقيًا.
         *
         * Y = 1.30
         * يجعل الرقم أطول رأسيًا.
         *
         * لذلك:
         * لا نصغر الحجم الأساسي،
         * وإنما نضغط العرض فقط.
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
         * =========================
         * رسم الوحدة
         * =========================
         *
         * نفس التنحيف والتمديد.
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

        return Icon.createWithBitmap(bitmap);
    }
}

ضع الملف مكان الملف الحالي:

"app/src/main/java/com/isx3i/nitrokill/util/IconTextGenerator.java"

الإعدادات المهمة حاليًا:

SIZE = 128
HORIZONTAL_SCALE = 0.75f
VERTICAL_SCALE = 1.30f

بهذا نحافظ على طول الأرقام، ونضغطها من الجوانب فقط. لونها أصبح سماوي نيون.
