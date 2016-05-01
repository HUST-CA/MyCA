package com.hustca.app.fragments;

/**
 * Created by hamster on 16/5/1.
 * <p/>
 * BBS fragment implementation. Just a extension of PlainBrowserFragment.
 */
public class BBSFragment extends PlainBrowserFragment {
    private static final String URL = "http://wsq.discuz.qq.com/?c=index&a=index&f=wx&fid=2&siteid=265482436";

    @Override
    protected String getURL() {
        return URL;
    }
}
