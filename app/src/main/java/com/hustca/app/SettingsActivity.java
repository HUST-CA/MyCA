package com.hustca.app;

import android.app.FragmentTransaction;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Explode;
import android.view.Window;

import com.hustca.app.fragments.SettingsFragment;

/**
 * Created by Hamster on 2015/8/18.
 * <p/>
 * Settings for push service. As I've mentioned in {@link PushApplication} I will do this.
 * <p/>
 * NOTE: AppCompat does not support PreferenceActivity so we have to use PrefFrag here.
 */
public class SettingsActivity extends AppCompatActivity {
    private boolean mIsFragmentShown;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
            getWindow().setEnterTransition(new Explode());
            getWindow().setReturnTransition(new Explode());
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = (Toolbar) findViewById(R.id.settings_toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Make a white arrow
            Drawable arrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
            // AS suggested this if
            if (arrow != null) {
                arrow.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
            }
            actionBar.setHomeAsUpIndicator(arrow);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!mIsFragmentShown) {
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.pref_fragment_container, new SettingsFragment());
            ft.commit();
            mIsFragmentShown = true;
        }
    }
}
