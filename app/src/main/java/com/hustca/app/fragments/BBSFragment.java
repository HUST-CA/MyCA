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
    private static final String URL = "http://wsq.discuz.qq.com/?c=index&a=index&f=wx&fid=2&siteid=265482436";
    private static final String BASE_URL = "wsq.discuz.qq.com";

    @Override
    protected String getURL() {
        return URL;
    }

    @Override
    protected String getBaseURL() {
        return BASE_URL;
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
