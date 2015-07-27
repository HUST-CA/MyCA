package com.hustca.app;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.hustca.app.fragments.RecentActivitiesFragment;


public class MainActivity extends AppCompatActivity implements RefreshIndicator {

    private static final String LOG_TAG = "MyCA_MA";
    SwipeRefreshLayout mSwipeRefreshLayout;
    Fragment mCurrentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar mToolbar;
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_to_refresh_container);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (mCurrentFragment != null && mCurrentFragment instanceof Refreshable) {
                    ((Refreshable) mCurrentFragment).refresh();
                } else {
                    Log.e(LOG_TAG, "SwipeToRefresh: current fragment does not exist or can not refresh.");
                }
            }
        });

    }

    private void switchToFragment(Fragment newFragment) {
        FragmentTransaction ft = this.getFragmentManager().beginTransaction();
        if (mCurrentFragment != null)
            ft.replace(R.id.swipe_to_refresh_container, newFragment);
        else
            ft.add(R.id.swipe_to_refresh_container, newFragment);
        ft.commit();
        mCurrentFragment = newFragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        switchToFragment(new RecentActivitiesFragment());
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

    @Override
    public void onRefreshStopped() {
        if (mSwipeRefreshLayout != null)
            mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onRefreshStarted() {
        if (mSwipeRefreshLayout != null)
            mSwipeRefreshLayout.setRefreshing(true);
    }
}
