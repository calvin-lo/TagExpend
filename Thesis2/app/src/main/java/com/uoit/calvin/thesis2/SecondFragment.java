package com.uoit.calvin.thesis2;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableRow;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;


public class SecondFragment extends Fragment {

    private static final int MARGIN = 1070;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.second_layout, container, false);
        displayChart(v);
        return v;
    }

    public void displayChart(View v) {


        Description description = new Description();
        description.setText("");

        LinearLayout linearLayout = (LinearLayout) v.findViewById(R.id.chartLayout);
        TagDBHelper tagDBHelper = new TagDBHelper(getContext().getApplicationContext());

        PieChart chart = new PieChart(getContext());
        List<Tag> dashList = tagDBHelper.getTagsList("#");
        ArrayList<PieEntry> entries = new ArrayList<>();

        for (Tag t : dashList) {
            entries.add(new PieEntry(t.getAmount(), t.getName()));
        }

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);

        chart.setDrawHoleEnabled(false);

        PieData data = new PieData(dataSet);
        data.setDrawValues(false);
        chart.setData(data);
        chart.setDescription(description);


        // chart 2

        PieChart chart2 = new PieChart(getContext());
        List<Tag> atList = tagDBHelper.getTagsList("@");
        ArrayList<PieEntry> entries2 = new ArrayList<>();

        for (Tag t : atList) {
            entries2.add(new PieEntry(t.getAmount(), t.getName()));
        }

        PieDataSet dataSet2 = new PieDataSet(entries2, "");
        dataSet2.setColors(ColorTemplate.COLORFUL_COLORS);

        chart2.setDrawHoleEnabled(false);

        PieData data2 = new PieData(dataSet2);
        data2.setDrawValues(false);
        chart2.setData(data2);
        chart2.setDescription(description);



        // declare the label

        TextView textViewByDash = new TextView(getContext());
        TextView textViewByAt = new TextView(getContext());
        textViewByDash.setText(R.string.byDash);
        textViewByAt.setText(R.string.byAt);


        // add all the object
        if (linearLayout != null) {

            linearLayout.addView(textViewByDash);
            linearLayout.addView(textViewByAt);
            linearLayout.addView(chart, MARGIN, MARGIN);
            linearLayout.addView(chart2, MARGIN, MARGIN);
        }
    }
}
