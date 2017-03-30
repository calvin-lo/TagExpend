package com.uoit.calvin.thesis_2016;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.tweetui.CompactTweetView;
import com.twitter.sdk.android.tweetui.TweetUtils;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;

import retrofit2.Call;
import retrofit2.Response;

public class ExploreFragment2 extends Fragment implements TweetsListener{

    View v;

    TransactionDBHelper transactionDBHelper;
    TagDBHelper tagDBHelper;
    Helper helper;
    TweetsListener tweetsListener;
    SharedPreferences sharedPreferences;
    List<Tweet> tweets;
    private Context context;

    public ExploreFragment2() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v =  inflater.inflate(R.layout.fragment_explore2, container, false);

        this.context = getContext();

        refresh();

        tweetsListener = this;

        tweets = new ArrayList<>();

        helper = new Helper(getContext());

        Button button_pull = (Button) v.findViewById(R.id.explore2_button_pull);
        if (button_pull != null) {
            button_pull.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                   helper.pullTweetsData(tweets, true);
                }
            });
        }
        Button button_refresh = (Button) v.findViewById(R.id.explore2_button_refresh);
        if (button_refresh != null) {
            button_refresh.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    refresh();
                }
            });
        }

        return v;
    }

    @Override
    public void tweetsCompleted(List<Tweet> tweetsNew){

        tweets = new ArrayList<>();

        if (tweetsNew != null) {
            for (Tweet t : tweetsNew) {
                if (t.text.endsWith(getString(R.string.twitter_tail))) {
                    tweets.add(t);
                }
            }
        }

        final LinearLayout layout_tweets = (LinearLayout) v.findViewById(R.id.explore2_layout_tweets);
        for (Tweet t : tweets) {
            TweetUtils.loadTweet(t.id, new Callback<Tweet>() {
                @Override
                public void success(Result<Tweet> result) {
                    CompactTweetView compactTweetView = new CompactTweetView(context, result.data);
                    if (layout_tweets != null) {
                        layout_tweets.addView(compactTweetView);
                    }
                }

                @Override
                public void failure(TwitterException exception) {

                }
            });

        }
    }

    public void refresh() {

        tweets = new ArrayList<>();

        UserDBHelper userDBHelper = new UserDBHelper(getContext());
        List<User> usersList = userDBHelper.getAllUser();

        for (User u : usersList) {
            new LoadTweets(this, context).execute(u.getUsername(), getString(R.string.explore_activity_title));
        }
        LinearLayout layout_tweets = (LinearLayout) v.findViewById(R.id.explore2_layout_tweets);
        if (layout_tweets != null) {
            layout_tweets.removeAllViews();
        }

        userDBHelper.close();


    }
}