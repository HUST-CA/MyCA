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
import com.hustca.app.util.ImageCacheUtil;
import com.hustca.app.util.ImageCropper;

import java.io.File;
import java.io.InputStream;


/**
 * Created by Hamster on 2015/8/7.
 * <p/>
 * Async image loader.
 * Can be used with ImageView.
 */
public class AsyncImageLoader {
    private static final String LOG_TAG = "MyCA_AsyncIMG";

    /**
     * Return a unique identifier for a url in cache
     * <p/>
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
     * See {@link com.hustca.app.util.networking.AsyncImageLoader#loadForImageView(ImageView, String, boolean)}
     * with shouldResize == true
     */
    public static void loadForImageView(final ImageView imageView, String source) {
        loadForImageView(imageView, source, true);
    }

    /**
     * Load picture for ImageView.<br/>
     * This will check if it's already cached.
     * If so, no loading pic will be shown and the cached image is shown immediately.
     * If not, it will show a loading pic and refresh when loading finished.
     *
     * Note: if shouldResize is true, the imageView should be measured since we need
     * its width and height. If shouldResize is false, there is no such requirement.
     *
     * @param imageView    view to operate on
     * @param source       URL
     * @param shouldResize whether we load a resized image so that fits the view
     *                     or the original image
     */
    public static void loadForImageView(final ImageView imageView, String source, boolean shouldResize) {
        if (null == imageView) {
            Log.e(LOG_TAG, "loadForImageView: imageView is null. Returning.");
            return;
        }
        if (null == source) {
            Log.d(LOG_TAG, "loadForImageView: source is null. Returning.");
            return;
        }

        AsyncImageLoaderPool.cancelAll(imageView);

        Context context = imageView.getContext();
        int viewWidth = imageView.getWidth();
        int viewHeight = imageView.getHeight();

        String originalCacheFileName = getUrlIdentifier(source);
        String exactCacheFileName = ImageCacheUtil.getThumbnailFileName(
                originalCacheFileName, viewWidth, viewHeight);

        File exactCacheFile = new File(ImageCacheUtil.getCacheDir(context),
                exactCacheFileName);
        File originalCacheFile = new File(ImageCacheUtil.getCacheDir(context),
                originalCacheFileName);

        if (exactCacheFile.exists()) {
            imageView.setImageURI(Uri.fromFile(exactCacheFile));
            return;
        } else if (originalCacheFile.exists()) {
            if (!shouldResize) {
                imageView.setImageURI(Uri.fromFile(originalCacheFile));
                return;
            }

            /* Original image exists, just need to resize it */
            ImageCropper.BitmapSource bitmapSource = new ImageCropper.BitmapSource();
            bitmapSource.bitmapPath = originalCacheFile.getAbsolutePath();

            ImageCropper.OnFinishListener listener = new ImageCropper.OnFinishListener() {
                @Override
                public void OnCropFinished(Bitmap bitmap) {
                    imageView.setImageBitmap(bitmap);
                }
            };
            CachedImageCropper cropper = new CachedImageCropper(
                    viewWidth,
                    viewHeight,
                    listener,
                    exactCacheFileName);
            cropper.execute(bitmapSource);
            return;
        }

        /* No cache hit, load from network */
        imageView.setImageBitmap(null);
        imageView.setBackgroundColor(imageView.getContext().
                getResources().getColor(android.R.color.darker_gray));
        RawImageLoader loader = new RawImageLoader(imageView, shouldResize);
        loader.execute(source);
        AsyncImageLoaderPool.add(loader);
    }

    /**
     * Loads image from network and start another AsyncTask to crop it if needed
     */
    public static class RawImageLoader extends CachedAsyncLoader {
        private ImageView mImageView;
        private String mCachePath;
        private boolean mShouldResize;

        /**
         * We will call another AsyncTask to crop it, and it will set
         * the final bitmap, so we need the ImageView here.
         *
         * @param iv           target ImageView
         * @param shouldResize shall we resize to fit ImageView after downloading
         */
        RawImageLoader(ImageView iv, boolean shouldResize) {
            mImageView = iv;
            mShouldResize = shouldResize;
        }

        private String getCacheDir() {
            return ImageCacheUtil.getCacheDir(mImageView.getContext()).getAbsolutePath();
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
            if (mShouldResize) {
                int width = mImageView.getWidth();
                int height = mImageView.getHeight();
                CachedImageCropper cropper = new CachedImageCropper(
                        width,
                        height,
                        listener,
                        ImageCacheUtil.getThumbnailFileName(mCachePath, width, height));
                cropper.execute(bitmapSource);
            }
            AsyncImageLoaderPool.remove(this);
        }

        public ImageView getImageView() { return mImageView; }
    }
}
