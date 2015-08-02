package com.hustca.app.fragments;

import android.os.Bundle;
import android.view.View;

import com.hustca.app.Article;

/**
 * Created by Hamster on 2015/7/26.
 * <p/>
 * A fragment to show recent activities in an association.
 */
public class RecentActivitiesFragment extends CardListBaseFragment {

    public void refresh() {
        setRefreshingIndicator(true);
        // TODO
        // This is simulation
        Article article = new Article();
        article.setTitle("test");
        article.setSummary("test");
        article.setPublishTime(System.currentTimeMillis());
        getListAdapter().add(article);
        getListAdapter().notifyDataSetChanged();
        setRefreshingIndicator(false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        refresh(); // load on boot
    }

}
