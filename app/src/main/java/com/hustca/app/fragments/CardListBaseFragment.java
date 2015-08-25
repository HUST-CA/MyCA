package com.hustca.app.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hustca.app.ArticleCardListAdapter;
import com.hustca.app.R;
import com.hustca.app.util.DividerItemDecoration;

/**
 * Created by Hamster on 2015/7/27.
 * <p/>
 * Template for a fragment which contains a list of cards.
 * <br/>
 * Contains a list view, its adapter and Swipe2Refresh. The adapter is
 * exported as protected member.
 */
public abstract class CardListBaseFragment extends Fragment {

    /**
     * Adapter for the inner ListView.
     * See {@link ArticleCardListAdapter}
     */
    private ArticleCardListAdapter mAdapter;
    /**
     * S2R layout in the fragment
     */
    private SwipeRefreshLayout mSwipeToRefreshLayout;

    /**
     * Used for refreshing data by S2R.
     * Starting and stopping of the indicator should be handled by subclass itself.
     * We provide convenient methods, see setRefreshingIndicator
     */
    protected abstract void refresh();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.card_list, container, false);
        RecyclerView rv = (RecyclerView) v.findViewById(R.id.list_of_cards);

        mAdapter = new ArticleCardListAdapter(getActivity());
        RecyclerView.LayoutManager lm = new LinearLayoutManager(getActivity());
        RecyclerView.ItemDecoration id = new DividerItemDecoration(getActivity(),
                DividerItemDecoration.VERTICAL_LIST);
        rv.setHasFixedSize(true);
        rv.setLayoutManager(lm);
        rv.setAdapter(mAdapter);
        rv.addItemDecoration(id);

        mSwipeToRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_to_refresh_container);
        mSwipeToRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });

        return v;
    }

    protected ArticleCardListAdapter getListAdapter() {
        return mAdapter;
    }

    protected void setRefreshingIndicator(boolean refreshing) {
        mSwipeToRefreshLayout.setRefreshing(refreshing);
    }

    @Override
    public void onStop() {
        super.onStop();

        // Bug fix: Switching frags doesn't stop refreshing indicator.
        // Really weird since the view has been destroyed.
        mSwipeToRefreshLayout.setRefreshing(false);
    }
}
