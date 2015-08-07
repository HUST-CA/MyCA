package com.hustca.app.util;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.hustca.app.Article;
import com.hustca.app.R;

/**
 * Created by Hamster on 2015/7/27.
 * <p/>
 * Misc methods about articles. Single instance.
 */
public class ArticleUtil {
    private static final String LOG_TAG = "MyCA_ArticleUtil";
    private static Context mContext;
    private static ArticleUtil mInstance = new ArticleUtil();

    private ArticleUtil() {
    }

    public static String loadArticleContent(Article article) {
        // TODO
        return null;
    }

    /**
     * Load cover photo for an article. <p/>
     * This is a network operation. Do not perform in UI thread.
     *
     * @param article Article to load cover pic
     * @return a Drawable of the cover
     */
    @SuppressWarnings("deprecation")
    public static Drawable loadArticlePic(Article article) {
        // TODO
        if (mContext == null) {
            Log.e(LOG_TAG, "loadArticlePic: context is null. Returning null.");
            return null;
        }
        return mContext.getResources().getDrawable(R.mipmap.ic_launcher);
    }

    public void setContext(Context context) {
        mInstance.setContext(context);
    }
}
