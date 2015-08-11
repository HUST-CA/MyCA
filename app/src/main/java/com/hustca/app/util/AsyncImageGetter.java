package com.hustca.app.util;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.text.Html;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.hustca.app.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


/**
 * Created by Hamster on 2015/8/7.
 * <p/>
 * Async image getter. Used in TextView.setText(Html.fromHtml(...,imageGetter,...));
 * Can also be used with ImageView. But you can not refresh them both.
 */
public class AsyncImageGetter implements Html.ImageGetter {
    private static final String LOG_TAG = "MyCA_AsyncIMG";
    private static final int CONNECTING_TIME_OUT_MILLIS = 5000;
    private static final int BUFFER_SIZE_BYTES = 1024;
    /**
     * Associated TextView. Used to requestLayout() after finishing.
     */
    private TextView mTextView;
    /**
     * Support ImageView as well.
     */
    private ImageView mImageView;

    /**
     * Set the associated controls. When drawable is ready, these controls will be refreshed.
     *
     * @param tv TextView containing HTML
     */
    public AsyncImageGetter(TextView tv) {
        mTextView = tv;
        mImageView = null;
    }

    public AsyncImageGetter(ImageView iv) {
        mImageView = iv;
        mTextView = null;
    }

    /**
     * Return the file name in a url
     * <p/>
     * e.g. http://www.aaa.com/index.html -> index.html
     * https://www.bbb.com/a -> a
     *
     * @param url          URL to extract file name from
     * @param default_name the name to return if no file name exists (http://www.a.com/)
     * @return file name
     */
    private static String getFilename(String url, String default_name) {
        int pos = url.lastIndexOf("/");
        if (pos == -1 || pos == url.length() - 1) return default_name;
        else return url.substring(pos + 1);
    }

