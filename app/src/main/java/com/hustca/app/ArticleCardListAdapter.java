package com.hustca.app;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hustca.app.fragments.NewsBrowserFragment;
import com.hustca.app.util.AsyncImageGetter;

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
public class ArticleCardListAdapter extends BaseAdapter {

    private ArrayList<Article> mArticles;
    private Context mContext;

    // Each view shares this listener
    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Article article = ((CardViewHolder) v.getTag()).relatedArticle;
            // TODO Use different method to launch browser on landscape/tablets
            // Here we just starts the activity, like we are in portrait
            Intent intent = new Intent(mContext, NewsBrowserActivity.class);
            intent.putExtra(NewsBrowserFragment.KEY_ARTICLE_BUNDLE, article);
            mContext.startActivity(intent);
        }
    };

    public ArticleCardListAdapter(Context context) {
        mContext = context;
        mArticles = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return mArticles.size();
    }

    @Override
    public Object getItem(int position) {
        if (position < 0 || position > mArticles.size())
            return null;
        return mArticles.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CardViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.card_item, parent, false);
            holder = new CardViewHolder();
            holder.imageView = (ImageView) convertView.findViewById(R.id.image_card);
            holder.summaryText = (TextView) convertView.findViewById(R.id.text_card_summary);
            holder.timeAndPlaceText = (TextView) convertView.findViewById(R.id.text_card_time);
            holder.titleText = (TextView) convertView.findViewById(R.id.text_card_title);
            convertView.setTag(holder);
        } else {
            // Each time we created a new view, we attach a tag
            // so there's no view without a tag
            // If there is, blame later programmers.
            holder = (CardViewHolder) convertView.getTag();
        }

        Article article = mArticles.get(position);
        holder.titleText.setText(article.getTitle());
        holder.summaryText.setText(article.getSummary());
        holder.timeAndPlaceText.setText(SimpleDateFormat.getInstance().format(
                article.getPublishTime()));
        holder.relatedArticle = article;
        convertView.setOnClickListener(onClickListener);

        // Loading pic is drawn here
        AsyncImageGetter getter = new AsyncImageGetter(holder.imageView);
        getter.loadForImageView(article.getCoverURL());

        return convertView;
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
     * Remove one entry
     * Remember to notifyDataSetChanged()
     */
    public void removeItemAt(int pos) {
        mArticles.remove(pos);
    }

    /**
     * This class will be set as tag for each view.
     */
    private class CardViewHolder {
        ImageView imageView;
        TextView titleText;
        TextView summaryText;
        TextView timeAndPlaceText;
        Article relatedArticle;
    }
}
