package com.uoit.calvin.thesis_2016;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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

public class ExploreFragment1 extends Fragment implements TweetsListener{

    View v;

    TransactionDBHelper transactionDBHelper;
    TagDBHelper tagDBHelper;
    Helper helper;
    TweetsListener tweetsListener;
    SharedPreferences sharedPreferences;
    List<Tweet> tweets;
    String followUsername;

    private Context context;


    public ExploreFragment1() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v =  inflater.inflate(R.layout.fragment_explore1, container, false);

        this.context = getContext();

        helper = new Helper(getContext());

        sharedPreferences = getContext().getSharedPreferences(getString(R.string.shared_pref_name_user), Context.MODE_PRIVATE);
        followUsername = sharedPreferences.getString(getString(R.string.shared_pref_arg_follow_user), null);
        if (sharedPreferences.getString(getString(R.string.shared_pref_arg_follow_user), null) != null) {
            new LoadTweets(this, context).execute(sharedPreferences.getString(
                    getString(R.string.shared_pref_arg_follow_user), null) , getString(R.string.explore_activity_title));
            ((ExploreActivity)getActivity()).setSearchTabTitle(
                    sharedPreferences.getString(getString(R.string.shared_pref_arg_follow_user), getString(R.string.explore_frag_title_search)));
        }

        tweetsListener = this;

        tweets = new ArrayList<>();

        Button button_pull = (Button) v.findViewById(R.id.explore1_button_pull);
        if (button_pull != null) {
            button_pull.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new LoadTweets(tweetsListener, context).execute(followUsername, getString(R.string.explore_activity_title));
                    helper.pullTweetsData(tweets, true);
                }
            });
        }



        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.explore_activity_actions, menu);

        MenuItem searchItem = menu.findItem(R.id.explore_action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setQueryHint(getString(R.string.twitter_hint_search));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                followUsername = query;
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(getString(R.string.shared_pref_arg_follow_user), followUsername);
                editor.apply();
                ((ExploreActivity)getActivity()).setSearchTabTitle(
                        sharedPreferences.getString(getString(R.string.shared_pref_arg_follow_user), getString(R.string.explore_frag_title_search)));

                new LoadTweets(tweetsListener, context).execute(query, getString(R.string.explore_activity_title));
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
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

        final LinearLayout layout_tweets = (LinearLayout) v.findViewById(R.id.explore1_layout_tweets);
        if (layout_tweets != null) {
            layout_tweets.removeAllViews();
        }
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


}