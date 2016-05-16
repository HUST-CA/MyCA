package com.hustca.app.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

/**
 * Created by hamster on 16/5/1.
 * <p/>
 * BBS fragment implementation. Just a extension of PlainBrowserFragment.
 */
public class BBSFragment extends PlainBrowserFragment {
    private static final String URL = "http://discuz.hustca.com";
    private static final String[] BASE_URL_LIST = {"wsq.discuz.com", "discuz.hustca.com"};

    @Override
    protected String getURL() {
        return URL;
    }

    @Override
    protected boolean shouldOpenInApp(String url) {
        for (String baseUrl : BASE_URL_LIST) {
            if (url.contains(baseUrl))
                return true;
        }
        return false;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View v = super.onCreateView(inflater, container, savedInstanceState);
        /* Make WebView takes up the whole screen */
        if (v != null) {
            v.post(new Runnable() {
                @Override
                public void run() {
                    mWebView.setLayoutParams(new FrameLayout.LayoutParams(
                            v.getWidth(), v.getHeight()));
                }
            });
        }
        return v;
    }
}
