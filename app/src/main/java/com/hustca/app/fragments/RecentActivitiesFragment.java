package com.hustca.app.fragments;

import android.os.Bundle;
import android.view.View;

import com.hustca.app.Article;
import com.hustca.app.RefreshIndicator;
import com.hustca.app.Refreshable;

/**
 * Created by Hamster on 2015/7/26.
 * <p/>
 * A fragment to show recent activities in an association.
 */
public class RecentActivitiesFragment extends CardListBaseFragment implements Refreshable {

    @Override
    public void refresh() {
        if (getActivity() instanceof RefreshIndicator)
            ((RefreshIndicator) getActivity()).onRefreshStarted();
        // TODO
        // This is simulation
        Article article = new Article();
        article.setTitle("test");
        article.setSummary("test");
        article.setPublishTime(System.currentTimeMillis());
        mAdapter.add(article);
        mAdapter.notifyDataSetChanged();
        if (getActivity() instanceof RefreshIndicator)
            ((RefreshIndicator) getActivity()).onRefreshStopped();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        refresh();
    }
}
