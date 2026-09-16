package com.isx3i.nitrokill.util;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Icon;

import java.util.Locale;

public final class IconTextGenerator {

    // مساحة أصغر حتى لا يصغر أندرويد النص
    private static final int SIZE = 64;

    // اللون السماوي
    private static final int INDICATOR_COLOR =
            Color.rgb(0, 210, 240);

    private IconTextGenerator() {
    }

    public static Icon forSpeed(long bytesPerSecond) {
        String[] label = shortLabel(bytesPerSecond);
        return render(label[0], label[1]);
    }

    private static String[] shortLabel(long bytesPerSecond) {

        double kbps = bytesPerSecond / 1024.0;

        if (kbps < 1.0) {
            return new String[]{
                    "0",
                    "KB/s"
            };

        } else if (kbps < 1000.0) {
            return new String[]{
                    String.format(Locale.US, "%.0f", kbps),
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

    private static Icon render(String value, String unit) {

        Bitmap bitmap = Bitmap.createBitmap(
                SIZE,
                SIZE,
                Bitmap.Config.ARGB_8888
        );

        Canvas canvas = new Canvas(bitmap);

        Paint paint = new Paint(
                Paint.ANTI_ALIAS_FLAG |
                Paint.SUBPIXEL_TEXT_FLAG
        );

        paint.setColor(INDICATOR_COLOR);
        paint.setTypeface(Typeface.create(
                Typeface.DEFAULT,
                Typeface.BOLD
        ));
        paint.setTextAlign(Paint.Align.CENTER);

        // تكبير الرقم داخل مساحة الأيقونة
        if (value.length() <= 1) {
            paint.setTextSize(42f);
        } else if (value.length() == 2) {
            paint.setTextSize(36f);
        } else if (value.length() == 3) {
            paint.setTextSize(29f);
        } else {
            paint.setTextSize(24f);
        }

        Paint.FontMetrics numberMetrics = paint.getFontMetrics();

        float numberY =
                22f -
                (numberMetrics.ascent + numberMetrics.descent) / 2f;

        canvas.drawText(
                value,
                SIZE / 2f,
                numberY,
                paint
        );

        // تكبير KB/s أو MB/s
        paint.setTextSize(17f);

        Paint.FontMetrics unitMetrics = paint.getFontMetrics();

        float unitY =
                48f -
                (unitMetrics.ascent + unitMetrics.descent) / 2f;

        canvas.drawText(
                unit,
                SIZE / 2f,
                unitY,
                paint
        );

        return Icon.createWithBitmap(bitmap);
    }
}
