package com.hustca.app.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.hustca.app.ArticleCardListAdapter;
import com.hustca.app.R;

/**
 * Created by Hamster on 2015/7/27.
 * <p/>
 * Base template for a fragment which contains a list of cards
 */
public class CardListBaseFragment extends Fragment {

    /**
     * Adapter for the inner ListView.
     * See {@link ArticleCardListAdapter}
     */
    protected ArticleCardListAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.card_list, container, false);
        ListView lv = (ListView) v.findViewById(R.id.list_of_cards);

        mAdapter = new ArticleCardListAdapter(getActivity());
        lv.setAdapter(mAdapter);

        return v;
    }
}
