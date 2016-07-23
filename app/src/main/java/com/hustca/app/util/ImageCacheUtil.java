package com.hustca.app.util;

import android.content.Context;

import java.io.File;
import java.util.Locale;

/**
 * Created by hamster on 16/7/23.
 *
 * Helper class for caching images
 */
public class ImageCacheUtil {
    public static File getCacheDir(Context context) {
        return context.getExternalCacheDir();
    }

    public static String getThumbnailFileName(String baseName, int width, int height) {
        return String.format(Locale.getDefault(), "%s_%dx%d", baseName, width, height);
    }
}
