package com.uoit.calvin.thesis_2016;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

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
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Response;

public class FragmentFollowing extends Fragment implements TweetsListener {

    View v;
    TransactionDBHelper transDB;
    TagDBHelper tagDB;
    Helper helper;
    String followUser;
    TweetsListener tweetsListener;

    public FragmentFollowing() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_following, container, false);
        helper = new Helper(v.getContext());
        tweetsListener = this;
        setHasOptionsMenu(true);

        return v;
    }


    /*
        Search
     */

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.main_activity_actions, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                followUser = query;
                new LoadTweets(tweetsListener).execute(query);
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

        List<Tweet> tweets = new ArrayList<>();

        if (tweetsNew != null) {

            helper.setUser(tweetsNew.get(0).user.name);

            transDB = new TransactionDBHelper(v.getContext().getApplicationContext());
            transDB.clearUser(tweetsNew.get(0).user.name);
            tagDB = new TagDBHelper(v.getContext().getApplicationContext());
            tagDB.clearUser(tweetsNew.get(0).user.name);

            for (Tweet t : tweetsNew) {
                if (t.text.endsWith("- #MyMoneyTag")) {
                    tweets.add(t);

                    String message = t.text.replace("- #MyMoneyTag", "");

                    Transaction trans = new Transaction(getContext());
                    trans.setMessage(message);
                    trans.setTags(helper.parseTag(message));
                    trans.setGeneral(helper.parseGeneral(message));
                    trans.setLocation(helper.parseLocation(message));
                    trans.setCategory(helper.parseCategory(message));
                    String pattern = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
                    SimpleDateFormat format = new SimpleDateFormat(pattern, Locale.CANADA);
                    String time = helper.getCurrentTime();
                    try {
                        Date date = format.parse(t.createdAt);
                        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.CANADA);
                        time = dateFormat.format(date);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    trans.setTimestamp(time);
                    trans.setAmount(helper.getAmount(message));
                    trans.setUser(t.user.name);
                    transDB.addTransactions(trans);

                    // add the tag to tag cloud
                    for (Tag tag : trans.getTagsList()) {
                        tag.setUser(t.user.name);
                        tagDB.addTag(tag);
                    }

                    transDB.close();
                    tagDB.close();
                }
            }
        }

        final LinearLayout myLayout = (LinearLayout) v.findViewById(R.id.tweet_layout);
        myLayout.removeAllViews();
        for (Tweet t : tweets) {
            TweetUtils.loadTweet(t.id, new Callback<Tweet>() {
                @Override
                public void success(Result<Tweet> result) {
                    CompactTweetView compactTweetView = new CompactTweetView(getActivity(), result.data);
                    myLayout.addView(compactTweetView);
                }

                @Override
                public void failure(TwitterException exception) {

                }
            });

        }


    }

    class LoadTweets extends AsyncTask<String, Void, List<Tweet>> {

        private final TweetsListener listener;
        List<Tweet> tweets = new ArrayList<>();

        LoadTweets(TweetsListener listener) {
            this.listener = listener;
        }
        @Override
        protected List<Tweet> doInBackground(String... params) {
            Call<List<Tweet>> tweetsCall = TwitterCore.getInstance().getApiClient().getStatusesService().userTimeline(null, params[0] , null,null,null,null,null,null,null);
            Response<List<Tweet>> responses = null;
            try {
                responses = tweetsCall.execute();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (responses != null) {
                tweets = responses.body();
            }

            return tweets;
        }

        @Override
        protected void onPostExecute(List<Tweet> tweets) {
            if (listener != null) {
                listener.tweetsCompleted(tweets);
            }
        }

    }





}