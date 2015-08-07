package com.hustca.app;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.hustca.app.fragments.HistoryFragment;
import com.hustca.app.fragments.NewsFragment;
import com.hustca.app.fragments.RecentActivitiesFragment;


public class MainActivity extends AppCompatActivity {


    private static final String LOG_TAG = "MyCA_MA";

    Fragment mCurrentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar mToolbar;
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

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
    public void onResume() {
        super.onResume();
        // Default fragment to show on boot
        // Remember to set checked property in XML if you want to change this
        switchToFragment(FragmentType.FRAGMENT_RECENT_ACTIVITIES);
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
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_drawer);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                int id = menuItem.getItemId();
                FragmentType fragToSwitch;
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
                        // TODO Settings activity
                        menuItem.setChecked(true);
                        return true;
                    case R.id.menu_about:
                        // TODO About
                        menuItem.setChecked(true);
                        return true;
                    case R.id.menu_switch_org:
                        // TODO
                        menuItem.setChecked(true);
                        return true;
                    default:
                        Log.wtf(LOG_TAG, "drawerMenu: unknown menu: " + menuItem.toString());
                        return true;
                }
                switchToFragment(fragToSwitch);
                menuItem.setChecked(true);
                return true;
            }
        });
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
