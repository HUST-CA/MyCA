package com.hustca.app;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.hustca.app.fragments.NewsBrowserFragment;


public class NewsBrowserActivity extends Activity {

    private static final String LOG_TAG = "MyCA_NewsBrowserACT";
    /* This boolean is to keep only one frag is shown during multi-tasking */
    private boolean mIsFragmentShown = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_news_browser);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            postponeEnterTransition();
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!mIsFragmentShown) {
            Bundle options = new Bundle(1);
            Article article = getIntent().getParcelableExtra(NewsBrowserFragment.KEY_ARTICLE_BUNDLE);
            if (article == null) {
                Log.e(LOG_TAG, "onResume: article parcel not defined in intent. Returning.");
                return;
            } else {
                options.putParcelable(NewsBrowserFragment.KEY_ARTICLE_BUNDLE, article);
            }

            NewsBrowserFragment frag = new NewsBrowserFragment();
            frag.setArguments(options);

            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.add(R.id.news_browser_container, frag);
            // Do not addToBackStack since we are in a separate new activity. There's
            // no way to go back to previous MainActivity.
            ft.commit();

            mIsFragmentShown = true;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // getMenuInflater().inflate(R.menu.menu_content_browser, menu);
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
}
