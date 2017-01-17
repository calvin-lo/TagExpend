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
    private static final String STAR_ICON = "☆";
    private static final String AT_ICON = "➴";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);

        Toolbar myChildToolbar = (Toolbar) findViewById(R.id.formToolbar);
        setSupportActionBar(myChildToolbar);
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }

        displayChart();
    }

    public void displayChart() {


        Description description = new Description();
        description.setText("");

        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.chartLayout);
        TagDBHelper tagDBHelper = new TagDBHelper(getApplicationContext());

        PieChart chart = new PieChart(getApplicationContext());
        List<Tag> dashList = tagDBHelper.getTagsList(STAR_ICON);
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
        chart.setDescription(description);


        // chart 2

        PieChart chart2 = new PieChart(getApplicationContext());
        List<Tag> atList = tagDBHelper.getTagsList(AT_ICON);
        ArrayList<PieEntry> entries2 = new ArrayList<>();

        for (Tag t : atList) {
            if (t.getAmount() > 0) {
                entries2.add(new PieEntry(t.getAmount(), t.getName()));
            }
        }

        PieDataSet dataSet2 = new PieDataSet(entries2, "");
        dataSet2.setColors(ColorTemplate.COLORFUL_COLORS);

        chart2.setDrawHoleEnabled(false);

        PieData data2 = new PieData(dataSet2);
        data2.setDrawValues(false);
        chart2.setData(data2);
        chart2.setDescription(description);



        // declare the label

        TextView textViewByDash = new TextView(getApplicationContext());
        TextView textViewByAt = new TextView(getApplicationContext());
        textViewByDash.setText(R.string.byDash);
        textViewByAt.setText(R.string.byAt);


        // add all the object
        if (linearLayout != null) {

            linearLayout.addView(textViewByDash);
            linearLayout.addView(chart, MARGIN, MARGIN);
            linearLayout.addView(textViewByAt);
            linearLayout.addView(chart2, MARGIN, MARGIN);
        }

        tagDBHelper.close();
    }
}
