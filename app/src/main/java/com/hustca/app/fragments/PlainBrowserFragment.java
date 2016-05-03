package com.hustca.app.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
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
    /**
     * Links under this base URL will be opened within the app.
     * Otherwise links will be opened by external browsers.
     * Since the base URL is searched in the whole URL (not just comparing from beginning)
     * so it can be a part of the URL.
     * <p/>
     * Passed as an argument.
     */
    public static final String KEY_BASE_URL = "base_url";

    private static final String DEFAULT_BASE_URL = "www.hustca.com/hustca";
    private String mBaseUrl;

    protected WebView mWebView;
    protected ProgressBar mProgressBar;

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

    /**
     * How to determine the base URL.
     * <p/>
     * By default, this method reads from the arguments and find
     * a key named "base_url".
     *
     * @return Base URL that need to be opened within app
     */
    protected String getBaseURL() {
        return getArguments().getString(KEY_BASE_URL, DEFAULT_BASE_URL);
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

        mBaseUrl = getBaseURL();

        mProgressBar.setMax(100); // newProgress documentation. For later use.
        mProgressBar.setIndeterminate(true);

        /* For logging into Discuz */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            CookieManager.getInstance().setAcceptThirdPartyCookies(mWebView, true);

        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.indexOf(mBaseUrl) > 0) {
                    view.loadUrl(url);
                    return true;
                } else {
                    /* Simply call super.thisMethod won't let you choose browsers */
                    Uri uri = Uri.parse(url);
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                    return true;
                }
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

        /* Enable LocalStorage, for Discuz wsg.qq.com */
        webSettings.setDomStorageEnabled(true);

        mProgressBar.setVisibility(View.VISIBLE);
        mWebView.loadUrl(getURL());

        /* Before 1st progress update (before 0%) show an intermediate bar,
        after 1st update (we got percentage) show the progress clearly.
         */
    }
}
