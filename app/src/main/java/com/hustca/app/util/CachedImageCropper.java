package com.hustca.app.util;

import android.graphics.Bitmap;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by hamster on 16/7/21.
 *
 * Cache the result of ImageCropper
 */
public class CachedImageCropper extends ImageCropper {
    private String mCachePath;
    private static final String TAG = "MyCA_CachedCropper";

    public CachedImageCropper(int targetHeight, int targetWidth,
                              ImageCropper.OnFinishListener listener, String cachePath) {
        super(targetHeight, targetWidth, listener);
        mCachePath = cachePath;
    }

    @Override
    protected Bitmap doInBackground(BitmapSource... params) {
        Bitmap bmp = super.doInBackground(params);

        File thumb = new File(mCachePath);
        if (thumb.exists()) {
            if (!thumb.delete()) {
                Log.w(TAG, "doInBackground: unable to delete current thumb file" +
                        thumb.getAbsolutePath());
            }
        }

        try {
            FileOutputStream outStream = new FileOutputStream(thumb);
            bmp.compress(Bitmap.CompressFormat.WEBP, 100, outStream);
            outStream.flush();
            outStream.close();
        } catch (FileNotFoundException e) {
            Log.w(TAG, "doInBackground: unable to open file for writing " + thumb.getAbsolutePath());
        } catch (IOException e) {
            Log.w(TAG, "doInBackground: unable to write file " + thumb.getAbsolutePath());
        }

        return bmp;
    }
}
