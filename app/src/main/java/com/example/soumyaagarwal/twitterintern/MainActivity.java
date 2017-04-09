package com.example.soumyaagarwal.twitterintern;

import android.app.ListActivity;
import android.graphics.Color;
import android.os.Handler;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.tweetui.SearchTimeline;
import com.twitter.sdk.android.tweetui.TimelineResult;
import com.twitter.sdk.android.tweetui.TweetTimelineListAdapter;

import io.fabric.sdk.android.Fabric;

public class MainActivity extends ListActivity implements ConnectivityReceiver.ConnectivityReceiverListener{

    private static final String TWITTER_KEY = "R3UORHTwI5P9deTenIEiWPkMV";
    private static final String TWITTER_SECRET = "PGymPMr9favEUqA8PndISkqMZqzq8jskFBy0h0zJGtdqnz2aXm";

    ProgressBar progress;
    EditText search;
    String searchtweets;
    ImageButton searchtweet;
    SearchTimeline searchTimeline;
    TweetTimelineListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));

        setContentView(R.layout.activity_main);

        progress = (ProgressBar)findViewById(R.id.progress);
        search = (EditText)findViewById(R.id.search);
        searchtweet = (ImageButton)findViewById(R.id.searchtweet);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setVisibility(fab.INVISIBLE);

        boolean isConnected = ConnectivityReceiver.isConnected();
        showSnack(isConnected);

        searchtweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                progress.setVisibility(View.VISIBLE);
                searchtweets = search.getText().toString();
                search();
            }
        });
    }

    void search()
    {
                    searchTimeline = new SearchTimeline.Builder()
                            .query(searchtweets)
                            .build();
                    adapter = new TweetTimelineListAdapter.Builder(getApplication())
                            .setTimeline(searchTimeline)
                            .build();
                    setListAdapter(adapter);

                    final Handler h = new Handler();
                    final int delay = 5000;

                    h.postDelayed(new Runnable(){
                        public void run()
                        {
                            adapter.refresh(new Callback<TimelineResult<Tweet>>() {
                                @Override
                                public void success(Result<TimelineResult<Tweet>> result) {
                                }

                                @Override
                                public void failure(TwitterException exception) {
                                }
                            });
                            h.postDelayed(this, delay);
                        }
                    }, delay);
                }

    private void showSnack(boolean isConnected) {
        String message;
        int color;
        if (isConnected) {
            message = "Good! Connected to Internet";
            color = Color.WHITE;
        } else {
            message = "Sorry! Not connected to internet";
            color = Color.RED;
        }

        Snackbar snackbar = Snackbar
                .make(findViewById(R.id.fab), message, Snackbar.LENGTH_LONG);

        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(color);
        if(color==Color.RED)
        {
            snackbar.setDuration(Snackbar.LENGTH_INDEFINITE);
        }
        snackbar.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MyApplication.getInstance().setConnectivityListener(this);
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        showSnack(isConnected);
    }
}