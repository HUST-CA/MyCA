package com.hustca.app;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.hustca.app.fragments.SettingsFragment;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by Hamster on 2015/8/18.
 * <p/>
 * JPush's notification system sucks.
 */
public class PMReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(JPushInterface.ACTION_MESSAGE_RECEIVED)) {
            showNotification(context, intent.getExtras());
        }
    }

    @SuppressWarnings("deprecation")
    private void showNotification(Context context, Bundle bundle) {
        // TODO an Article instance may be stored in EXTRA_EXTRA (JSON)
        // Check that and take GOOD use of it
        String title = bundle.getString(JPushInterface.EXTRA_TITLE);
        String msg = bundle.getString(JPushInterface.EXTRA_MESSAGE);

        if (title == null || title.isEmpty()) {
            title = context.getResources().getString(R.string.app_name);
        }

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        boolean isSoundOn = pref.getBoolean(SettingsFragment.KEY_PUSH_SOUND_ENABLED, false);
        boolean isVibOn = pref.getBoolean(SettingsFragment.KEY_PUSH_VIBRATION_ENABLED, false);
        int defaults = 0;
        if (isSoundOn)
            defaults |= Notification.DEFAULT_SOUND;
        if (isVibOn)
            defaults |= Notification.DEFAULT_VIBRATE;

        NotificationManager nm = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification.Builder builder = new Notification.Builder(context);
        builder.setContentTitle(title)
                .setContentText(msg)
                .setDefaults(defaults)
                .setAutoCancel(true)
                .setSmallIcon(R.mipmap.ic_launcher);
        nm.notify(0, builder.getNotification());
    }
}
