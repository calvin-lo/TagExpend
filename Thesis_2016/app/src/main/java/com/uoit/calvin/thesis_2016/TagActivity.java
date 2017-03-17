package com.uoit.calvin.thesis_2016;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
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

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;

import java.util.List;

public class TagActivity extends AppCompatActivity {

    public String tag;

    ViewPagerAdapter adapter;
    Toolbar toolBar;
    SharedPreferences sharedpreferences;
    private CustomViewPager viewPager;
    private TabLayout tabLayout;
    private Context context;

    private MultiAutoCompleteTextView tv_search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag);
        tag = getIntent().getStringExtra(getString(R.string.intent_extra_tag));
        this.context = this;

        sharedpreferences = getSharedPreferences(getString(R.string.shared_pref_name_user), Context.MODE_PRIVATE);

        toolBar = (Toolbar) findViewById(R.id.tag_toolbar);
        setSupportActionBar(toolBar);
        String username = sharedpreferences.getString(getString(R.string.shared_pref_arg_username), null);
        if (toolBar != null) {
            toolBar.setTitle(tag);
            TagDBHelper tagDBHelper = new TagDBHelper(this.getApplicationContext());
            List<Tag> tagList = tagDBHelper.getTagsList(getString(R.string.icon_all), username);
            for (Tag t : tagList) {
                if (t.toString().equals(tag)) {
                    String title = tag;
                    toolBar.setTitle(title);
                    tagDBHelper.close();
                }
            }
        }
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }

        viewPager = (CustomViewPager) findViewById(R.id.tag_viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tag_tabs);
        setupTabLayout();

        KeyboardVisibilityEvent.setEventListener(this, new KeyboardVisibilityEventListener() {
            @Override
            public void onVisibilityChanged(boolean isOpen) {
                RelativeLayout layout_button = (RelativeLayout) findViewById(R.id.tag_layout_button);
                if (layout_button != null) {
                    if (isOpen) {
                        layout_button.setVisibility(View.VISIBLE);
                    } else {
                        layout_button.setVisibility(View.GONE);
                    }
                }
            }
        });
    }


    @Override
    public void onBackPressed() {
        tv_search = (MultiAutoCompleteTextView) findViewById(R.id.main_search);
        adapter.notifyDataSetChanged();
        if (tv_search != null && tv_search.getVisibility() == View.VISIBLE) {
            tv_search.setVisibility(View.GONE);
        }else {
            super.onBackPressed();
        }
    }

    public void clickDelete(View v) {
        TagDBHelper tagDB = new TagDBHelper(getApplicationContext());
        tagDB.deleteTag(tag);
        tagDB.close();
        finish();
    }


    /*
    View Pager
 */
    private void setupViewPager(CustomViewPager viewPager) {
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new FragmentTag1(), getResources().getString(R.string.tag_frag_title_list));
        adapter.addFragment(new FragmentTag2(),getResources().getString(R.string.tag_frag_title_trend));
        viewPager.setAdapter(adapter);
        viewPager.setPagingEnabled(true);
    }


    private void setupTabLayout() {

        final TabLayout.Tab list = tabLayout.newTab();
        list.setIcon(R.drawable.ic_list_black_24dp);

        final TabLayout.Tab trend = tabLayout.newTab();
        trend.setIcon(R.drawable.ic_insert_chart_black_24dp);



        tabLayout.addTab(list, 0);
        tabLayout.addTab(trend, 1);

        tabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.accent));

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.tag_activity_actions, menu);
        return false;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                tv_search = (MultiAutoCompleteTextView) findViewById(R.id.tag_search);
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
                }
            default:
                return super.onOptionsItemSelected(item);

        }
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


}


