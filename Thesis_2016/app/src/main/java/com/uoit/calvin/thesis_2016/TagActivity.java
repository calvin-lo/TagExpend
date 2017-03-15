package com.uoit.calvin.thesis_2016;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class TagActivity extends AppCompatActivity {

    public String tag;
    private String username;

    ViewPagerAdapter adapter;
    Toolbar toolBar;
    SharedPreferences sharedpreferences;
    private CustomViewPager viewPager;
    private TabLayout tabLayout;

    List<Transaction> transList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag);
        tag = getIntent().getStringExtra("tag");

        sharedpreferences = getSharedPreferences("USER", Context.MODE_PRIVATE);

        toolBar = (Toolbar) findViewById(R.id.tagToolbar);
        setSupportActionBar(toolBar);
        SharedPreferences sharedpreferences = getSharedPreferences("USER", Context.MODE_PRIVATE);
        username = sharedpreferences.getString("username", null);
        if (toolBar != null) {
            toolBar.setTitle(tag);
            TagDBHelper tagDBHelper = new TagDBHelper(this.getApplicationContext());
            List<Tag> tagList = tagDBHelper.getTagsList("*", username);
            for (Tag t : tagList) {
                if (t.toString().equals(tag)) {
                    //String title= tag + " - Total: $" + t.getAmount();
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

        viewPager = (CustomViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        setupTabLayout();
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
        adapter.addFragment(new FragmentTag1(), getResources().getString(R.string.fragment_tag1));
        adapter.addFragment(new FragmentTag2(),getResources().getString(R.string.fragment_tag2));
        viewPager.setAdapter(adapter);
        viewPager.setPagingEnabled(true);
    }


    private void setupTabLayout() {
        tabLayout = (TabLayout) findViewById(R.id.tabs);

        final TabLayout.Tab list = tabLayout.newTab();
        list.setIcon(R.drawable.ic_dns_black_24dp);

        final TabLayout.Tab trend = tabLayout.newTab();
        trend.setIcon(R.drawable.ic_insert_chart_black_24dp);



        tabLayout.addTab(list, 0);
        tabLayout.addTab(trend, 1);

        tabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.accent));

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager));
    }





    public void setMonthSpinner() {

        Spinner monthSpinner = (Spinner) findViewById(R.id.monthSpinner);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.months, R.layout.spinner_layout);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        if (monthSpinner != null) {
            monthSpinner.setAdapter(adapter);
        }

        if (monthSpinner != null) {
            monthSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                    int month = new Helper(getApplicationContext()).parseMonthtoInt(parentView.getItemAtPosition(position).toString());;
                    Spinner yearSpinner = (Spinner) findViewById(R.id.yearSpinner);
                    if (yearSpinner != null) {
                        int year = Integer.parseInt(yearSpinner.getSelectedItem().toString());
                        displayTrend(year, month);
                    }
                }
                @Override
                public void onNothingSelected(AdapterView<?> parentView) {
                    // your code here
                }
            });
        }

    }

    public void setYearSpinner() {
        Spinner yearSpinner = (Spinner) findViewById(R.id.yearSpinner);

        int currYear = Calendar.getInstance().get(Calendar.YEAR);
        List<Integer> years = new ArrayList<>();
        for (int i = currYear; i >= 2000; i--){
            years.add(i);
        }

        ArrayAdapter<Integer> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, years);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        if (yearSpinner != null) {
            yearSpinner.setAdapter(adapter);
        }

        yearSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                int year = Integer.parseInt(parentView.getItemAtPosition(position).toString());
                Spinner monthSpinner = (Spinner) findViewById(R.id.yearSpinner);
                if (monthSpinner != null) {
                    int month = new Helper(getApplicationContext()).parseMonthtoInt(monthSpinner.getSelectedItem().toString());
                    displayTrend(year, month);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });
    }

    public ArrayList<Float> calculateDateByYear (int year) {
        ArrayList<Float> resultList = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            resultList.add(0.0f);
        }
        for (int i = 0; i < transList.size(); i++) {
            Transaction t = transList.get(i);
            if (t.getYear() == year) {
                resultList.set(t.getMonth()-1, resultList.get(t.getMonth()-1) + t.getAmount());
            }
        }
        return resultList;
    }

    public ArrayList<Float> calculateDateByDay (int year, int month) {
        ArrayList<Float> resultList = new ArrayList<>();
        for (int i = 0; i < 31; i++) {
            resultList.add(0.0f);
        }
        for (int i = 0; i < transList.size(); i++) {
            Transaction t = transList.get(i);
            if ((t.getYear() == year) && (t.getMonth() == month)) {
                resultList.set(t.getDay(), resultList.get(t.getDay()) + t.getAmount());
            }
        }
        return resultList;
    }

    public void displayTrend(int year, int month) {

        LineChart chart = (LineChart) findViewById(R.id.chart);

        boolean type = month > 0 && month <= 12;

        ArrayList<Float> arrayList;
        ArrayList<Entry> entries = new ArrayList<>();
        if (type) {
            arrayList = calculateDateByDay(year, month);
            for (int i = 0; i < 31; i++) {
                entries.add(new Entry(i, arrayList.get(i)));
            }
        } else {
            arrayList = calculateDateByYear(year);
            for (int i =0; i < 12; i++) {
                entries.add(new Entry(i, arrayList.get(i)));
            }
        }

        LineDataSet dataset = new LineDataSet(entries, "Amount");
        LineData data = new LineData(dataset);
        if (chart != null) {
            chart.setData(data);
            chart.invalidate();
            chart.getDescription().setEnabled(false);
            chart.getLegend().setEnabled(false);
            //chart.setScaleEnabled(false);

            XAxis xAxis = chart.getXAxis();
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setDrawGridLines(false);
            xAxis.setGranularity(1f);

            if (type) {
                xAxis.setLabelCount(10);
                xAxis.setAxisMinimum(1.0f);
                xAxis.setAxisMaximum(31.0f);
                xAxis.setValueFormatter(new XAxisFormatterDefault());
            } else {
                xAxis.setLabelCount(12);
                xAxis.setAxisMinimum(0.0f);
                xAxis.setAxisMaximum(11.0f);
                xAxis.setValueFormatter(new XAxisFormatterMonth());
            }
            YAxis yAxisLeft = chart.getAxisLeft();
            yAxisLeft.setAxisMinimum(0.0f);
            yAxisLeft.setDrawGridLines(false);


            YAxis yAxisRight = chart.getAxisRight();
            yAxisRight.setEnabled(false);

        }


    }
}


