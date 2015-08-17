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
        article.setTitle("协会第一届摄影大赛作品展览");
        article.setSummary("比赛于今日闭幕，以下为参赛作品一览，请为您支持的作品投一票……");
        article.setPublishTime(System.currentTimeMillis());
        // Random pic on search engine
        article.setCoverURL("http://e.hiphotos.baidu.com/image/pic/item/a71ea8d3fd1f41342b85c597261f95cad0c85ead.jpg");
        article.setContentURL("http://www.hustca.com");
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
