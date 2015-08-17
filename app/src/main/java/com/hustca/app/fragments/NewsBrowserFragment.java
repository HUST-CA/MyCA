package com.hustca.app.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import com.hustca.app.Article;
import com.hustca.app.R;
import com.hustca.app.util.AsyncImageGetter;

/**
 * Created by Hamster on 2015/8/2.
 * <p/>
 * A fragment displaying news/history detail.
 */
public class NewsBrowserFragment extends Fragment {

    /**
     * Passing article info between activity and frag using bundle. This is the key.
     */
    public static final String KEY_ARTICLE_BUNDLE = "article";

    private static final String LOG_TAG = "MyCA_NewsBrowserFrag";
    private ImageView mHeaderImageView;
    private CollapsingToolbarLayout mCollapsingToolbar;
    private WebView mWebView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.news_browser, container, false);
        mHeaderImageView = (ImageView) v.findViewById(R.id.news_browser_header_image);
        mCollapsingToolbar = (CollapsingToolbarLayout) v.findViewById(R.id.collapsing_toolbar);
        mWebView = (WebView) v.findViewById(R.id.news_browser_web);

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();

        final Article article = getArguments().getParcelable(KEY_ARTICLE_BUNDLE);
        if (article == null) {
            Log.w(LOG_TAG, "onResume: article from Parcelable is null. Returning.");
            // TODO notify user about broken object
            return;
        }

        AsyncImageGetter asyncImageGetter = new AsyncImageGetter(mHeaderImageView);
        asyncImageGetter.loadForImageView(article.getCoverURL());

        mCollapsingToolbar.setTitle(article.getTitle());
        // TODO Do we need to change the colors according to picture?
        mCollapsingToolbar.setCollapsedTitleTextAppearance(R.style.TextAppearance_CollapsedCollapsingBar);
        mCollapsingToolbar.setExpandedTitleTextAppearance(R.style.TextAppearance_ExpandedCollapsingBar);

        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        mWebView.loadUrl(article.getContentURL());
    }
}
