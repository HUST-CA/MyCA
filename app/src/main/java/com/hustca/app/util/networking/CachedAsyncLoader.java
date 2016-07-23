package com.hustca.app.util.networking;

import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Hamster on 2015/8/16.
 * <p/>
 * {@link AsyncLoader} with caches.
 */
public abstract class CachedAsyncLoader extends AsyncLoader {
    private static final String LOG_TAG = "MyCA_CachedAsyncLoader";

    /**
     * Bytes to copy at once from Internet to cache file.
     */
    private static final int BUFFER_SIZE = 1024;

    /**
     * Return a cache file path for a URL.<p/>
     * e.g. <em>http://www.a.com/a.jpg</em> returns
     * <em>/sdcard/Android/data/com.this.app/files/a.jpg</em><br/>
     * This file can be non-existent and this class will create it.
     * <p/>
     * Since this is a network-only bare-bone class, it can not read external cache dir
     * by Context. Plz implement this using your mContext in your subclass.
     * <p/>
     * TODO what if storage is not available?
     * TODO different pic with same name
     *
     * @param source URL to load. You can just take the file name.
     * @return The cache file path corresponding to the URL.
     */
    protected abstract String getCacheFileName(String source);

    /**
     * Same as {@link AsyncLoader#doInBackground(String...)}, added cache feature.
     * If any error happens, the same InputStream will still be returned as base method returns.
     *
     * @param params [0] for URL
     * @return Same InputStream
     */
    @Override
    protected InputStream doInBackground(String... params) {
        InputStream in = super.doInBackground(params);

        if (in == null) return null;

        File cacheFile = new File(getCacheFileName(params[0]));
        OutputStream out;
        try {
            out = new FileOutputStream(cacheFile);
        } catch (FileNotFoundException e) {
            // According to doc, FileOutputStream is ABLE to create the file.
            // This exception is used when the file can't be opened for writing.
            // Permission denied, occupied by other process etc.
            Log.e(LOG_TAG, "doInBackground: can't open " + cacheFile.getAbsolutePath()
                    + " for writing: " + e.getLocalizedMessage());
            return in; // still return this for later use.
        }

        byte[] buffer = new byte[BUFFER_SIZE];
        int len;
        try {
            while ((len = in.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }
            out.flush();
            out.close();
        } catch (IOException e) {
            Log.e(LOG_TAG, "doInBackground: can't read/write/flush/close streams, returning null: "
                    + e.getLocalizedMessage());
            return null; // the InputStream has been consumed and no other stream available.
        }

        try {
            return new FileInputStream(cacheFile);
        } catch (FileNotFoundException e) {
            // If we are here, it means the world is going to BOOM in one day!
            return null; // the InputStream has been consumed and no other stream available.
        }
    }
}
