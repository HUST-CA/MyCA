package com.hustca.app.fragments;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.hustca.app.R;

/**
 * Created by Hamster on 2015/8/18.
 * <p/>
 * Settings fragment. Used in pref_push activity (yet another container)
 */
public class SettingsFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_push);
    }
}
