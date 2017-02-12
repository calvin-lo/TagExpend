package com.uoit.calvin.thesis_2016;

import android.os.Bundle;
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
    private static final int MARGIN = 700;
    List<Transaction> transList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag);
        tag = getIntent().getStringExtra("tag");

        Toolbar toolBar = (Toolbar) findViewById(R.id.tagToolbar);
        if (toolBar != null) {
            TagDBHelper tagDBHelper = new TagDBHelper(this.getApplicationContext());
            List<Tag> tagList = tagDBHelper.getTagsList("*");
            for (Tag t : tagList) {
                if (t.toString().equals(tag)) {
                    String title= tag + " - Total: $" + t.getAmount();
                    toolBar.setTitle(title);
                    tagDBHelper.close();
                }
            }

        }
        setSupportActionBar(toolBar);
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }

        // set the title
        TransactionDBHelper transDB = new TransactionDBHelper(this);
        transList = transDB.getTransByTag(tag);

        ListViewAdapter arrayAdapter = new ListViewAdapter(this, new ArrayList<>(transList));
        ListView transListView = (ListView) findViewById(R.id.transListView);
        if (transListView != null) {
            transListView.setAdapter(arrayAdapter);
            registerForContextMenu(transListView);
        }

        transDB.close();
        setYearSpinner();
        setMonthSpinner();
        int currYear = Calendar.getInstance().get(Calendar.YEAR);
        int currMonth = Calendar.getInstance().get(Calendar.MONTH);
        displayTrend(currYear, -1);
    }

    public void clickDelete(View v) {
        TagDBHelper tagDB = new TagDBHelper(getApplicationContext());
        tagDB.deleteTag(tag);
        tagDB.close();
        finish();
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

                    int month = new Helper().parseMonthtoInt(parentView.getItemAtPosition(position).toString());;
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
                    int month = new Helper().parseMonthtoInt(monthSpinner.getSelectedItem().toString());
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


