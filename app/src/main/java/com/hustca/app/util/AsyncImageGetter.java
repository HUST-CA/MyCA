package com.hustca.app.util;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hustca.app.R;

import java.io.File;
import java.io.InputStream;


/**
 * Created by Hamster on 2015/8/7.
 * <p/>
 * Async image getter. Used in TextView.setText(Html.fromHtml(...,imageGetter,...));
 * Can also be used with ImageView. But you can not refresh them both.
 */
public class AsyncImageGetter implements Html.ImageGetter {
    private static final String LOG_TAG = "MyCA_AsyncIMG";

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

    private class AsyncLoaderForTextView extends CachedAsyncLoader {
        private TextView mTextView;
        private Context mContext;
        private BitmapDrawableContainer mReplacingDrawable;

        AsyncLoaderForTextView(TextView tv, BitmapDrawableContainer drawableContainer) {
            mTextView = tv;
            mContext = tv.getContext();
            mReplacingDrawable = drawableContainer;
        }

        @Override
        protected void onPostExecute(InputStream in) {
            if (in == null) {
                // TODO Make an error pic
                mReplacingDrawable.mDrawable = mContext.getResources().getDrawable(R.mipmap.ic_launcher);
            } else {
                if (mTextView != null) {
                    mReplacingDrawable.mDrawable = Drawable.createFromStream(in, null);
                    mTextView.requestLayout(); // Refresh the TextView
                }
            }
        }

        @Override
        protected String getCacheFileName(String source) {
            return mContext.getExternalCacheDir().getAbsolutePath()
                    + File.separator + getFilename(source, "default");
        }

        /**
         * This is often called from network thread. So use a handler
         *
         * @param e Captured exception
         */
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

    private class AsyncLoaderForImageView extends CachedAsyncLoader {
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
                    + File.separator + getFilename(source, "default");
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
