package com.uoit.calvin.thesis_2016;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;

import retrofit2.Call;
import retrofit2.Response;

public class FragmentFollowing extends Fragment implements TweetsListener {

    View v;
    TransactionDBHelper transDB;
    TagDBHelper tagDB;
    Helper helper;
    TweetsListener tweetsListener;
    SharedPreferences sharedPreferences;
    List<Tweet> tweets;

    String followUser;

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

        sharedPreferences = getContext().getSharedPreferences("USER", Context.MODE_PRIVATE);
        followUser = sharedPreferences.getString("followUser", "");
        if (sharedPreferences.getString("followUser", null) != null) {
            new LoadTweets(this).execute(sharedPreferences.getString("followUser", null));
        }

        tweetsListener = this;
        setHasOptionsMenu(true);

        tweets = new ArrayList<>();

        Button pullButton = (Button) v.findViewById(R.id.pullButton);
        pullButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pullData();
                ((MainActivity)getActivity()).setToolbarTitle(sharedPreferences.getString("followUser", v.getResources().getString(R.string.fragment4)));

            }
        });


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
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("followUser", followUser);
                editor.apply();

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

        tweets = new ArrayList<>();

        if (tweetsNew != null) {
            for (Tweet t : tweetsNew) {
                if (t.text.endsWith("- #MyMoneyTag")) {
                    tweets.add(t);
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

    public void pullData() {

        if (tweets != null) {
            if (tweets.size() > 0) {

                transDB = new TransactionDBHelper(v.getContext().getApplicationContext());
                transDB.clearUser(tweets.get(0).user.screenName);
                tagDB = new TagDBHelper(v.getContext().getApplicationContext());
                tagDB.clearUser(tweets.get(0).user.screenName);

                for (Tweet t : tweets) {
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
                    int color[] = helper.getColorArray();
                    int randomNum = ThreadLocalRandom.current().nextInt(0, color.length);
                    trans.setColor(color[randomNum]);
                    try {
                        Date date = format.parse(t.createdAt);
                        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.CANADA);
                        time = dateFormat.format(date);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    trans.setTimestamp(time);
                    trans.setAmount(helper.getAmount(message));
                    trans.setUser(t.user.screenName);
                    trans.setName(t.user.name);
                    transDB.addTransactions(trans);

                    // add the tag to tag cloud
                    for (Tag tag : trans.getTagsList()) {
                        tag.setName(t.user.name);
                        tag.setUser(t.user.screenName);
                        tagDB.addTag(tag);
                    }

                    transDB.close();
                    tagDB.close();
                }
            }
            ((MainActivity) getActivity()).setupDrawer();
        }
    }

    private class LoadTweets extends AsyncTask<String, Void, List<Tweet>> {

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