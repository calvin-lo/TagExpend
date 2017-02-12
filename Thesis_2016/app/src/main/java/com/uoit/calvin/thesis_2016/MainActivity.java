package com.uoit.calvin.thesis_2016;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;

import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;

import io.fabric.sdk.android.Fabric;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "PvpXWnRXyy9ggCnhsh26aGOLc";
    private static final String TWITTER_SECRET = "VX0q8UBgeIiT4UT5fzygdfq49xiwWzTOoL0wKxZaGs0sg7Qjfy";

    // List Layout
    private static final int UPDATE = 2;
    private static final int SAVING_DATA = 1;
    private static final int RESULT_OK = 1;

    TransactionDBHelper transDB;
    TagDBHelper tagDB;

    ViewPagerAdapter adapter;

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));
        setContentView(R.layout.activity_main);

        //deleteDatabase("transDB");
        //deleteDatabase("tagCloudDB");

        // Set up the action bar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getResources().getString(R.string.fragment1));

        // Set up the drawer
        //updateDrawer();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        if (drawer != null) {
            drawer.addDrawerListener(toggle);
        }
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) {
            navigationView.setNavigationItemSelectedListener(this);
        }

        // Tab Layout
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        tabLayout = (TabLayout) findViewById(R.id.tabs);

        setupViewPager(viewPager);

        final TabLayout.Tab home = tabLayout.newTab();
        home.setIcon(R.drawable.ic_home_black_24dp);

        final TabLayout.Tab dashboard = tabLayout.newTab();
        dashboard.setIcon(R.drawable.ic_dashboard_black_24dp);

        final TabLayout.Tab chart = tabLayout.newTab();
        chart.setIcon(R.drawable.ic_insert_chart_black_24dp);

        final TabLayout.Tab following = tabLayout.newTab();
        following.setIcon(R.drawable.ic_explore_black_24dp);

        tabLayout.addTab(home, 0);
        tabLayout.addTab(dashboard, 1);
        tabLayout.addTab(chart, 2);
        tabLayout.addTab(following,3);

        tabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.accent));


        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager));

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
              @Override
              public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

              @Override
              public void onPageSelected(int position) {
                  switch (position) {
                      case 0:
                          //home.setIcon(R.drawable.ic_home_black_24dp);
                          //star.setIcon(R.drawable.ic_create_black_24dp);
                          toolbar.setTitle(getResources().getString(R.string.fragment1));
                          break;
                      case 1:
                         //home.setIcon(R.drawable.ic_home_black_24dp);
                          //star.setIcon(R.drawable.ic_clear_black_24dp);
                          toolbar.setTitle(getResources().getString(R.string.fragment2));
                          break;
                      case 2:
                          toolbar.setTitle(getResources().getString(R.string.fragment3));
                          break;
                      case 3:
                          toolbar.setTitle(getResources().getString(R.string.fragment4));
                          break;

                  }
              }

              @Override
              public void onPageScrollStateChanged(int state) {}

          });
    }

    @Override
    protected void onRestart() {
        adapter.notifyDataSetChanged();
        super.onRestart();
    }

    @Override
    public void onBackPressed() {
        adapter.notifyDataSetChanged();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer != null) {
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            } else {
                super.onBackPressed();
            }
        }
    }

    @Override
    public void onResume() {
        adapter.notifyDataSetChanged();
        super.onResume();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.

        int id = item.getItemId();
        if (id == R.id.nav_tag) {
            Intent intent = new Intent(this, TagCloudActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_chart) {
            Intent intent = new Intent(this, ChartActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_trend) {

        }
        else if (id == R.id.nav_setting) {
            Intent intent = new Intent(this, SettingActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer != null) {
            drawer.closeDrawer(GravityCompat.START);
        }
        return true;
    }

    /*
        View Pager
     */
    private void setupViewPager(ViewPager viewPager) {
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new FragmentHome(), getResources().getString(R.string.fragment1));
        adapter.addFragment(new FragmentTagCloud(),getResources().getString(R.string.fragment2));
        adapter.addFragment(new FragmentChart(), getResources().getString(R.string.fragment3));
        adapter.addFragment(new FragmentFollowing(), getResources().getString(R.string.fragment4));
        viewPager.setAdapter(adapter);
    }


    /*
        Setting Menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_activity_actions, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView =
                (SearchView) MenuItemCompat.getActionView(searchItem);
        return super.onCreateOptionsMenu(menu);
    }


} // end of class






