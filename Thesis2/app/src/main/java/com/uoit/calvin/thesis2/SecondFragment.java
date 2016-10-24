package com.uoit.calvin.thesis2;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

/**
 * Created by calvin on 05/10/16.
 */

public class SecondFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.second_layout,null);

        PieChart chart = (PieChart) v.findViewById(R.id.chart);

        ArrayList<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(1f, 0));
        entries.add(new PieEntry(2f, 1));
        entries.add(new PieEntry(3f, 2));
        entries.add(new PieEntry(4f, 3));
        entries.add(new PieEntry(5f, 4));
        entries.add(new PieEntry(6f, 5));

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);

        PieData data = new PieData(dataSet);

        chart.setData(data);


        return v;
    }


}
