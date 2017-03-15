package com.uoit.calvin.thesis_2016;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import io.fabric.sdk.android.Fabric;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "PvpXWnRXyy9ggCnhsh26aGOLc";
    private static final String TWITTER_SECRET = "VX0q8UBgeIiT4UT5fzygdfq49xiwWzTOoL0wKxZaGs0sg7Qjfy";

    ViewPagerAdapter adapter;

    private Toolbar toolbar;
    private ActionBar ab;
    private TabLayout tabLayout;
    private CustomViewPager viewPager;

    private Helper helper;
    NavigationView navigationView;
    SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        helper = new Helper(this);

        sharedpreferences = getSharedPreferences("USER", Context.MODE_PRIVATE);
        if (sharedpreferences.getString("username", null) == null) {
            helper.setUser("*");
            helper.setSelectedPosition(R.id.nav_user);
        }

        if (sharedpreferences.getString("defaultUsername", null) == null) {
            helper.setDefaultUser(getString(R.string.default_user));
        }

        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));
        setContentView(R.layout.activity_main);

        //deleteDatabase("transDB");
        //deleteDatabase("tagCloudDB");
        //deleteDatabase("userDB");

        // Set up the action bar
        toolbar = (Toolbar) findViewById(R.id.mainToolbar);
        setSupportActionBar(toolbar);
        ab = getSupportActionBar();
        toolbar.setTitle(getResources().getString(R.string.fragment1));

        // Set up the drawer
        setupDrawer();

        viewPager = (CustomViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);


        // Tab Layout
        setupTabLayout();

    }

    @Override
    protected void onRestart() {
        setupDrawer();
        setupViewPager(viewPager);
        navigationView.getMenu().findItem(R.id.nav_setting).setChecked(false);
        adapter.notifyDataSetChanged();
        super.onRestart();
    }

    @Override
    public void onBackPressed() {
        setupDrawer();
        setupViewPager(viewPager);
        navigationView.getMenu().findItem(R.id.nav_setting).setChecked(false);
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
        setupViewPager(viewPager);
        super.onResume();
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.

        int id = item.getItemId();

        if (id == R.id.nav_home) {
            viewPager.setCurrentItem(0, true);
        } else if (id == R.id.nav_tag) {
            viewPager.setCurrentItem(1, true);
        }
        else if (id == R.id.nav_chart) {
            viewPager.setCurrentItem(2, true);
        }
        else if (id == R.id.nav_explore) {
            viewPager.setCurrentItem(3, true);
        }
        else if (id == R.id.nav_user) {
            helper.setUser(getResources().getString(R.string.default_user));
            helper.setSelectedPosition(R.id.nav_user);
            viewPager.setCurrentItem(0, true);
            adapter.notifyDataSetChanged();
        } else if (id == R.id.nav_all) {
            helper.setUser("*");
            helper.setSelectedPosition(R.id.nav_all);
            viewPager.setCurrentItem(0, true);
            adapter.notifyDataSetChanged();
        }
        else if (id == R.id.nav_user_more) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            TransactionDBHelper transactionDBHelper = new TransactionDBHelper(this);
            final String[] followingList = transactionDBHelper.getUser();

            builder.setTitle(R.string.dialog_title)
                    .setItems(followingList, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            helper.setUser(followingList[which]);
                            helper.setSelectedPosition(R.id.nav_user_more);
                            adapter.notifyDataSetChanged();
                        }
                    });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
        else if (id == R.id.nav_user1) {;
            helper.setUser(item.getTitle().toString());
            helper.setSelectedPosition(R.id.nav_user1);
            viewPager.setCurrentItem(0, true);
            adapter.notifyDataSetChanged();
        }
        else if (id == R.id.nav_user2) {
            helper.setUser(item.getTitle().toString());
            helper.setSelectedPosition(R.id.nav_user2);
            viewPager.setCurrentItem(0, true);
            adapter.notifyDataSetChanged();
        }
        else if (id == R.id.nav_user3) {
            helper.setUser(item.getTitle().toString());
            helper.setSelectedPosition(R.id.nav_user3);
            viewPager.setCurrentItem(0, true);
            adapter.notifyDataSetChanged();
        }
        else if (id == R.id.nav_user4) {
            helper.setUser(item.getTitle().toString());
            helper.setSelectedPosition(R.id.nav_user4);
            viewPager.setCurrentItem(0, true);
            adapter.notifyDataSetChanged();
        }
        else if (id == R.id.nav_user5) {
            helper.setUser(item.getTitle().toString());
            helper.setSelectedPosition(R.id.nav_user5);
            viewPager.setCurrentItem(0, true);
            adapter.notifyDataSetChanged();
        }
        else if (id == R.id.nav_setting) {
            Intent intent = new Intent(this, SettingActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer != null) {
            drawer.closeDrawer(GravityCompat.START);
        }

        setToolbar();

        return true;
    }

    /*
        View Pager
     */
    private void setupViewPager(CustomViewPager viewPager) {
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new FragmentHome(), getResources().getString(R.string.fragment1));
        adapter.addFragment(new FragmentTagCloud(),getResources().getString(R.string.fragment2));
        adapter.addFragment(new FragmentChart(), getResources().getString(R.string.fragment3));
        adapter.addFragment(new FragmentFollowing(), getResources().getString(R.string.fragment4));
        viewPager.setAdapter(adapter);
        viewPager.setPagingEnabled(true);

        setToolbar();
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                String username = sharedpreferences.getString("username", getString(R.string.default_user));
                switch (position) {
                    case 0:
                        if (username.equals("*")) {
                            toolbar.setTitle(getResources().getString(R.string.fragment1));
                        } else {
                            setToolbar();
                            TransactionDBHelper transactionDBHelper = new TransactionDBHelper(getApplicationContext());
                            int count = transactionDBHelper.getAllData(username).size();
                            String s = count + " transactions";
                            toolbar.setSubtitle(s);
                        }
                        break;
                    case 1:
                        if (username.equals("*")) {
                            toolbar.setTitle(getResources().getString(R.string.fragment2));
                        } else {
                            setToolbar();
                            TagDBHelper tagDBHelper = new TagDBHelper(getApplicationContext());
                            int count = tagDBHelper.getTagsList("*", username).size();
                            String s = count + " tags";
                            toolbar.setSubtitle(s);
                        }
                        break;
                    case 2:
                        if (username.equals("*")) {
                            toolbar.setTitle(getResources().getString(R.string.fragment3));
                        } else {
                            setToolbar();
                            TagDBHelper tagDBHelper = new TagDBHelper(getApplicationContext());
                            int count = tagDBHelper.getTagsList("*", username).size();
                            String s = count + " tags";
                            toolbar.setSubtitle(s);
                        }
                        break;
                    case 3:
                        toolbar.setTitle(sharedpreferences.getString("followUsername", getResources().getString(R.string.fragment4)));
                        toolbar.setSubtitle("");
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    /*
        Set up Tab Layout
     */

    private void setupTabLayout() {
        tabLayout = (TabLayout) findViewById(R.id.tabs);

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
    }

    /*
        Set up Drawer
     */

    public void setupDrawer() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        if (drawer != null) {
            drawer.addDrawerListener(toggle);
        }
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);

        TransactionDBHelper transactionDBHelper = new TransactionDBHelper(this);
        String[] followingList = transactionDBHelper.getUser();
        int[] navUserID = {R.id.nav_user1, R.id.nav_user2, R.id.nav_user3, R.id.nav_user4, R.id.nav_user5};

        View header = navigationView.getHeaderView(0);

        TextView title = (TextView) header.findViewById(R.id.headerTitle);
        title.setText(sharedpreferences.getString("defaultUsername", getResources().getString(R.string.default_user)));

        if (navigationView != null) {
            navigationView.getMenu().getItem(0).setTitle(sharedpreferences.getString("defaultUsername", getResources().getString(R.string.default_user)));
            int i = 0;
            for (String s : followingList) {
                if (i > 5) {
                    navigationView.getMenu().findItem(R.id.nav_user_more).setVisible(true);
                    break;
                }
                else {
                    navigationView.getMenu().findItem(navUserID[i]).setTitle(followingList[0]);
                    navigationView.getMenu().findItem(navUserID[i]).setVisible(true);
                    i++;
                }
            }
            navigationView.setNavigationItemSelectedListener(this);
            //navigationView.getMenu().findItem(sharedpreferences.getInt("selectedPosition", 0)).setChecked(true);
        }

    }

    /*
        Setting Menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /*getMenuInflater().inflate(R.menu.main_activity_actions, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        return super.onCreateOptionsMenu(menu);
        */
        return false;
    }

    public void setToolbarTitle(String title) {
        toolbar.setTitle(title);
    }

    public void setToolbar() {
        String username = sharedpreferences.getString("username", getString(R.string.default_user));
        UserDBHelper userDBHelper = new UserDBHelper(getApplicationContext());
        String displayName = userDBHelper.getUserNyUsername(username).getDisplayName();
        if (!username.equals("*")) {
            toolbar.setTitle(displayName);
            toolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.tw__transparent));
            TransactionDBHelper transactionDBHelper = new TransactionDBHelper(getApplicationContext());
            int count = transactionDBHelper.getAllData(username).size();
            String s = count + " transactions";
            toolbar.setSubtitle(s);
            if (ab != null) {
                ab.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.side_toolbar));
            }
        } else {
            toolbar.setTitle(getString(R.string.fragment1));
            toolbar.setSubtitle("");
            if (ab != null) {
                toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.primaryText));
                ab.setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(this, R.color.actionBarColor)));
            }
        }
    }

} // end of class






