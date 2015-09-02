package com.hustca.app.fragments;

import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import com.hustca.app.PMReceiver;
import com.hustca.app.R;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by Hamster on 2015/8/18.
 * <p/>
 * Settings fragment. Used in pref_push activity (yet another container)
 */
public class SettingsFragment extends PreferenceFragment {
    public static final String KEY_PUSH_ENABLED = "push_enabled";
    public static final String KEY_PUSH_SOUND_ENABLED = "push_sound_enabled";
    public static final String KEY_PUSH_VIBRATION_ENABLED = "push_vibration_enabled";

    private Context mContext;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_push);

        mContext = this.getActivity();

        Preference mPrefPushEnabled = findPreference(KEY_PUSH_ENABLED);

        mPrefPushEnabled.setDefaultValue(!JPushInterface.isPushStopped(mContext));
        mPrefPushEnabled.setOnPreferenceChangeListener(
                new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, final Object newValue) {
                        // This interface can't be called from another thread (Handler)
                        if ((boolean) newValue) {
                            JPushInterface.resumePush(mContext);
                        } else {
                            JPushInterface.stopPush(mContext);

                            // Clear all notifications, especially those "Missing stats code"
                            NotificationManager nm = (NotificationManager)
                                    mContext.getSystemService(Context.NOTIFICATION_SERVICE);
                            nm.cancelAll();
                        }

                        // Disable services and receivers as well - I have OBD
                        // And this is a bit time-consuming, do it in another thread
                        new android.os.Handler().post(new Runnable() {
                            @Override
                            public void run() {
                                setPushServicesEnabled((boolean) newValue);
                            }
                        });
                        return true;
                    }
                });
    }

    private void setPushServicesEnabled(boolean doYouLikeMe) {
        PackageManager pm = mContext.getPackageManager();
        ComponentName pushServices[] = new ComponentName[]{
                new ComponentName(mContext, PMReceiver.class),
                new ComponentName(mContext, "cn.jpush.android.service.PushService"),
                new ComponentName(mContext, "cn.jpush.android.service.DaemonService"),
                new ComponentName(mContext, "cn.jpush.android.service.PushReceiver"),
                new ComponentName(mContext, "cn.jpush.android.ui.PushActivity"),
                new ComponentName(mContext, "cn.jpush.android.service.DownloadService")
        };

        int state = doYouLikeMe ?
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED :
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED;

        for (ComponentName service : pushServices) {
            pm.setComponentEnabledSetting(service,
                    state,
                    PackageManager.DONT_KILL_APP);
        }
    }
}
