package com.hustca.app.activities;

import android.app.ActivityOptions;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.hustca.app.R;
import com.hustca.app.fragments.HistoryFragment;
import com.hustca.app.fragments.NewsFragment;
import com.hustca.app.fragments.RecentActivitiesFragment;

import cn.jpush.android.api.JPushInterface;


public class MainActivity extends AppCompatActivity {


    private static final String LOG_TAG = "MyCA_MA";

    Fragment mCurrentFragment;
    NavigationView mNavigationView;
    DrawerLayout mDrawerLayout;
    ActionBarDrawerToggle mDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar mToolbar;
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar,
                R.string.open_drawer, R.string.close_drawer);
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        mNavigationView = (NavigationView) findViewById(R.id.drawer_navigation_view);
        setNavigationMenuListener();

    }

    /**
     * Switch current fragment in container (a FrameLayout) to specific one.
     * If current fragment is the same as the wanted, do not perform any operation.
     * Not even new Fragment().
     *
     * @param fragmentType Type of fragment to switch to.
     */
    private void switchToFragment(FragmentType fragmentType) {
        // Prepare the new fragment
        Fragment newFragment;
        switch (fragmentType) {
            case FRAGMENT_RECENT_ACTIVITIES:
                if (mCurrentFragment instanceof RecentActivitiesFragment)
                    return;
                else
                    newFragment = new RecentActivitiesFragment();
                break;
            case FRAGMENT_HISTORY:
                if (mCurrentFragment instanceof HistoryFragment)
                    return;
                else
                    newFragment = new HistoryFragment();
                break;
            case FRAGMENT_NEWS:
                if (mCurrentFragment instanceof NewsFragment)
                    return;
                else
                    newFragment = new NewsFragment();
                break;
            case FRAGMENT_H2O:
                // TODO Implement this frag
                // break;
                return;
            default:
                Log.wtf(LOG_TAG, "switchToFragment: unknown frag type: " + fragmentType);
                return;
        }

        // Do the real transaction
        FragmentTransaction ft = this.getFragmentManager().beginTransaction();
        if (mCurrentFragment != null)
            ft.replace(R.id.fragment_container, newFragment);
        else
            ft.add(R.id.fragment_container, newFragment);
        ft.commit();
        mCurrentFragment = newFragment;
    }

    @Override
    protected void onPause() {
        super.onPause();

        // JPush Again!
        JPushInterface.onPause(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Default fragment to show on boot

        // Remember to set checked property in XML if you want to change this (FOR INITIAL SELECTION)
        // getItem(0) is its index. This will affect the selection on re-launch
        // from multi-task view or something like that.
        mNavigationView.getMenu().getItem(0).setChecked(true);
        switchToFragment(FragmentType.FRAGMENT_RECENT_ACTIVITIES);

        // JPush again!
        JPushInterface.onResume(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setNavigationMenuListener() {
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(final MenuItem menuItem) {
                final int id = menuItem.getItemId();

                menuItem.setChecked(true);

                // Give the drawer 235ms to retract or it may cause glitch even on 3GB RAM with S801AC
                new android.os.Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        FragmentType fragToSwitch = FragmentType.FRAGMENT_RECENT_ACTIVITIES;
                        switch (id) {
                            case R.id.menu_recent_act:
                                fragToSwitch = FragmentType.FRAGMENT_RECENT_ACTIVITIES;
                                break;
                            case R.id.menu_history:
                                fragToSwitch = FragmentType.FRAGMENT_HISTORY;
                                break;
                            case R.id.menu_news:
                                fragToSwitch = FragmentType.FRAGMENT_NEWS;
                                break;
                            case R.id.menu_h2o:
                                fragToSwitch = FragmentType.FRAGMENT_H2O;
                                break;
                    /* Following can't be handled with FragmentType */
                            case R.id.menu_settings:
                                Intent intent = new Intent();
                                intent.setClass(getApplicationContext(), SettingsActivity.class);
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                    startActivity(intent,
                                            ActivityOptions.makeSceneTransitionAnimation(MainActivity.this).toBundle());
                                } else {
                                    startActivity(intent);
                                }
                                break;
                            case R.id.menu_about:
                                // TODO About
                                break;
                            case R.id.menu_switch_org:
                                // TODO
                                break;
                            default:
                                Log.wtf(LOG_TAG, "drawerMenu: unknown menu: " + menuItem.toString());
                        }
                        switchToFragment(fragToSwitch);
                    }
                }, 235);

                mDrawerLayout.closeDrawers();
                return true;
            }
        });
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggles
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    /**
     * An enum for identifying fragments by a single int.
     * This seems a little dirty but I can't think of better ways.
     * Meant for switchToFragment without using /new Fragment()/ as param
     */
    private enum FragmentType {
        FRAGMENT_RECENT_ACTIVITIES,
        FRAGMENT_NEWS,
        FRAGMENT_HISTORY,
        FRAGMENT_H2O
    }
}
