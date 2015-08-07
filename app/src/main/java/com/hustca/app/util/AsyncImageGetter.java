package com.hustca.app.util;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
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
     * Only used in Html.fromHTML(...,imgGetter,...)
     * If you want to load images for ImageView, see setUpForImageView
     */
    @Override
    @SuppressWarnings("deprecation")
    public Drawable getDrawable(String source) {
        BitmapDrawableContainer consistentDrawable = new BitmapDrawableContainer();
        // TODO Make a loading pic here
        consistentDrawable.mDrawable = new BitmapDrawable(mTextView.getResources(),
                ((BitmapDrawable) mTextView.getResources().getDrawable(R.mipmap.ic_launcher)).getBitmap());

        /* Only used in TextView so ImageView is null here */
        AsyncLoader loader = new AsyncLoader(mTextView, null, consistentDrawable);
        loader.execute(source);
        return consistentDrawable;
    }

    /**
     * Load picture for ImageView
     *
     * @param source URL
     */
    public void loadForImageView(String source) {
        // TODO Make a loading pic here
        mImageView.setImageResource(R.mipmap.ic_launcher);
        AsyncLoader loader = new AsyncLoader(null, mImageView, null);
        loader.execute(source);
    }

    /**
     * This class will download the pic to local storage.
     * After downloading, it will replace the drawable in {@link BitmapDrawableContainer}
     * and refresh the corresponding TextView/ImageView.
     * If the pic can be found in local cache, do not load and return directly.
     */
    private class AsyncLoader extends AsyncTask<String, Void, Drawable> {
        private Context mContext; // from mTextView
        private TextView mTextView;
        private ImageView mImageView;
        private BitmapDrawableContainer mReplacingDrawable;

        AsyncLoader(@Nullable TextView textView, @Nullable ImageView imageView,
                    @Nullable BitmapDrawableContainer replacingDrawable) {
            this.mTextView = textView;
            this.mImageView = imageView;
            if (mTextView != null) {
                mContext = mTextView.getContext();
                mReplacingDrawable = replacingDrawable;
            } else if (mImageView != null) {
                mContext = mImageView.getContext();
            } else {
                Log.wtf(LOG_TAG, "AsyncLoader: neither control is defined!");
            }
        }

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
            File cacheFile = new File(mContext.getExternalCacheDir(), getFilename(url, "default"));
            if (cacheFile.exists()) {
                return Drawable.createFromPath(cacheFile.getAbsolutePath());
            }
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

        @Override
        protected void onPostExecute(Drawable drawable) {
            super.onPostExecute(drawable);
            if (drawable == null) {
                //mReplacingDrawable = mContext.getResources().getDrawable(R.mipmap.error_pic);
                // TODO make an error pic
            } else {
                if (mTextView != null) {
                    mReplacingDrawable.mDrawable = drawable;
                    mTextView.requestLayout(); // Refresh the TextView
                }

                if (mImageView != null) {
                    mImageView.setImageDrawable(drawable);
                }
            }
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
        private String getFilename(String url, String default_name) {
            int pos = url.lastIndexOf("/");
            if (pos == -1 || pos == url.length() - 1) return default_name;
            else return url.substring(pos + 1);
        }
    }
}
