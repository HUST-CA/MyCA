package com.hustca.app.util.networking;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.hustca.app.R;

import java.io.File;
import java.io.InputStream;


/**
 * Created by Hamster on 2015/8/7.
 * <p/>
 * Async image getter.
 * Can be used with ImageView.
 */
public class AsyncImageGetter {
    private static final String LOG_TAG = "MyCA_AsyncIMG";

    /**
     * Return a unique identifier for a url in cache
     * <p/>
     * Normally it's the file name in the URL.
     * If no file name is available, return the hashCode of the URL.
     *
     * @param url          URL to get identifier
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
    public static void loadForImageView(ImageView imageView, String source) {
        if (null == imageView) {
            Log.e(LOG_TAG, "loadForImageView: mImageView is null. Returning.");
            return;
        }
        if (null == source) {
            Log.d(LOG_TAG, "loadForImageView: source is null. Returning.");
            return;
        }

        File cacheFile = new File(imageView.getContext().getExternalCacheDir(),
                getUrlIdentifier(source));
        if (cacheFile.exists()) {
            imageView.setImageURI(Uri.fromFile(cacheFile));
            return;
        }

        imageView.setBackgroundColor(imageView.getContext().
                getResources().getColor(android.R.color.darker_gray));
        AsyncLoaderForImageView loader = new AsyncLoaderForImageView(imageView);
        loader.execute(source);
    }

    private static class AsyncLoaderForImageView extends CachedAsyncLoader {
        private ImageView mImageView;
        private Context mContext;

        AsyncLoaderForImageView(ImageView iv) {
            mImageView = iv;
            mContext = iv.getContext();
        }

        @Override
        protected void onPostExecute(InputStream in) {
            if (in == null) {
                // TODO Make an error pic
                mImageView.setImageResource(R.mipmap.ic_launcher);
            } else {
                if (mImageView != null) {
                    mImageView.setImageDrawable(Drawable.createFromStream(in, null));
                }
            }
        }

        @Override
        protected String getCacheFileName(String source) {
            return mContext.getExternalCacheDir().getAbsolutePath()
                    + File.separator + getUrlIdentifier(source);
        }

        @Override
        protected void exceptionHandler(final Exception e) {
            e.printStackTrace();
            Handler handler = new Handler(mContext.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(mContext,
                            mContext.getResources().getString(R.string.network_error)
                                    + " " + e.getLocalizedMessage()
                            , Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
