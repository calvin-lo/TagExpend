package com.uoit.calvin.thesis_2016;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.support.v7.widget.Toolbar;

import android.text.InputType;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.MultiAutoCompleteTextView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import io.fabric.sdk.android.Fabric;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, android.widget.PopupMenu.OnMenuItemClickListener{

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
    SharedPreferences mainSharedpreferences;
    private List<MenuItem> items;

    private Context context;
    private MultiAutoCompleteTextView tv_search;


    private static final int RESULT_OK = 1;
    private static final int SAVING_DATA = 1;

    int tag_count;
    int trans_count;

    private ActionBarDrawerToggle toggle;

    Menu menu;

    int frag_position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        //deleteDatabase("transDB");
        //deleteDatabase("tagCloudDB");
        //deleteDatabase("userDB");

        helper = new Helper(this);
        this.context = this;
        frag_position = 0;

        sharedpreferences = getSharedPreferences(getString(R.string.shared_pref_name_user), Context.MODE_PRIVATE);
        mainSharedpreferences = getSharedPreferences(getString(R.string.shared_pref_name_main), Context.MODE_PRIVATE);

        if (sharedpreferences.getString(getString(R.string.shared_pref_arg_username), null) == null) {
            helper.setUser(getString(R.string.user_default));
        }

        if (sharedpreferences.getString(getString(R.string.shared_pref_arg_default_display_name), null) == null) {
            helper.setDefaultDisplayName(getString(R.string.user_default));
        }

        setLocalUser();


        int pos = sharedpreferences.getInt(getString(R.string.shared_pref_arg_selected_pos), 0);
        helper.setSelectedPosition(pos);


        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));
        setContentView(R.layout.activity_main);

        updateCount();

        // Set up the action bar
        toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        ab = getSupportActionBar();
        if (toolbar != null) {
            toolbar.setTitle(getResources().getString(R.string.home_frag_title));
            toolbar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    titleMenu(v);
                }
            });
        }

        // Set up the drawer
        setupDrawer();

        viewPager = (CustomViewPager) findViewById(R.id.main_viewpager);
        setupViewPager();

        // Tab Layout
        tabLayout = (TabLayout) findViewById(R.id.main_tabs);
        setupTabLayout();

        final FloatingActionButton fab_add = (FloatingActionButton) findViewById(R.id.main_fab_add);
        if (fab_add != null) {
            fab_add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), FormActivity.class);
                    startActivityForResult(intent, SAVING_DATA);
                }
            });
        }

        KeyboardVisibilityEvent.setEventListener(this, new KeyboardVisibilityEventListener() {
            @Override
            public void onVisibilityChanged(boolean isOpen) {
                RelativeLayout layout_button = (RelativeLayout) findViewById(R.id.main_layout_button);
                if (layout_button != null && fab_add != null) {
                    if (isOpen) {
                        layout_button.setVisibility(View.VISIBLE);
                        fab_add.setVisibility(View.GONE);

                    } else {
                        layout_button.setVisibility(View.GONE);
                        fab_add.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

    }

    @Override
    protected void onRestart() {
        setupDrawer();
        setupViewPager();
        setLocalUser();
        navigationView.getMenu().findItem(R.id.nav_setting).setChecked(false);
        adapter.notifyDataSetChanged();
        super.onRestart();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.main_layout_drawer);
        tv_search = (MultiAutoCompleteTextView) findViewById(R.id.main_search);
        if (drawer != null) {
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            } else if (tv_search != null && tv_search.getVisibility() == View.VISIBLE) {
                tv_search.setVisibility(View.GONE);
            }else {
                super.onBackPressed();
            }
        }
        setupDrawer();
        setupViewPager();
        setLocalUser();
        navigationView.getMenu().findItem(R.id.nav_setting).setChecked(false);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        setupDrawer();
        setupViewPager();
        setLocalUser();
        navigationView.getMenu().findItem(R.id.nav_setting).setChecked(false);
        adapter.notifyDataSetChanged();
        super.onResume();
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.

        int id = item.getItemId();
        final int position = items.indexOf(item);
        if (id == R.id.nav_all) {
            helper.setUser(getString(R.string.icon_all));
            helper.setSelectedPosition(position);
            viewPager.setCurrentItem(0, true);
            adapter.notifyDataSetChanged();
        } else if (id == R.id.nav_user) {
            helper.setUser(getResources().getString(R.string.user_default));
            helper.setSelectedPosition(position);
            viewPager.setCurrentItem(0, true);
            adapter.notifyDataSetChanged();
        } else if (id == R.id.nav_explore) {
            Intent intent = new Intent(this, ExploreActivity.class);
            startActivity(intent);
        }
        else if (id == R.id.nav_user_more) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            TransactionDBHelper transactionDBHelper = new TransactionDBHelper(this);
            final String[] followingList = transactionDBHelper.getUser();

            builder.setTitle(R.string.dialog_title_select_follow)
                    .setItems(followingList, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            helper.setUser(followingList[which]);
                            helper.setSelectedPosition(position);
                            adapter.notifyDataSetChanged();
                        }
                    });
            AlertDialog dialog = builder.create();
            dialog.show();
            transactionDBHelper.close();
        }
        else if (id == R.id.nav_user1) {
            helper.setUser(item.getTitle().toString());
            helper.setSelectedPosition(position);
            viewPager.setCurrentItem(0, true);
            adapter.notifyDataSetChanged();
        }
        else if (id == R.id.nav_user2) {
            helper.setUser(item.getTitle().toString());
            helper.setSelectedPosition(position);
            viewPager.setCurrentItem(0, true);
            adapter.notifyDataSetChanged();
        }
        else if (id == R.id.nav_user3) {
            helper.setUser(item.getTitle().toString());
            helper.setSelectedPosition(position);
            viewPager.setCurrentItem(0, true);
            adapter.notifyDataSetChanged();
        }
        else if (id == R.id.nav_user4) {
            helper.setUser(item.getTitle().toString());
            helper.setSelectedPosition(position);
            viewPager.setCurrentItem(0, true);
            adapter.notifyDataSetChanged();
        }
        else if (id == R.id.nav_user5) {
            helper.setUser(item.getTitle().toString());
            helper.setSelectedPosition(position);
            viewPager.setCurrentItem(0, true);
            adapter.notifyDataSetChanged();
        }
        else if (id == R.id.nav_setting) {
            Intent intent = new Intent(this, SettingActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.main_layout_drawer);
        if (drawer != null) {
            drawer.closeDrawer(GravityCompat.START);
        }

        setToolbar();
        updateCount();
        setToggle();

        return true;
    }

    /** View Pager */
    private void setupViewPager() {
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new FragmentHome(), getResources().getString(R.string.home_frag_title));
        adapter.addFragment(new FragmentTagCloud(),getResources().getString(R.string.tag_cloud_frag_title));
        adapter.addFragment(new FragmentChart(), getResources().getString(R.string.chart_frag_title));
        viewPager.setAdapter(adapter);
        viewPager.setPagingEnabled(true);

        setToolbar();
        updateCount();
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                frag_position = position;
                toolbar.setSubtitle(""); //
                //updateCount();
                String s = "";
                switch (position) {
                    case 0:
                        s = trans_count + " " + getString(R.string.main_subtitle_trans);
                        break;
                    case 1:
                        s = tag_count + " " + getString(R.string.main_subtitle_tags);
                        break;
                    case 2:
                        s = tag_count + " " + getString(R.string.main_subtitle_tags);
                        break;
                }
                toolbar.setSubtitle(s);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    /** Set up Tab Layout */
    private void setupTabLayout() {

        final TabLayout.Tab home = tabLayout.newTab();
        home.setIcon(R.drawable.ic_home_black_24dp);

        final TabLayout.Tab dashboard = tabLayout.newTab();
        dashboard.setIcon(R.drawable.ic_dashboard_black_24dp);

        final TabLayout.Tab chart = tabLayout.newTab();
        chart.setIcon(R.drawable.ic_insert_chart_black_24dp);

        tabLayout.addTab(home, 0);
        tabLayout.addTab(dashboard, 1);
        tabLayout.addTab(chart, 2);

        tabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.accent));

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager));
    }

    /** Set up Drawer */
    public void setupDrawer() {
        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.main_layout_drawer);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.nav_drawer_open, R.string.nav_drawer_close);
        UserDBHelper userDBHelper =  new UserDBHelper(this);
        //toggle.setDrawerIndicatorEnabled(false);
        setToggle();
        toggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawer != null) {
                    if (drawer.isDrawerVisible(GravityCompat.START)) {
                        drawer.closeDrawer(GravityCompat.START);
                    } else {
                        drawer.openDrawer(GravityCompat.START);
                    }
                }
            }

        });

        if (drawer != null) {
            drawer.addDrawerListener(toggle);
        }
        toggle.syncState();

        items = new ArrayList<>();
        Menu menu;
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) {
            menu = navigationView.getMenu();
            for(int i=0; i<menu.size(); i++){
                items.add(menu.getItem(i));
            }

        }

        List<User> usersList = userDBHelper.getAllUser();
        List<String> usersStrList = new ArrayList<>();
        for (User u : usersList) {
            if (!u.getUsername().equals(getString(R.string.user_default))) {
                usersStrList.add(u.getUsername());
            }
        }
        String[] followingList = usersStrList.toArray(new String[usersStrList.size()]);
        int[] navUserID = {R.id.nav_user1, R.id.nav_user2, R.id.nav_user3, R.id.nav_user4, R.id.nav_user5};

        if (navigationView != null) {
            int i = 0;
            for (String s : followingList) {
                if (i > 5) {
                    navigationView.getMenu().findItem(R.id.nav_user_more).setVisible(true);
                    break;
                }
                else {
                    User user = userDBHelper.getUserByUsername(followingList[i]);
                    navigationView.getMenu().findItem(navUserID[i]).setTitle(user.getUsername());
                    navigationView.getMenu().findItem(navUserID[i]).setVisible(true);
                    i++;
                }
            }
            navigationView.setNavigationItemSelectedListener(this);
            if (sharedpreferences.getInt(getString(R.string.shared_pref_arg_selected_pos),0) != -1) {
                navigationView.getMenu().getItem(sharedpreferences.getInt(getString(R.string.shared_pref_arg_selected_pos), 0)).setChecked(true);
            }
        }

        userDBHelper.close();
    }

    public void setToggle() {
        String username = sharedpreferences.getString(getString(R.string.shared_pref_arg_username), getString(R.string.user_default));
        if (username.equals(getString(R.string.icon_all))) {
            toggle.setDrawerIndicatorEnabled(true);
            toggle.setHomeAsUpIndicator(helper.resizeDrawble(getDrawable(R.mipmap.ic_launcher), 80,80));
        } else {
            toggle.setDrawerIndicatorEnabled(false);
            UserDBHelper userDBHelper = new UserDBHelper(this);
            Helper helper = new Helper(this);
            User toggle_user = userDBHelper.getUserByUsername(username);
            Bitmap pi = helper.loadImageFromStorage(toggle_user);
            toggle.setHomeAsUpIndicator(new BitmapDrawable(getResources(), pi));
        }
    }

    /** Setting Menu */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_activity_actions, menu);
        this.menu = menu;

        Helper helper = new Helper(this);

        menu.findItem(R.id.action_month).setTitle(helper.parseMonthToString(mainSharedpreferences.getInt(getString(R.string.shared_pref_arg_month), 0)));
        String s = Integer.toString(mainSharedpreferences.getInt(getString(R.string.shared_pref_arg_year), helper.getYear(helper.timeToDate(helper.getCurrentTime()))));
        menu.findItem(R.id.action_year).setTitle(s);

        helper.setPerfencesYear(Integer.parseInt(menu.findItem(R.id.action_year).getTitle().toString()));
        helper.setPerfencesMonth(helper.parseMonthToInt(menu.findItem(R.id.action_month).getTitle().toString()));

        //menu.findItem(R.id.action_month).setTitle("Feb");
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                tv_search = (MultiAutoCompleteTextView) findViewById(R.id.main_search);
                if (tv_search != null) {
                    tv_search.setVisibility(View.VISIBLE);
                    tv_search.requestFocus();
                    tv_search.setTokenizer(new SpaceTokenizer());
                    TagDBHelper tagDBHelper = new TagDBHelper(this);
                    String tagList[] = tagDBHelper.getTagsStringList("*");
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, tagList);
                    tv_search.setAdapter(adapter);
                    tv_search.setThreshold(1);
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(tv_search, InputMethodManager.SHOW_IMPLICIT);

                    tv_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                        @Override
                        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                                Intent intent = new Intent(getApplicationContext(), TagActivity.class);
                                String s = v.getText().toString().replace(" ", "");
                                intent.putExtra(getString(R.string.intent_extra_tag), s);
                                context.startActivity(intent);
                                return true;
                            }
                                return false;
                            }
                    });
                    tagDBHelper.close();
                }
                break;
            case R.id.action_month:
                RecyclerView month_picker = (RecyclerView) findViewById(R.id.main_month_picker);
                LinearLayout year_picker_layout_0 = (LinearLayout) findViewById(R.id.main_year_picker);
                if (month_picker.getVisibility() == View.GONE) {
                    month_picker.setHasFixedSize(true);
                    StaggeredGridLayoutManager GridLayoutManager = new StaggeredGridLayoutManager(4, 1);
                    month_picker.setLayoutManager(GridLayoutManager);

                    MonthsSolventRecyclerViewAdapter rcAdapter = new MonthsSolventRecyclerViewAdapter(this, this, getResources().getStringArray(R.array.months_short));
                    month_picker.setAdapter(rcAdapter);

                    month_picker.setVisibility(View.VISIBLE);
                    tabLayout.setVisibility(View.GONE);
                    year_picker_layout_0.setVisibility(View.GONE);
                } else {
                    month_picker.setVisibility(View.GONE);
                    tabLayout.setVisibility(View.VISIBLE);
                }

                break;
            case R.id.action_year:
                LinearLayout year_picker_layout = (LinearLayout) findViewById(R.id.main_year_picker);
                RecyclerView month_picker_0 = (RecyclerView) findViewById(R.id.main_month_picker);
                RecyclerView year_picker = (RecyclerView) findViewById(R.id.main_year_picker_rv);
                EditText year_et = (EditText) findViewById(R.id.main_year_picker_et);
                if (year_picker_layout.getVisibility() == View.GONE) {

                    year_picker.setHasFixedSize(true);
                    StaggeredGridLayoutManager GridLayoutManager = new StaggeredGridLayoutManager(3, 1);
                    year_picker.setLayoutManager(GridLayoutManager);

                    int currYear = Calendar.getInstance().get(Calendar.YEAR);
                    List<String> years = new ArrayList<>();
                    for (int i = currYear; i >= currYear - 5; i--) {
                        years.add(Integer.toString(i));
                    }

                    YearsSolventRecyclerViewAdapter yearsSolventRecyclerViewAdapter = new YearsSolventRecyclerViewAdapter(this, this, years);
                    year_picker.setAdapter(yearsSolventRecyclerViewAdapter);

                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(year_et, InputMethodManager.SHOW_IMPLICIT);

                    year_et.setGravity(Gravity.CENTER_HORIZONTAL);
                    year_et.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                        @Override
                        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                                changeYear(v.getText().toString());
                                return true;
                            }
                            return false;
                        }
                    });

                    year_picker_layout.setVisibility(View.VISIBLE);
                    tabLayout.setVisibility(View.GONE);
                    month_picker_0.setVisibility(View.GONE);
                } else {
                    year_picker_layout.setVisibility(View.GONE);
                    tabLayout.setVisibility(View.VISIBLE);
                }
                break;
            default:
                return super.onOptionsItemSelected(item);

        }

        return super.onOptionsItemSelected(item);
    }

    public void changeMonth(String updatedMonth) {
        helper.setPerfencesMonth(helper.parseMonthToInt(updatedMonth));
        int month = mainSharedpreferences.getInt(getString(R.string.shared_pref_arg_month), 0);
        String month_str = helper.parseMonthToString(month);
        menu.findItem(R.id.action_month).setTitle(month_str);
        helper.setPerfencesMonth(month);

        adapter.notifyDataSetChanged();
        String s = "";
        updateCount();
        if (frag_position == 0) {
            s = trans_count + " " + getString(R.string.main_subtitle_trans);
        } else {
            s = tag_count + " " + getString(R.string.main_subtitle_tags);
        }
        toolbar.setSubtitle(s);

        RecyclerView month_picker = (RecyclerView) findViewById(R.id.main_month_picker);
        month_picker.setVisibility(View.GONE);
        tabLayout.setVisibility(View.VISIBLE);
    }

    public void changeYear(String updatedYear) {
        helper.setPerfencesYear(Integer.parseInt(updatedYear));
        int year = mainSharedpreferences.getInt(getString(R.string.shared_pref_arg_year), 0);
        String year_str = Integer.toString(year);
        menu.findItem(R.id.action_year).setTitle(year_str);
        helper.setPerfencesYear(year);

        adapter.notifyDataSetChanged();
        String s = "";
        updateCount();
        if (frag_position == 0) {
            s = trans_count + " " + getString(R.string.main_subtitle_trans);
        } else {
            s = tag_count + " " + getString(R.string.main_subtitle_tags);
        }
        toolbar.setSubtitle(s);

        LinearLayout year_picker_layout = (LinearLayout) findViewById(R.id.main_year_picker);
        year_picker_layout.setVisibility(View.GONE);
        tabLayout.setVisibility(View.VISIBLE);
    }


    /** Handle button */
    public void clickSymbol(String type) {
        if (tv_search != null) {
            String code = "";
            switch (type) {
                case "star":
                    code = getResources().getString(R.string.icon_general);
                    break;
                case "at":
                    code = getResources().getString(R.string.icon_location);
                    break;
                case "category":
                    code = getResources().getString(R.string.icon_category);
                    break;
                default:
                    code = getResources().getString(R.string.icon_general);
                    break;

            }


            int pos_start = tv_search.getSelectionStart();
            int pos_end = tv_search.getSelectionEnd();
            int length = tv_search.getText().toString().length();
            String oldString = tv_search.getText().toString();
            String oldString_start = tv_search.getText().toString().substring(0, pos_start);
            String oldString_end = tv_search.getText().toString().substring(pos_end, length);
            String newString;
            if (length == 0) {
                newString = code;
                tv_search.setText(newString);
                tv_search.setSelection(1);
            } else if (pos_end == 0) {
                newString = oldString_start + code + oldString_end;
                tv_search.setText(newString);
                tv_search.setSelection(pos_end + 1);
            } else if (oldString.charAt(pos_end-1) != ' '){
                newString = oldString_start + " " + code + oldString_end;
                tv_search.setText(newString);
                tv_search.setSelection(pos_end + 2);
            } else {
                newString = oldString_start + code + oldString_end;
                tv_search.setText(newString);
                tv_search.setSelection(pos_end + 1);
            }

        }
    }

    public void clickStar(View v) {
        tv_search.setInputType(InputType.TYPE_CLASS_TEXT);
        clickSymbol("star");
    }

    public void clickAt(View v) {
        tv_search.setInputType(InputType.TYPE_CLASS_TEXT);
        clickSymbol("at");
    }

    public void clickCategory(View v) {
        tv_search.setInputType(InputType.TYPE_CLASS_TEXT);
        clickSymbol("category");
    }

    public void setToolbar() {
        String username = sharedpreferences.getString(getString(R.string.shared_pref_arg_username), getString(R.string.user_default));
        UserDBHelper userDBHelper = new UserDBHelper(this);
        TransactionDBHelper transactionDBHelper = new TransactionDBHelper(getApplicationContext());
        String displayName = userDBHelper.getUserByUsername(username).getDisplayName();
        if (!username.equals(getString(R.string.icon_all))) {
            toolbar.setTitle(displayName);
        } else {
            toolbar.setTitle(getString(R.string.home_frag_title));
        }
        toolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.tw__transparent));
        int year = mainSharedpreferences.getInt(getString(R.string.shared_pref_arg_year), 0);
        int month = mainSharedpreferences.getInt(getString(R.string.shared_pref_arg_month), 0);
        int count = transactionDBHelper.getTransByTime(year, month, username).size();
        String s = count + " " + getString(R.string.main_subtitle_trans);
        toolbar.setSubtitle(s);
        if (ab != null) {
            ab.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.side_toolbar));
        }
        transactionDBHelper.close();
        userDBHelper.close();
    }

    /** Add */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case SAVING_DATA:
                    SharedPreferences sharedpreferences = getSharedPreferences(getString(R.string.shared_pref_name_user), Context.MODE_PRIVATE);
                    UserDBHelper userDBHelper = new UserDBHelper(this);
                    TransactionDBHelper transDB = new TransactionDBHelper(this);
                    TagDBHelper tagDB = new TagDBHelper(this);

                    User user = new User(this,
                            sharedpreferences.getString(getString(R.string.shared_pref_arg_default_display_name), getResources().getString(R.string.user_default)),
                            getResources().getString(R.string.user_default));
                    user.setSinceID(-1);
                    user.setProfileImage(R.drawable.ic_account_box_white_24dp);
                    if (userDBHelper.checkDuplicate(user)) {
                        user = userDBHelper.getUserByUsername(getResources().getString(R.string.user_default));
                        user.addCount();
                        userDBHelper.updateUser(user);
                    } else {
                        user.setCount(0);
                        userDBHelper.addUser(user);
                    }
                    // add the transaction
                    String message = data.getStringExtra(getString(R.string.intent_extra_trans));

                    Transaction trans = new Transaction(this);
                    trans.setMessage(message);
                    trans.setTags(helper.parseTag(message));
                    trans.setGeneral(helper.parseGeneral(message));
                    trans.setLocation(helper.parseLocation(message));
                    trans.setCategory(helper.parseCategory(message));
                    trans.setTimestamp(helper.getCurrentTime());
                    trans.setAmount(helper.getAmount(message));
                    trans.setColor(data.getIntExtra(getString(R.string.intent_extra_color), 0));
                    trans.setUser(user);
                    transDB.addTransactions(trans);

                    // add the tag to tag cloud
                    for (Tag t : trans.getTagsList()) {
                        t.setUser(user);
                        t.setColor(ContextCompat.getColor(this, R.color.box));
                        tagDB.addTag(t);
                    }

                    transDB.close();
                    tagDB.close();
                    userDBHelper.close();
                    break;
            }
        }

        helper.displayTransList(findViewById(android.R.id.content), this,
                mainSharedpreferences.getInt(getString(R.string.shared_pref_arg_year), 0),
        mainSharedpreferences.getInt(getString(R.string.shared_pref_arg_month), 0));
        updateCount();
    }


    public void setLocalUser() {
        UserDBHelper userDBHelper = new UserDBHelper(this);
        if (userDBHelper.getUserByUsername(getString(R.string.user_default)).getUsername() == null) {
            User user = new User(this,
                    sharedpreferences.getString(getString(R.string.shared_pref_arg_default_display_name), getResources().getString(R.string.user_default)),
                    getResources().getString(R.string.user_default));
            user.setSinceID(-1);
            userDBHelper.addUser(user);
        }
        userDBHelper.close();
    }

    public void updateCount() {
        TransactionDBHelper transactionDBHelper = new TransactionDBHelper(this);
        String username = sharedpreferences.getString(getString(R.string.shared_pref_arg_username), getString(R.string.user_default));
        int year = mainSharedpreferences.getInt(getString(R.string.shared_pref_arg_year), 0);
        int month = mainSharedpreferences.getInt(getString(R.string.shared_pref_arg_month), 0);
        trans_count = transactionDBHelper.getTransByTime(year, month, username).size();
        tag_count = transactionDBHelper.getTransTagsByTime(year, month, username).size();
        transactionDBHelper.close();
    }

    public void setToolbarSubtitle(String s) {
        toolbar.setSubtitle(s);
    }

    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.main_popup_unfollow:
                String username = sharedpreferences.getString(getString(R.string.shared_pref_arg_username), getString(R.string.user_default));
                Helper helper = new Helper(this);
                helper.unfollow(username);
                adapter.notifyDataSetChanged();
                helper.setUser(getString(R.string.icon_all));
                helper.setSelectedPosition(0);
                viewPager.setCurrentItem(0, true);
                finish();
                return true;
        }
        return true;
    }

    public void titleMenu(View v) {
        String username = sharedpreferences.getString(getString(R.string.shared_pref_arg_username), getString(R.string.user_default));
        if (username.equals(getString(R.string.user_default))) {

        }
        else if (!username.equals(getString(R.string.icon_all))) {
            PopupMenu popupMenu = new PopupMenu(MainActivity.this, v);
            popupMenu.setOnMenuItemClickListener(MainActivity.this);
            popupMenu.inflate(R.menu.follow_popup);
            popupMenu.show();
        }
    }

    public int getYear() {
        return mainSharedpreferences.getInt(getString(R.string.shared_pref_arg_year), 0);
    }

    public int getMonth() {
        return mainSharedpreferences.getInt(getString(R.string.shared_pref_arg_month), 0);
    }


} // end of class






