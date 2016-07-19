package com.hustca.app;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hustca.app.activities.NewsBrowserActivity;
import com.hustca.app.fragments.NewsBrowserFragment;
import com.hustca.app.util.networking.AsyncImageGetter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Hamster on 2015/7/27.
 * <p/>
 * An adapter for list of cards (only articles).
 * <br/>
 * Used in History/News/RecentActActivity.
 * <br/>
 * <strong>ATTENTION! </strong> Data should be sorted time-descending.
 * mArticles[0] should be the latest one.
 */
public class ArticleCardListAdapter extends RecyclerView.Adapter<ArticleCardListAdapter.CardViewHolder> {

    private ArrayList<Article> mArticles;
    private Activity mActivity; // Shared Element Transition

    public ArticleCardListAdapter(Activity activity) {
        mActivity = activity;
        mArticles = new ArrayList<>();
    }

    @Override
    public CardViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_item, viewGroup, false);
        return new CardViewHolder(v);
    }

    @Override
    public void onBindViewHolder(CardViewHolder cardViewHolder, int i) {
        Article article = mArticles.get(i);
        cardViewHolder.titleText.setText(article.getTitle());
        cardViewHolder.summaryText.setText(Html.fromHtml(article.getSummary())
                .toString().replace((char)65532, (char)32)); // Strip [OBJ](65532)
        cardViewHolder.timeAndPlaceText.setText(SimpleDateFormat.getInstance().format(
                article.getPublishTime()));
        cardViewHolder.relatedArticle = article;
        AsyncImageGetter.loadForImageView(cardViewHolder.imageView, article.getCoverURL());
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return mArticles.size();
    }

    /**
     * Clear all data in this list.
     * Remember to notifyDataSetChanged()
     */
    public void clear() {
        mArticles.clear();
    }

    /**
     * Add a number of articles (array)
     * Remember to notifyDataSetChanged()
     */
    public void add(Article[] articles) {
        Collections.addAll(mArticles, articles);
    }

    /**
     * Add one entry to the beginning. Use when the latest info comes.
     * Remember to notifyDataSetChanged()
     */
    public void add(Article article) {
        mArticles.add(0, article);
    }

    /**
     * Add one entry to the beginning.
     * NOTE: Compare with previous data
     * TODO Shall we make the judgment here?
     * Remember to notifyDataSetChanged()
     */
    public void add(ArrayList<Article> articles) {
        mArticles.addAll(articles);
    }

    /**
     * Remove one entry
     * Remember to notifyDataSetChanged()
     */
    public void removeItemAt(int pos) {
        mArticles.remove(pos);
    }

    /**
     * No more tags. We have RecyclerView!
     */
    public class CardViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView titleText;
        TextView summaryText;
        TextView timeAndPlaceText;
        Article relatedArticle;

        private View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Use different method to launch browser on landscape/tablets
                // Here we just starts the activity, like we are in portrait
                Intent intent = new Intent(mActivity, NewsBrowserActivity.class);
                intent.putExtra(NewsBrowserFragment.KEY_ARTICLE_BUNDLE, relatedArticle);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    // Don't use v as 2nd param, otherwise the animation will be misplaced.
                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(
                            mActivity, imageView, "header");
                    mActivity.startActivity(intent, options.toBundle());
                } else {
                    mActivity.startActivity(intent);
                }
            }
        };

        public CardViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.image_card);
            summaryText = (TextView) itemView.findViewById(R.id.text_card_summary);
            timeAndPlaceText = (TextView) itemView.findViewById(R.id.text_card_time);
            titleText = (TextView) itemView.findViewById(R.id.text_card_title);
            itemView.setOnClickListener(onClickListener);
        }
    }


}
