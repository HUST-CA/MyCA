package com.hustca.app.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hustca.app.R;

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.news_browser, container);
    }

    @Override
    public void onResume() {
        super.onResume();
        // TODO do the getArgument() things here.
    }
}
