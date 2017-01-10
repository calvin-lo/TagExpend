package com.uoit.calvin.thesis_2016;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import io.fabric.sdk.android.Fabric;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "PvpXWnRXyy9ggCnhsh26aGOLc";
    private static final String TWITTER_SECRET = "VX0q8UBgeIiT4UT5fzygdfq49xiwWzTOoL0wKxZaGs0sg7Qjfy";

    // List Layout
    private static final int DETAILS = 3;
    private static final int UPDATE = 2;
    private static final int SAVING_DATA = 1;
    private static final int RESULT_OK = 1;

    TransactionDBHelper transDB;
    TagDBHelper tagDB;

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

        /**
         * Databases
         */
        displayTransList();



    }

    @Override
    protected void onRestart() {
        super.onRestart();
        //updateDrawer();
    }

    @Override
    public void onBackPressed() {
        //updateDrawer();
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
        super.onResume();
        displayTransList();
    }

    public void create(View v) {
        Intent intent = new Intent(getApplicationContext(), FormActivity.class);
        startActivityForResult(intent, SAVING_DATA);
    }

    public void create() {
        Intent intent = new Intent(getApplicationContext(), FormActivity.class);
        startActivityForResult(intent, SAVING_DATA);
    }

    /*
        Drawer
     */
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
        } else if (id == R.id.nav_setting) {
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
        displayTransList();
    }

    public void displayTransList() {
        transDB = new TransactionDBHelper(getApplicationContext());

        List<Transaction> transList = transDB.getAllData();
        List<List<Transaction>> myList = new ArrayList<>();

        //List<MyDate> uniqueDate = new ArrayList<>();
        List<Date> uniqueDate = new ArrayList<>();

        int todayPosition = -1;
        int index = 0;
        Helper helper = new Helper();
        //MyDate todayDate = helper.timeToMyDate(helper.getCurrentTime());
        Date todayDate = helper.timeToDate(helper.getCurrentTime());


        for (Transaction t : transList) {
            boolean dup = false;
            for (Date date : uniqueDate) {
                if (helper.getYear(t.getDate())== helper.getYear(date)
                        && helper.getMonth(t.getDate()) == helper.getMonth(date)
                        && helper.getDay(t.getDate()) == helper.getDay(date)) {
                    dup = true;
                }
            }
            if (!dup) {
                uniqueDate.add(t.getDate());
            }
        }

        boolean haveToday = false;
        for (Transaction t : transList) {
            if (helper.getYear(t.getDate())== helper.getYear(todayDate)
                    && helper.getMonth(t.getDate()) == helper.getMonth(todayDate)
                    && helper.getDay(t.getDate()) == helper.getDay(todayDate)) {
                haveToday = true;
            }
        }
        if (!haveToday) {
            uniqueDate.add(todayDate);
        }


        Collections.sort(transList);
        Collections.reverse(transList);
        Collections.sort(uniqueDate);
        Collections.reverse(uniqueDate);
        for (Date date : uniqueDate) {
            List<Transaction> temp = new ArrayList<>();
            for (Transaction t : transList) {
                if (helper.getYear(t.getDate())== helper.getYear(date)
                        && helper.getMonth(t.getDate()) == helper.getMonth(date)
                        && helper.getDay(t.getDate()) == helper.getDay(date)) {
                    temp.add(t);
                }
            }
            myList.add(temp);
        }

        for (int i = 0; i < uniqueDate.size(); i++) {
            if (helper.getYear(uniqueDate.get(i)) == helper.getYear(todayDate)
                    && helper.getMonth(uniqueDate.get(i))  == helper.getMonth(todayDate)
                    && helper.getDay(uniqueDate.get(i))  == helper.getDay(todayDate)) {
                todayPosition = i;
            }
        }

        if (myList.size() > 0) {
            MainListViewAdapter mainAdapter = new MainListViewAdapter(getApplicationContext(), myList, uniqueDate, todayPosition);
            ListView transListView = (ListView) findViewById(R.id.transactionList);
            if (transListView != null) {
                transListView.setAdapter(mainAdapter);
                transListView.setSelectionFromTop(todayPosition,0);
            }
            registerForContextMenu(transListView);
        }

        transDB.close();
    }

    // End of List Layout


} // end of class






