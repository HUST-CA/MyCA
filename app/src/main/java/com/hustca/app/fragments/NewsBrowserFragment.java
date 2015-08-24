package com.hustca.app.fragments;

import android.app.Fragment;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.NavUtils;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;

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
    private ProgressBar mProgressBar;

    @Override
    @SuppressWarnings("deprecation")
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.news_browser, container, false);
        mHeaderImageView = (ImageView) v.findViewById(R.id.news_browser_header_image);
        mCollapsingToolbar = (CollapsingToolbarLayout) v.findViewById(R.id.collapsing_toolbar);
        mWebView = (WebView) v.findViewById(R.id.news_browser_web);
        mProgressBar = (ProgressBar) v.findViewById(R.id.news_browser_progress);

        Toolbar toolbar = (Toolbar) v.findViewById(R.id.news_browser_toolbar);
        Drawable arrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        // AS suggested this if
        if (arrow != null) {
            arrow.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
            toolbar.setNavigationIcon(arrow);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    NavUtils.navigateUpFromSameTask(getActivity());
                }
            });
        }

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

        mHeaderImageView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                mHeaderImageView.getViewTreeObserver().removeOnPreDrawListener(this);
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
                    getActivity().startPostponedEnterTransition();
                }
                return true;
            }
        });
        AsyncImageGetter asyncImageGetter = new AsyncImageGetter(mHeaderImageView);
        asyncImageGetter.loadForImageView(article.getCoverURL());

        mCollapsingToolbar.setTitle(article.getTitle());
        // TODO Do we need to change the colors according to picture?
        mCollapsingToolbar.setCollapsedTitleTextAppearance(R.style.TextAppearance_CollapsedCollapsingBar);
        mCollapsingToolbar.setExpandedTitleTextAppearance(R.style.TextAppearance_ExpandedCollapsingBar);

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
        mWebView.getSettings().setJavaScriptEnabled(true);

        mProgressBar.setVisibility(View.VISIBLE);
        mWebView.loadUrl(article.getContentURL());

        /* Before 1st progress update (before 0%) show an intermediate bar,
        after 1st update (we got percentage) show the progress clearly.
         */
    }
}
