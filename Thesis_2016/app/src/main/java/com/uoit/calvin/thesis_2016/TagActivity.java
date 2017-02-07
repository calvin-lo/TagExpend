package com.uoit.calvin.thesis_2016;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
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
        displayTrend();
    }

    public void clickDelete(View v) {
        TagDBHelper tagDB = new TagDBHelper(getApplicationContext());
        tagDB.deleteTag(tag);
        tagDB.close();
        finish();
    }

    public ArrayList<Float> calculateDateByYear (int year) {
        ArrayList<Float> resultList = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            resultList.add(0.0f);
        }
        for (Transaction t : transList) {
            if (t.getYear() == year) {
                switch (t.getMonth()){
                    case 1:
                        resultList.set(0, resultList.get(0) + t.getAmount());
                        break;
                    case 2:
                        resultList.set(1, resultList.get(1) + t.getAmount());
                        break;
                    case 3:
                        resultList.set(2, resultList.get(2) + t.getAmount());
                        break;
                    case 4:
                        resultList.set(3, resultList.get(3) + t.getAmount());
                        break;
                    case 5:
                        resultList.set(4, resultList.get(4) + t.getAmount());
                        break;
                    case 6:
                        resultList.set(5, resultList.get(5) + t.getAmount());
                        break;
                    case 7:
                        resultList.set(6, resultList.get(6) + t.getAmount());
                        break;
                    case 8:
                        resultList.set(7, resultList.get(7) + t.getAmount());
                        break;
                    case 9:
                        resultList.set(8, resultList.get(8) + t.getAmount());
                        break;
                    case 10:
                        resultList.set(9, resultList.get(9) + t.getAmount());
                        break;
                    case 11:
                        resultList.set(10, resultList.get(10) + t.getAmount());
                        break;
                    case 12:
                        resultList.set(11, resultList.get(11) + t.getAmount());
                        break;
                }
            }
        }
        return resultList;
    }

    public void displayTrend() {

        Description description = new Description();
        description.setText("");

        LineChart chart = (LineChart) findViewById(R.id.chart);

        ArrayList<Float> arrayList = calculateDateByYear(2017);
        ArrayList<Entry> entries = new ArrayList<>();


        for (int i =0; i < 12; i++) {
            entries.add(new Entry(i+1, arrayList.get(i)));
        }

        LineDataSet dataset = new LineDataSet(entries, "Amount");

        LineData data = new LineData(dataset);
        if (chart != null) {
            chart.setData(data);
            chart.invalidate();
            chart.setDescription(description);
        }


    }
}


