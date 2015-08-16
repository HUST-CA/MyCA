package com.hustca.app.util;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Hamster on 2015/8/16.
 * <p/>
 * This class will download the specific content from URL.<p/>
 */
public abstract class AsyncLoader extends AsyncTask<String, Void, InputStream> {
    private static final String LOG_TAG = "MyCA_AsyncLoader";
    /**
     * Used in HTTP connection. Connection Time out in ms. Will throw exception if exceeded.
     */
    private int mConnectionTimeoutMillis = 5000;

    public int getConnectionTimeoutMillis() {
        return mConnectionTimeoutMillis;
    }

    public void setConnectionTimeoutMillis(int mConnectionTimeoutMillis) {
        this.mConnectionTimeoutMillis = mConnectionTimeoutMillis;
    }

    /**
     * Since doInBackground is a overriding method, it can't use "throws".
     * But the exceptions needs to be dealt with (need to be shown to user here. Be clear about
     * what's happening, not just "network error")
     * Please implement this in subclasses using Log or Toast to properly show the errors.
     * And we return null after this method call.
     *
     * @param e Captured exception
     */
    protected abstract void exceptionHandler(Exception e);

    /**
     * Fetch data from URL
     * <p/>
     * Don't forget to close the input stream!
     *
     * @param params [0] for URL
     * @return InputStream of URL. If any error happens, null.
     */
    @Override
    protected InputStream doInBackground(String... params) {
        if (params.length == 0) {
            Log.e(LOG_TAG, "doInBackground: params length = 0");
            return null;
        }

        String url = params[0];
        URL fURL;
        try {
            fURL = new URL(url);
        } catch (MalformedURLException e) {
            exceptionHandler(e);
            return null;
        }

        InputStream in;
        try {
            HttpURLConnection conn = (HttpURLConnection) fURL.openConnection();
            conn.setConnectTimeout(mConnectionTimeoutMillis);
            conn.setRequestMethod("GET");
            if (conn.getResponseCode() == 200) {
                in = conn.getInputStream();
            } else {
                Log.e(LOG_TAG, "AsyncLoader: URL does not return 200: " + conn.getResponseCode());
                // TODO notify
                return null;
            }
        } catch (IOException e) {
            exceptionHandler(e);
            return null;
        }

        return in;
    }
}