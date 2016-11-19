package com.uoit.calvin.thesis2;

import android.support.v4.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    DrawerLayout mDrawerLayout;
    ViewPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //deleteDatabase("tagCloudDB");

        // Set up the action bar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Set up the drawer
        updateDrawer();

        ListView mDrawerList;
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        if (mDrawerList != null) {
            mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String tag = ((TextView)view).getText().toString();
                    Intent intent = new Intent( view.getContext(), TagActivity.class);
                    intent.putExtra("tag", tag );
                    startActivity(intent);
                }
            });
        }

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

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
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        updateDrawer();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        updateDrawer();
        adapter.notifyDataSetChanged();
    }

    /*
        Tab Layout
     */

    private void setupViewPager(ViewPager viewPager) {
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new PrimaryFragment(), getResources().getString(R.string.fragOne));
        adapter.addFragment(new SecondFragment(), getResources().getString((R.string.fragTwo)));
        viewPager.setAdapter(adapter);
    }

    /*
        Drawer
     */

    public void updateDrawer() {
        String[] mPlanetTitles;
        ListView mDrawerList;
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        TagDBHelper tagDB = new TagDBHelper(this);
        mPlanetTitles = tagDB.getTagsStringList();

        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        if (mDrawerList != null) {
            mDrawerList.setAdapter(new ArrayAdapter<>(this, R.layout.drawer_tags, mPlanetTitles));
        }


        //  Setup Drawer Toggle of the Toolbar
        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.app_name, R.string.app_name);
        mDrawerLayout.addDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();
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

} // end of class






