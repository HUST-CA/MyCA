package com.hustca.app.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by hamster on 16/7/21.
 * 
 * Resize and crops a bitmap at given path or InputStream to given size (fitting edges)
 */
public class ImageCropper extends AsyncTask<ImageCropper.BitmapSource, Void, Bitmap> {
    private int mTargetHeight;
    private int mTargetWidth;
    private OnFinishListener mOnFinishListener;
    private static final String TAG = "MyCA_ImageCropper";
    
    public ImageCropper(int targetWidth, int targetHeight, OnFinishListener listener) {
        mTargetHeight = targetHeight;
        mTargetWidth = targetWidth;
        mOnFinishListener = listener;
    }
    
    @Override
    protected Bitmap doInBackground(BitmapSource... params) {
        /* File successfully fetched from internet, cache available */
        //String originalFileName = getCacheFileName(params[0]);
        ByteArrayOutputStream originalOutStream = new ByteArrayOutputStream();
        boolean isUseStream = params[0].bitmapInputStream != null;

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        if (isUseStream) {
            /* Keeps a backup of the stream */
            byte[] buffer = new byte[1024];
            int len;
            
            try {
                while ((len = params[0].bitmapInputStream.read(buffer)) > -1)
                    originalOutStream.write(buffer, 0, len);
                originalOutStream.flush();
            } catch (IOException e) {
                Log.e(TAG, "doInBackground: can not read from InputStream", e);
                return null;
            }
            BitmapFactory.decodeStream(new ByteArrayInputStream(originalOutStream.toByteArray()),
                    null, options);
        } else if (params[0].bitmapPath != null) {
            BitmapFactory.decodeFile(params[0].bitmapPath, options);
        } else {
            Log.e(TAG, "doInBackground: none of sources available.");
            return null;
        }

        int imgWidth = options.outWidth;
        int imgHeight = options.outHeight;
        int inSampleSize = 1;

        if (imgWidth > mTargetWidth || imgHeight > mTargetHeight) {
            int heightRatio = Math.round((float) imgHeight / (float) mTargetHeight);
            int widthRatio = Math.round((float) imgWidth / (float) mTargetWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }

        options.inJustDecodeBounds = false;
        options.inSampleSize = inSampleSize;

        /* Sub-sampled */
        Bitmap roughBmp;
        if (isUseStream) {
            roughBmp = BitmapFactory.decodeStream(new ByteArrayInputStream(originalOutStream.toByteArray()),
                    null, options);
        } else {
            roughBmp = BitmapFactory.decodeFile(params[0].bitmapPath, options);
        }

        /* Make it exact */
        imgWidth = options.outWidth;
        imgHeight = options.outHeight;
        float exactHeightRatio = (float) mTargetHeight / imgHeight;
        float exactWidthRatio = (float) mTargetWidth / imgWidth;
        float exactRatio = Math.max(exactHeightRatio, exactWidthRatio);
        Matrix resizeMat = new Matrix();
        resizeMat.postScale(exactRatio, exactRatio);
        return Bitmap.createBitmap(roughBmp, 0, 0, (int)(mTargetWidth / exactRatio), (int)(mTargetHeight / exactRatio), resizeMat, false);
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);
        if (mOnFinishListener != null)
            mOnFinishListener.OnCropFinished(bitmap);
    }

    public interface OnFinishListener {
        void OnCropFinished(Bitmap bitmap);
    }

    /**
     * Priority: InputStream > Path
     * 
     * If InputStream is used, we will create a copy of it in memory.
     * If file path is used, we will just read from storage.
     */
    public static class BitmapSource {
        public String bitmapPath;
        public InputStream bitmapInputStream;
    }
}
