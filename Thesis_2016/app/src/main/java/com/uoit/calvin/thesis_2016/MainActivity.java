package com.uoit.calvin.thesis_2016;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.daimajia.swipe.util.Attributes;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;

import java.util.ArrayList;
import java.util.List;

import io.fabric.sdk.android.Fabric;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "PvpXWnRXyy9ggCnhsh26aGOLc";
    private static final String TWITTER_SECRET = "VX0q8UBgeIiT4UT5fzygdfq49xiwWzTOoL0wKxZaGs0sg7Qjfy";

    ViewPagerAdapter adapter;

    ListViewAdapter arrayAdapter;

    // List Layout
    private static final int DETAILS = 3;
    private static final int UPDATE = 2;
    private static final int SAVING_DATA = 1;
    private static final int RESULT_OK = 1;

    TransactionDBHelper transDB;
    TagDBHelper tagDB;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));
        setContentView(R.layout.activity_main);

        //deleteDatabase("transDB");
        //deleteDatabase("tagCloudDB");

        // Set up the action bar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Set up the drawer
        updateDrawer();
        //setupTabLayout();

        /**
         * Databases
         */
        displayTransList();



    }

    @Override
    protected void onRestart() {
        super.onRestart();
        //setupTabLayout();
        updateDrawer();
        arrayAdapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        //setupTabLayout();
        updateDrawer();
        arrayAdapter.notifyDataSetChanged();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        arrayAdapter.notifyDataSetChanged();
        displayTransList();
    }

    public void create(View v) {
        Intent intent = new Intent(getApplicationContext(), FormActivity.class);
        startActivityForResult(intent, SAVING_DATA);
    }

    /*
        Tab Layout
     */
    private void setupViewPager(ViewPager viewPager) {
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new PrimaryFragment(), getResources().getString(R.string.fragOne));
        //adapter.addFragment(new SecondFragment(), getResources().getString((R.string.fragTwo)));
        viewPager.setAdapter(adapter);
    }

    public void setupTabLayout() {
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);
/*
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        if (tabLayout != null) {
            tabLayout.setupWithViewPager(viewPager);
            tabLayout.getTabAt(0).setIcon(R.drawable.ic_dns_white_24dp);
            tabLayout.getTabAt(1).setIcon(R.drawable.ic_donut_small_black_24dp);

            tabLayout.setOnTabSelectedListener(
                    new TabLayout.ViewPagerOnTabSelectedListener(viewPager) {
                        @Override
                        public void onTabSelected(TabLayout.Tab tab) {
                            super.onTabSelected(tab);
                            switch (tab.getPosition()) {
                                case 0:
                                    tab.setIcon(R.drawable.ic_dns_white_24dp);
                                    break;
                                case 1:
                                    tab.setIcon(R.drawable.ic_donut_small_white_24dp);
                                    break;
                            }
                        }

                        @Override
                        public void onTabUnselected(TabLayout.Tab tab) {
                            super.onTabUnselected(tab);
                            switch (tab.getPosition()) {
                                case 0:
                                    tab.setIcon(R.drawable.ic_dns_black_24dp);
                                    break;
                                case 1:
                                    tab.setIcon(R.drawable.ic_donut_small_black_24dp);
                                    break;
                            }
                        }

                        @Override
                        public void onTabReselected(TabLayout.Tab tab) {
                            super.onTabReselected(tab);
                        }
                    }
            );
        }
*/
    }
    /*
        Drawer
     */
    public void updateDrawer() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

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
            Menu menu = navigationView.getMenu();
            menu.clear();
            for (String s : new TagDBHelper(this).getTagsStringList()) {
                menu.add(s);
            }
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.

        String tag = item.getTitle().toString();
        Intent intent = new Intent(this, TagActivity.class);
        // symbol ☆
        intent.putExtra("tag", tag);
        startActivity(intent);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /*
        Setting Menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.setting:
                Intent intent = new Intent(this, SettingActivity.class);
                startActivity(intent);
                adapter.notifyDataSetChanged();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // List Layout

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case SAVING_DATA:
                    // add the transaction
                    transDB = new TransactionDBHelper(getApplicationContext());
                    Helper helper = new Helper();

                    String input = data.getStringExtra("trans");
                    List<Tag> tagList = helper.parseTag(input);

                    Transaction trans = new Transaction();
                    trans.setTags(tagList);
                    trans.setTimestamp(helper.getCurrentTime());
                    trans.setAmout(helper.getAmount(input));
                    transDB.addTransactions(trans);

                    // add the tag to tag cloud
                    tagDB = new TagDBHelper(getApplicationContext());
                    for (Tag t : tagList) {
                        tagDB.addTag(t);
                    }

                    transDB.close();
                    tagDB.close();
                    break;
                case UPDATE:
                    break;

            }
        }
        updateDrawer();
        displayTransList();
    }

    public void displayTransList() {
        transDB = new TransactionDBHelper(getApplicationContext());

        List<Transaction> transList = transDB.getAllData();
        List<Long> transIds = new ArrayList<>();
        for (Transaction t : transList) {
            transIds.add(t.getId());
        }

        ArrayList<Transaction> myDataset = new ArrayList<>(transList);

        // Set the transaction
        arrayAdapter = new ListViewAdapter(getApplicationContext(), myDataset);
        ListView transListView = (ListView) findViewById(R.id.transactionList);
        transListView.setAdapter(arrayAdapter);
        registerForContextMenu(transListView);

  /*      // Set the ID
        ArrayAdapter arrayAdapterID = new ArrayAdapter<>(getApplicationContext(), R.layout.activity_listview, transIds);
        ListView idList = (ListView) findViewById(R.id.transactionListID);
        idList.setAdapter(arrayAdapterID);
        registerForContextMenu(idList);

       */

        transDB.close();
    }

    // End of List Layout



} // end of class






