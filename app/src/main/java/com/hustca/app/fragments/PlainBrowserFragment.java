package com.hustca.app.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.hustca.app.R;

/**
 * Created by hamster on 16/5/1.
 * <p/>
 * Simple fragment for a web browser, supporting passing URL as fragment bundle
 * or overriding the getURL() method.
 */
public class PlainBrowserFragment extends Fragment {
    /**
     * URL to load, passed as an argument in a bundle.
     */
    public static final String KEY_URL = "url";

    private WebView mWebView;
    private ProgressBar mProgressBar;

    /**
     * How to get the URL for loading in the WebView.
     * <p/>
     * By default, this method reads the argument for this fragment
     * and finds a key named "url".
     *
     * @return URL to load
     */
    protected String getURL() {
        /* TODO default as error page */
        return getArguments().getString(KEY_URL, "about:blank");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.plain_browser, container, false);
        mWebView = (WebView) v.findViewById(R.id.plain_browser_webview);
        mProgressBar = (ProgressBar) v.findViewById(R.id.plain_browser_progress);
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();

        mProgressBar.setMax(100); // newProgress documentation. For later use.
        mProgressBar.setIndeterminate(true);

        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                mProgressBar.setIndeterminate(false); // We have clear % now. Not intermediate.
                mProgressBar.setProgress(newProgress);
                if (newProgress != 100) {
                    mProgressBar.setVisibility(View.VISIBLE);
                } else {
                    mProgressBar.setVisibility(View.INVISIBLE);
                }
            }
        });
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        mWebView.getSettings().setDomStorageEnabled(true);
        String appCachePath = getActivity().getCacheDir().getAbsolutePath();
        mWebView.getSettings().setAppCachePath(appCachePath);
        mWebView.getSettings().setAllowFileAccess(true);
        mWebView.getSettings().setAppCacheEnabled(true);

        mProgressBar.setVisibility(View.VISIBLE);
        mWebView.loadUrl(getURL());

        /* Before 1st progress update (before 0%) show an intermediate bar,
        after 1st update (we got percentage) show the progress clearly.
         */
    }


}
