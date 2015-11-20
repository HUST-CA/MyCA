package com.hustca.app.fragments;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.hustca.app.Article;
import com.hustca.app.util.RSSParser;
import com.hustca.app.util.networking.AsyncTextLoader;

import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Hamster on 2015/7/26.
 * <p/>
 * A fragment to show recent activities in an association.
 */
public class RecentActivitiesFragment extends CardListBaseFragment {

    private static final String URL = "http://www.hustca.com/news/feed";

    public void refresh() {
        setRefreshingIndicator(true);
        AsyncTextLoader.OnFinishListener listener = new AsyncTextLoader.OnFinishListener() {
            @Override
            public void onFinish(String content) {
                RSSParser parser = new RSSParser();
                ArrayList<Article> articles = null;
                try {
                    articles = parser.parse(new ByteArrayInputStream(content.getBytes()));
                } catch (XmlPullParserException e) {
                    Toast.makeText(RecentActivitiesFragment.this.getActivity(), "XML Malformed", Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    Toast.makeText(RecentActivitiesFragment.this.getActivity(), "Read error", Toast.LENGTH_SHORT).show();
                }
                if (articles != null) {
                    // no error
                    getRecyclerView().smoothScrollToPosition(0);
                    getListAdapter().add(articles);
                    getListAdapter().notifyItemRangeInserted(0, articles.size());
                    setRefreshingIndicator(false);
                }
            }
        };
        AsyncTextLoader textLoader = new AsyncTextLoader(this.getActivity(), listener);
        textLoader.execute(URL);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        refresh(); // load on boot
    }

}
