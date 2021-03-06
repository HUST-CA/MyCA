package com.hustca.app;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONObject;

/**
 * Created by Hamster on 2015/7/27.
 * <p/>
 * A class representing an article (any piece of text here).
 * Supported types: see the code of {@link com.hustca.app.Article.ArticleType}
 */
public class Article implements Parcelable {
    public static final Creator<Article> CREATOR = new Creator<Article>() {
        @Override
        public Article createFromParcel(Parcel in) {
            return new Article(in);
        }

        @Override
        public Article[] newArray(int size) {
            return new Article[size];
        }
    };
    /**
     * Article type. See {@link com.hustca.app.Article.ArticleType}
     */
    private ArticleType mType;
    /**
     * Author instance. Maybe id and name only.
     */
    private User mAuthor;
    /**
     * Article ID. Not that article of different type may have the same ID.
     */
    private int mId;
    /**
     * Publish time. Unix timestamp in milliseconds.
     */
    private long mPublishTime;
    /**
     * Title
     */
    private String mTitle;
    /**
     * Summary. Usually two lines (20~30 Chinese chars)
     */
    private String mSummary;
    /**
     * Content URL
     */
    private String mContentURL;
    /**
     * This will not be added into parcel.
     */
    private String mContent;
    /**
     * URL of cover picture. Should be specified by server side.
     */
    private String mCoverURL;
    /**
     * Place of interest :-)
     */
    private String mLocation;

    protected Article(Parcel in) {
        mId = in.readInt();
        mPublishTime = in.readLong();
        mTitle = in.readString();
        mSummary = in.readString();
        mContentURL = in.readString();
        mCoverURL = in.readString();
        mLocation = in.readString();
    }

    public Article() {
        mId = -1;
        // Seems like nothing else could be done here.
        // Leave everything null as the default initializer does.
    }

    /*
     * Countless getters
     */
    public ArticleType getType() {
        return mType;
    }

    public void setType(ArticleType mType) {
        this.mType = mType;
    }

    public User getAuthor() {
        return mAuthor;
    }

    public void setAuthor(User mAuthor) {
        this.mAuthor = mAuthor;
    }

    public int getId() {
        return mId;
    }

    public void setId(int mId) {
        this.mId = mId;
    }

    public long getPublishTime() {
        return mPublishTime;
    }

    public void setPublishTime(long mPublishTime) {
        this.mPublishTime = mPublishTime;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public String getSummary() {
        return mSummary;
    }

    public void setSummary(String mSummary) {
        this.mSummary = mSummary;
    }

    public String getContent() {
        return mContent;
    }

    public void setContent(String mContent) {
        this.mContent = mContent;
    }

    public String getCoverURL() {
        return mCoverURL;
    }

    public void setCoverURL(String mCoverURL) {
        this.mCoverURL = mCoverURL;
    }

    public String getContentURL() {
        return mContentURL;
    }

    public void setContentURL(String mContentURL) {
        this.mContentURL = mContentURL;
    }

    public void initArticleWithJSON(JSONObject json) {
        // TODO
    }

    /* Auto generated Parcelable implementations. Impressive! */
    @Override
    public int describeContents() {
        // I don't know what does this do. Leave it 0. (one type only)
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        // Still don't know what does the second param do
        dest.writeInt(mId);
        dest.writeLong(mPublishTime);
        dest.writeString(mTitle);
        dest.writeString(mSummary);
        dest.writeString(mContentURL);
        dest.writeString(mCoverURL);
        dest.writeString(mLocation);
    }

    public enum ArticleType {
        ARTICLE_NEWS,
        ARTICLE_ACTIVITY, // Recent activity
        ARTICLE_HISTORY,
        ARTICLE_USERTOPIC, // a thread in H2O world
        ARTICLE_REPLY // reply to a thread or news
    }
}
