package com.uoit.calvin.thesis_2016;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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

public class ExploreActivity extends AppCompatActivity{


    ViewPagerAdapter adapter;
    private CustomViewPager viewPager;
    private TabLayout tabLayout;
    Toolbar toolbar;
    TabLayout.Tab tab_search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore);

        toolbar = (Toolbar) findViewById(R.id.explore_toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }

        viewPager = (CustomViewPager) findViewById(R.id.explore_viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.explore_tabs);
        setupTabLayout();


    }


    private void setupViewPager(CustomViewPager viewPager) {
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new ExploreFragment1(), getResources().getString(R.string.explore_frag_title_search));
        adapter.addFragment(new ExploreFragment2(), getResources().getString(R.string.explore_frag_title_following));
        viewPager.setAdapter(adapter);
        viewPager.setPagingEnabled(true);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.shared_pref_name_user), Context.MODE_PRIVATE);
                        setToolbar(sharedPreferences.getString(getString(R.string.shared_pref_arg_follow_user), getString(R.string.explore_activity_title)));
                    case 1:
                        setToolbar(getString(R.string.explore_activity_title));

                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    private void setupTabLayout() {

        tab_search = tabLayout.newTab();
        tab_search.setText(getString(R.string.explore_frag_title_search));

        TabLayout.Tab tab_following = tabLayout.newTab();
        tab_following.setText(getString(R.string.explore_frag_title_following));

        tabLayout.addTab(tab_search, 0);
        tabLayout.addTab(tab_following, 1);

        tabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.accent));

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager));
    }

    public void setToolbar(String title) {
        toolbar.setTitle(title);
    }

    public void setSearchTabTitle(String title) {
        tab_search.setText(title);
    }

}
