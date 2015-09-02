package com.hustca.app.util;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.hustca.app.R;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

/**
 * Created by Hamster on 2015/8/16.
 * <p/>
 * Async loader for article's full content. Of course, we do not cache this.
 */
public class AsyncTextLoader extends AsyncLoader {
    private static final String LOG_TAG = "MyCA_AsyncContent";

    private Context mContext;
    private OnFinishListener mCallback;
    private String mReturningString;

    public AsyncTextLoader(Context context, OnFinishListener cb) {
        mContext = context;
        mCallback = cb;
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

    @Override
    protected InputStream doInBackground(String... param) {
        InputStream inputStream = super.doInBackground(param);

        BufferedReader br;
        try {
            br = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            br = new BufferedReader(new InputStreamReader(inputStream));
            Log.e(LOG_TAG, "onPostExecute: unsupported encoding, fallback to default: "
                    + e.getLocalizedMessage());
        }

        /*
        Note that reading from this InputStream is network operation,
        so it has to be done here in doInBackground, not onPostExecute.
         */
        StringBuilder builder = new StringBuilder();
        String line;
        try {
            while ((line = br.readLine()) != null) {
                builder.append(line);
                builder.append("\n");
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "onPostExecute: IOException when reading lines: "
                    + e.getLocalizedMessage());
        }

        mReturningString = builder.toString();
        try {
            return new ByteArrayInputStream(mReturningString.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            Log.e(LOG_TAG, "onPostExecute: unsupported encoding, fallback to default: "
                    + e.getLocalizedMessage());
            return new ByteArrayInputStream(mReturningString.getBytes());
        }
    }

    @Override
    protected void onPostExecute(InputStream inputStream) {
        mCallback.onFinish(mReturningString);
    }

    /**
     * What do to after the content has been loaded.
     */
    public interface OnFinishListener {
        void onFinish(String content);
    }
}
