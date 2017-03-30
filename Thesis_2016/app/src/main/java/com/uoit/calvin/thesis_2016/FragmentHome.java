package com.uoit.calvin.thesis_2016;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.tweetui.CompactTweetView;
import com.twitter.sdk.android.tweetui.TweetUtils;

import java.util.ArrayList;
import java.util.List;

public class FragmentHome extends Fragment implements TweetsListener{

    String username;
    View v;
    List<Tweet> tweets;
    TweetsListener tweetsListener;


    public FragmentHome() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v =  inflater.inflate(R.layout.fragment_home, container, false);

        final SharedPreferences sharedPreferences = getActivity().getSharedPreferences(getString(R.string.shared_pref_name_user), Context.MODE_PRIVATE);
        username = sharedPreferences.getString(getString(R.string.shared_pref_arg_username), null);

        tweetsListener = this;

        final int year = ((MainActivity)getActivity()).getYear();
        final int month = ((MainActivity)getActivity()).getMonth();

        final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.home_swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                if (!username.equals(getString(R.string.user_default))) {
                    if (username.equals(getString(R.string.icon_all))) {
                        UserDBHelper userDBHelper = new UserDBHelper(getContext());
                        List<User> usersList = userDBHelper.getAllUser();
                        for (User u : usersList) {
                            new LoadTweets(tweetsListener, getContext()).execute(u.getUsername(), getString(R.string.helper_pull_all));
                        }
                        userDBHelper.close();
                    } else {
                        new LoadTweets(tweetsListener, getContext()).execute(sharedPreferences.getString(
                                getString(R.string.shared_pref_arg_username), null), getString(R.string.helper_pull_all));
                    }

                    updateData();
                }

                swipeRefreshLayout.setRefreshing(false);
                new Helper(getContext()).displayTransList(v, getActivity(), year , month);

            }
        });

        new Helper(getContext()).displayTransList(v, getActivity(), year, month);
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
    }


    public void updateData() {
        Helper helper = new Helper(getContext());
        helper.pullTweetsData(tweets, true);
    }




}