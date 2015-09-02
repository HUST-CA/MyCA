package com.hustca.app;

import android.app.Application;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by Hamster on 2015/8/18.
 * <p/>
 * I hate push services. Believe me. I will allow you to disable them.
 */
public class PushApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        JPushInterface.init(this);
    }
}
