package com.hustca.app;

import android.app.Application;
import android.app.Notification;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.hustca.app.fragments.SettingsFragment;

import cn.jpush.android.api.BasicPushNotificationBuilder;
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

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        BasicPushNotificationBuilder builder = new BasicPushNotificationBuilder(this);

        builder.notificationFlags = 0; // Don't allow anything at the start

        if (preferences.getBoolean(SettingsFragment.KEY_PUSH_SOUND_ENABLED, false)) {
            builder.notificationFlags |= Notification.DEFAULT_SOUND;
        }

        if (preferences.getBoolean(SettingsFragment.KEY_PUSH_VIBRATION_ENABLED, false)) {
            builder.notificationFlags |= Notification.DEFAULT_VIBRATE;
        }

        // If it's stopped by Settings Activity, it won't be started here
        // You must start in the Settings Activity

        JPushInterface.setDebugMode(true);
        JPushInterface.init(this);
        JPushInterface.setDefaultPushNotificationBuilder(builder);
    }
}
