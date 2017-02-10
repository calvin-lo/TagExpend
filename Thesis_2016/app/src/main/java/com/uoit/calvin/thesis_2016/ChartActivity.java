package com.uoit.calvin.thesis_2016;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;

public class ChartActivity extends AppCompatActivity {

    private static final int MARGIN = 1080;
    private static final String STAR_ICON = "\u2606";
    private static final String LOCATION_ICON = "\u27B4";
    private static final String DOLLAR_ICON = "\u00A4";
    private static final String CATEGORY_ICON = "\u00A7";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);

        Toolbar myChildToolbar = (Toolbar) findViewById(R.id.chartToolbar);
        setSupportActionBar(myChildToolbar);
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
            //ab.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        }

        displayChart();
    }

    public void displayChart() {

        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.chartLayout);
        TagDBHelper tagDBHelper = new TagDBHelper(getApplicationContext());

        PieChart chart = new PieChart(getApplicationContext());
        List<Tag> dashList = tagDBHelper.getTagsList(CATEGORY_ICON);
        ArrayList<PieEntry> entries = new ArrayList<>();

        for (Tag t : dashList) {
            if (t.getAmount() > 0) {
                entries.add(new PieEntry(t.getAmount(), t.getName()));
            }
        }

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);

        chart.setDrawHoleEnabled(false);

        PieData data = new PieData(dataSet);
        data.setDrawValues(false);
        chart.setData(data);
        // add all the object
        if (linearLayout != null) {
            linearLayout.addView(chart, MARGIN, MARGIN);
        }

        tagDBHelper.close();
    }
}
