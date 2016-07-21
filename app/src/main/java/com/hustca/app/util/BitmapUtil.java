package com.hustca.app.util;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * Created by hamster on 16/7/19.
 *
 * Utility for cropping images
 */
public class BitmapUtil {
    public static ByteArrayInputStream bitmapToInputStream(Bitmap bitmap) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.WEBP, 100, out);
        return new ByteArrayInputStream(out.toByteArray());
    }
}
