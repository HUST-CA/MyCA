package com.hustca.app.util.networking;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.text.Html;
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

    private ImageView mImageView;

    public AsyncImageGetter(ImageView iv) {
        mImageView = iv;
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
     * Load picture for ImageView.<br/>
     * This will check if it's already cached.
     * If so, no loading pic will be shown and the cached image is shown immediately.
     * If not, it will show a loading pic and refresh when loading finished.
     * <em>Remember to initialize with ImageView</em>
     *
     * @param source URL
     */
    public void loadForImageView(String source) {
        if (null == mImageView) {
            Log.e(LOG_TAG, "loadForImageView: mImageView is null. Returning.");
            return;
        }
        if (null == source) {
            Log.d(LOG_TAG, "loadForImageView: source is null. Returning.");
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
