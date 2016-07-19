package com.hustca.app.fragments;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import com.hustca.app.Article;
import com.hustca.app.R;
import com.hustca.app.util.networking.AsyncImageGetter;

/**
 * Created by Hamster on 2015/8/2.
 * <p/>
 * A fragment displaying news/history detail, with collapsing toolbar and header image
 */
public class NewsBrowserFragment extends Fragment {

    /**
     * Passing article info between activity and frag using bundle. This is the key.
     */
    public static final String KEY_ARTICLE_BUNDLE = "article";

    private static final String LOG_TAG = "MyCA_NewsBrowserFrag";
    private ImageView mHeaderImageView;
    private CollapsingToolbarLayout mCollapsingToolbar;

    @Override
    @SuppressWarnings("deprecation")
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.news_browser, container, false);
        mHeaderImageView = (ImageView) v.findViewById(R.id.news_browser_header_image);
        mCollapsingToolbar = (CollapsingToolbarLayout) v.findViewById(R.id.collapsing_toolbar);

        Toolbar toolbar = (Toolbar) v.findViewById(R.id.news_browser_toolbar);
        Drawable arrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        // AS suggested this if
        if (arrow != null) {
            arrow.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
            toolbar.setNavigationIcon(arrow);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getActivity().onBackPressed();
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
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    getActivity().startPostponedEnterTransition();
                }
                return true;
            }
        });
        AsyncImageGetter.loadForImageView(mHeaderImageView, article.getCoverURL());

        mCollapsingToolbar.setTitle(article.getTitle());
        // TODO Do we need to change the colors according to picture?
        mCollapsingToolbar.setCollapsedTitleTextAppearance(R.style.TextAppearance_CollapsedCollapsingBar);
        mCollapsingToolbar.setExpandedTitleTextAppearance(R.style.TextAppearance_ExpandedCollapsingBar);

        PlainBrowserFragment browserFragment = new PlainBrowserFragment();
        Bundle argument = new Bundle(1);
        argument.putString(PlainBrowserFragment.KEY_URL, article.getContentURL());
        browserFragment.setArguments(argument);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.add(R.id.plain_browser_fragment_container, browserFragment);
        transaction.commit();
    }
}
