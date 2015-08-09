package com.hustca.app;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.hustca.app.fragments.NewsBrowserFragment;


public class NewsBrowserActivity extends AppCompatActivity {

    private static final String LOG_TAG = "MyCA_NewsBrowserACT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_browser);
    }

    @Override
    protected void onResume() {
        super.onResume();

        Bundle options = new Bundle(1);
        int articleId = getIntent().getIntExtra(NewsBrowserFragment.KEY_ARTICLE_BUNDLE, -1);
        if (articleId == -1) {
            Log.e(LOG_TAG, "onResume: article ID not defined in intent. Returning.");
            return;
        } else {
            options.putInt(NewsBrowserFragment.KEY_ARTICLE_BUNDLE, articleId);
        }

        NewsBrowserFragment frag = new NewsBrowserFragment();
        frag.setArguments(options);

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.add(R.id.news_browser_container, frag);
        // Do not addToBackStack since we are in a separate new activity. There's
        // no way to go back to previous MainActivity.
        ft.commit();
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
