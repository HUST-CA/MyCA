package com.hustca.app.util;

import android.util.Log;
import android.util.Xml;

import com.hustca.app.Article;
import com.hustca.app.User;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Hamster on 2015/10/5.
 * <p/>
 * XML Parser based on PULL. Used to parse RSS feeds and show in corresponding section.
 * Specifically for WordPress' RSS2.0.
 */
public class RSSParser {

    private static final String LOG_TAG = "MyCA_RSS";
    private static final String INPUT_ENCODING = "UTF-8";
    private static final int ARRAY_INITIAL_SIZE = 10;

    /* RSS Format */
    private static final String RSS_ITEM = "item";
    private static final String RSS_TITLE = "title";
    private static final String RSS_AUTHOR = "creator"; // dc:creator
    private static final String RSS_CONTENT_URL = "guid"; // "link" is not safe for Chinese title
    private static final String RSS_SUMMARY = "description";
    private static final String RSS_DATE = "pubDate";
    private static final String RSS_CONTENT = "encoded"; // content:encoded

    /**
     * Date format for pubDate.
     * E.g. "Mon, 05 Oct 2015 05:38:47 +0000"
     */
    private static final String RSS_DATE_FORMAT = "EE, dd MMM yyyy hh:mm:ss ZZZZ";

    /**
     * Parse the input stream into an ArrayList of Article instances
     *
     * @param is Input stream for RSS/XML feed
     * @return Parsed ArrayList
     * @throws XmlPullParserException Format error etc
     * @throws IOException            TODO I don't know why. parser.next()
     */
    public ArrayList<Article> parse(InputStream is) throws XmlPullParserException, IOException {
        XmlPullParser parser = Xml.newPullParser();
        parser.setInput(is, INPUT_ENCODING);

        ArrayList<Article> articles = null;
        Article articleBuf = null;
        int eventType = parser.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            switch (eventType) {
                case XmlPullParser.START_DOCUMENT:
                    articles = new ArrayList<>(ARRAY_INITIAL_SIZE);
                    break;
                case XmlPullParser.START_TAG:
                    if (articleBuf != null) {
                        if (parser.getName().equals(RSS_TITLE)) { // Android Studio is stupid here
                            parser.next(); // If we don't do this, we will get null in getText
                            articleBuf.setTitle(parser.getText());
                        } else if (parser.getName().equals(RSS_CONTENT_URL)) {
                            parser.next();
                            articleBuf.setContentURL(parser.getText());
                        } else if (parser.getName().equals(RSS_SUMMARY)) {
                            parser.next();
                            articleBuf.setSummary(parser.getText());
                        } else if (parser.getName().equals(RSS_AUTHOR)) {
                            parser.next();
                            articleBuf.setAuthor(new User(parser.getText(), User.INVALID_USER_ID));
                        } else if (parser.getName().equals(RSS_DATE)) {
                            parser.next();
                            try {
                                Date date = new SimpleDateFormat(RSS_DATE_FORMAT, Locale.US).parse(
                                        parser.getText());
                                articleBuf.setPublishTime(date.getTime());
                            } catch (ParseException e) {
                                Log.i(LOG_TAG, "Error converting date to long. "
                                        + e.getLocalizedMessage());
                                articleBuf.setPublishTime(System.currentTimeMillis());
                            }
                        } else if (parser.getName().equals(RSS_CONTENT)) {
                            // TODO Find first pic or use default one or change theme
                            parser.next();
                            String tmp = parser.getText();
                            articleBuf.setCoverURL(getFirstPicLink(tmp));
                        }
                    }
                    if (articleBuf == null && parser.getName().equals(RSS_ITEM)) {
                        articleBuf = new Article();
                    }
                    break;
                case XmlPullParser.END_TAG:
                    if (parser.getName().equals("item") && articles != null) {
                        articles.add(articleBuf);
                        articleBuf = null;
                    }
                    break;
            }
            eventType = parser.next();
        }
        return articles;
    }

    /**
     * This will look for &lt;img src="...." /&gt; and extract the image's URL.
     * <p/>
     * Done by searching "src="" and locating another quote.
     *
     * @param in Input string.
     * @return Found image URL. NULL if no pic is found or input string is empty.
     */
    private String getFirstPicLink(String in) {
        if (null == in || in.isEmpty()) {
            Log.w(LOG_TAG, "Got an empty string/null in getFirstPicLink");
            return null;
        }
        int firstQuote = in.indexOf("src=\"");
        int nextQuote = in.indexOf("\"", firstQuote + 5);
        if (firstQuote == -1 || nextQuote == -1) {
            // There is no img tag
            return null;
        }
        return in.substring(firstQuote + 5, nextQuote);
    }
}
