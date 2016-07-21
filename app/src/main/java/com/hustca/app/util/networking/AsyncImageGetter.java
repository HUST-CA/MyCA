package com.hustca.app.util.networking;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.hustca.app.R;
import com.hustca.app.util.CachedImageCropper;
import com.hustca.app.util.ImageCropper;

import java.io.File;
import java.io.InputStream;


/**
 * Created by Hamster on 2015/8/7.
 * <p>
 * Async image getter.
 * Can be used with ImageView.
 */
public class AsyncImageGetter {
    private static final String LOG_TAG = "MyCA_AsyncIMG";

    /**
     * Return a unique identifier for a url in cache
     * <p>
     * Normally it's the file name in the URL.
     * If no file name is available, return the hashCode of the URL.
     *
     * @param url URL to get identifier
     * @return cache identifier
     */
    private static String getUrlIdentifier(String url) {
        int pos = url.lastIndexOf("/");
        if (pos == -1 || pos == url.length() - 1) return String.valueOf(url.hashCode());
        else return url.substring(pos + 1);
    }

    /**
     * Load picture for ImageView.<br/>
     * This will check if it's already cached.
     * If so, no loading pic will be shown and the cached image is shown immediately.
     * If not, it will show a loading pic and refresh when loading finished.
     * <em>Remember to initialize with ImageView</em>
     *
     * @param source URL
     */
    public static void loadForImageView(final ImageView imageView, String source) {
        if (null == imageView) {
            Log.e(LOG_TAG, "loadForImageView: mImageView is null. Returning.");
            return;
        }
        if (null == source) {
            Log.d(LOG_TAG, "loadForImageView: source is null. Returning.");
            return;
        }

        String originalCacheFileName = getUrlIdentifier(source);
        String exactCacheFileName = originalCacheFileName +
                "_" + imageView.getWidth() + "x" + imageView.getHeight();
        File cacheFile = new File(imageView.getContext().getExternalCacheDir(),
                exactCacheFileName);
        if (cacheFile.exists()) {
            imageView.setImageURI(Uri.fromFile(cacheFile));
            return;
        } else {
            cacheFile = new File(imageView.getContext().getExternalCacheDir(),
                    originalCacheFileName);
            if (cacheFile.exists()) {
                ImageCropper.BitmapSource bitmapSource = new ImageCropper.BitmapSource();
                bitmapSource.bitmapPath = cacheFile.getAbsolutePath();

                ImageCropper.OnFinishListener listener = new ImageCropper.OnFinishListener() {
                    @Override
                    public void OnCropFinished(Bitmap bitmap) {
                        imageView.setImageBitmap(bitmap);
                    }
                };
                CachedImageCropper cropper = new CachedImageCropper(
                        imageView.getHeight(),
                        imageView.getWidth(),
                        listener,
                        cacheFile.getAbsolutePath() + "_" +
                                imageView.getWidth() + "x" + imageView.getHeight());
                cropper.execute(bitmapSource);
                return;
            }
        }

        /* No cache hit, load from network */
        imageView.setBackgroundColor(imageView.getContext().
                getResources().getColor(android.R.color.darker_gray));
        RawImageLoader loader = new RawImageLoader(imageView);
        loader.execute(source);
    }

    /**
     * Loads image from network and start another AsyncTask to crop it
     */
    private static class RawImageLoader extends CachedAsyncLoader {
        private ImageView mImageView;
        private String mCachePath;

        /**
         * We will call another AsyncTask to crop it, and it will set
         * the final bitmap, so we need the ImageView here.
         *
         * @param iv target ImageView
         */
        RawImageLoader(ImageView iv) {
            mImageView = iv;
        }

        private String getCacheDir() {
            return mImageView.getContext().getExternalCacheDir().getAbsolutePath();
        }

        @Override
        protected String getCacheFileName(String source) {
            mCachePath = getCacheDir() + File.separator + getUrlIdentifier(source);
            return mCachePath;
        }

        @Override
        protected void exceptionHandler(Exception e) {
            e.printStackTrace();
            final String desc = e.getLocalizedMessage();
            final Context context = mImageView.getContext();
            Handler handler = new Handler(context.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context,
                            context.getResources().getString(R.string.network_error)
                                    + " " + desc
                            , Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        protected void onPostExecute(InputStream inputStream) {
            super.onPostExecute(inputStream);
            ImageCropper.BitmapSource bitmapSource = new ImageCropper.BitmapSource();
            bitmapSource.bitmapInputStream = inputStream;

            ImageCropper.OnFinishListener listener = new ImageCropper.OnFinishListener() {
                @Override
                public void OnCropFinished(Bitmap bitmap) {
                    mImageView.setImageBitmap(bitmap);
                }
            };
            CachedImageCropper cropper = new CachedImageCropper(
                    mImageView.getHeight(),
                    mImageView.getWidth(),
                    listener,
                    mCachePath);
            cropper.execute(bitmapSource);
        }
    }
}