    /**
     * Only used in Html.fromHTML(...,imgGetter,...)<br/>
     * If already cached, return the cache directly. Otherwise start a new thread to load it.
     * <em>NOTE: If the picture is cached, we return a Drawable. But when loading is required,
     * what we return is a {@link BitmapDrawableContainer}.</em><br/>
     * If you want to load images for ImageView, see {@link AsyncImageGetter#loadForImageView(String)}
     */
    @Override
    @SuppressWarnings("deprecation")
    public Drawable getDrawable(String source) {
        if (mTextView == null) {
            Log.e(LOG_TAG, "getDrawable: mTextView is null. You must initialize with the associated TextView before HTML things");
            return null;
        }

        File cacheFile = new File(mTextView.getContext().getExternalCacheDir(),
                getFilename(source, "default"));
        if (cacheFile.exists()) {
            return Drawable.createFromPath(cacheFile.getAbsolutePath());
        }

        BitmapDrawableContainer consistentDrawable = new BitmapDrawableContainer();
        // TODO Make a loading pic here
        consistentDrawable.mDrawable = new BitmapDrawable(mTextView.getResources(),
                ((BitmapDrawable) mTextView.getResources().getDrawable(R.mipmap.ic_launcher)).getBitmap());

        /* Only used in TextView so ImageView is null here */
        AsyncLoaderForTextView loader = new AsyncLoaderForTextView(mTextView, consistentDrawable);
        loader.execute(source);
        return consistentDrawable;
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
    public void loadForImageView(String source) {
        if (mImageView == null) {
            Log.e(LOG_TAG, "loadForImageView: mImageView is null. Returning.");
            return;
        }

        File cacheFile = new File(mImageView.getContext().getExternalCacheDir(),
                getFilename(source, "default"));
        if (cacheFile.exists()) {
            mImageView.setImageURI(Uri.fromFile(cacheFile));
            return;
        }

        // TODO Make a loading pic here
        mImageView.setImageResource(R.mipmap.ic_launcher);
        AsyncLoaderForImageView loader = new AsyncLoaderForImageView(mImageView);
        loader.execute(source);
    }

    /**
     * This class will download the specific picture to local storage.<p/>
     * This is only a network operator. The logic for refreshing controls should be
     * implemented in subclasses' onPostExecute().<p/>
     * NOTE: this class will NOT respect existing cached file and will overwrite it.
     */
    private abstract class AsyncLoaderBase extends AsyncTask<String, Void, Drawable> {
        /**
         * Return a cache file path for a URL.<p/>
         * e.g. <em>http://www.a.com/a.jpg</em> returns
         * <em>/sdcard/Android/data/com.this.app/files/a.jpg</em><br/>
         * This file can be non-existent and this class will create it.
         * <p/>
         * Since this is a network-only bare-bone class, it can not read external cache dir
         * by Context. Plz implement this using your mContext in your subclass.
         *
         * @param source URL to load. You can just take the file name.
         * @return The cache file path corresponding to the URL.
         */
        protected abstract String getCacheFilename(String source);

        /**
         * Fetch picture from URL and store to local storage.
         *
         * @param params [0] for URL
         * @return drawable fetched and cached. If any error happens, null.
         */
        @Override
        protected Drawable doInBackground(String... params) {
            if (params.length == 0) {
                Log.e(LOG_TAG, "doInBackground: params length = 0");
                return null;
            }

            String url = params[0];
            // TODO what if storage is not available?
            // TODO different pic with same name
            File cacheFile = new File(getCacheFilename(url));
            InputStream in = null;
            OutputStream out = null;
            try {
                URL fURL = new URL(url);
                HttpURLConnection conn = (HttpURLConnection) fURL.openConnection();
                conn.setConnectTimeout(CONNECTING_TIME_OUT_MILLIS);
                conn.setRequestMethod("GET");
                if (conn.getResponseCode() == 200) {
                    in = conn.getInputStream();
                    out = new FileOutputStream(cacheFile);

                    byte[] buffer = new byte[BUFFER_SIZE_BYTES];
                    int len;
                    while ((len = in.read(buffer)) != -1) {
                        out.write(buffer, 0, len);
                    }
                } else {
                    Log.e(LOG_TAG, "AsyncLoader: URL does not return 200: " + conn.getResponseCode());
                    // TODO notify
                    return null;
                }
            } catch (MalformedURLException e) {
                Log.e(LOG_TAG, "AsyncLoader: URL malformed: " + url);
                //TODO notify user?
                return null;
            } catch (IOException e) {
                Log.e(LOG_TAG, "AsyncLoader: IOException: " + e.getLocalizedMessage());
                e.printStackTrace();
                //TODO must notify user this time
                return null;
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        Log.i(LOG_TAG, "AsyncLoader: in stream is null. No need to close.");
                    }
                }

                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e) {
                        Log.i(LOG_TAG, "AsyncLoader: out stream is null. No need to close.");
                    }
                }
            }
            return Drawable.createFromPath(cacheFile.getAbsolutePath());
        }
    }

    private class AsyncLoaderForTextView extends AsyncLoaderBase {
        private TextView mTextView;
        private Context mContext;
        private BitmapDrawableContainer mReplacingDrawable;

        AsyncLoaderForTextView(TextView tv, BitmapDrawableContainer drawableContainer) {
            mTextView = tv;
            mContext = tv.getContext();
            mReplacingDrawable = drawableContainer;
        }

        @Override
        protected void onPostExecute(Drawable drawable) {
            if (drawable == null) {
                // TODO Make an error pic
            } else {
                if (mTextView != null) {
                    mReplacingDrawable.mDrawable = drawable;
                    mTextView.requestLayout(); // Refresh the TextView
                }
            }
        }

        @Override
        protected String getCacheFilename(String source) {
            return mContext.getExternalCacheDir().getAbsolutePath()
                    + File.separator + getFilename(source, "default");
        }
    }

    private class AsyncLoaderForImageView extends AsyncLoaderBase {
        private static final String LOG_TAG = "MyCA_AsyncLoaderForImg";
        private ImageView mImageView;
        private Context mContext;

        AsyncLoaderForImageView(ImageView iv) {
            mImageView = iv;
            mContext = iv.getContext();
        }

        @Override
        protected void onPostExecute(Drawable drawable) {
            if (drawable == null) {
                // TODO Make an error pic
            } else {
                if (mImageView != null) {
                    mImageView.setImageDrawable(drawable);
                }
            }
        }

        @Override
        protected String getCacheFilename(String source) {
            return mContext.getExternalCacheDir().getAbsolutePath()
                    + File.separator + getFilename(source, "default");
        }
    }
}
