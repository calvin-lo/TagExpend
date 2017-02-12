package com.uoit.calvin.thesis_2016;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class FragmentChart extends Fragment{

    View v;
    private static final int MARGIN = 1080;
    private static final String GENERAL_ICON = "\u2606";
    private static final String LOCATION_ICON = "\u27B4";
    private static final String DOLLAR_ICON = "\u00A4";
    private static final String CATEGORY_ICON = "\u00A7";

    private Spinner typeSpinner;;

    public FragmentChart() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v =  inflater.inflate(R.layout.fragment_chart, container, false);

        typeSpinner = (Spinner) v.findViewById(R.id.typeSpinner);
        setTypeSpinner();

        return v;
    }

    public void displayChart(String type) {

        LinearLayout linearLayout = (LinearLayout) v.findViewById(R.id.chartLayout);
        linearLayout.removeAllViews();
        TagDBHelper tagDBHelper = new TagDBHelper(v.getContext());

        PieChart chart = new PieChart(v.getContext());
        List<Tag> tagList = tagDBHelper.getTagsList(type);
        ArrayList<PieEntry> entries = new ArrayList<>();

        for (Tag t : tagList) {
            if (t.getAmount() > 0) {
                entries.add(new PieEntry(t.getAmount(), t.getName()));
            }
        }

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(ColorTemplate.PASTEL_COLORS);

        PieData data = new PieData(dataSet);
        data.setDrawValues(false);
        chart.setDrawHoleEnabled(true);
        chart.setData(data);
        chart.getDescription().setEnabled(false);
        linearLayout.addView(chart, MARGIN, MARGIN);

        tagDBHelper.close();
    }

    public void setTypeSpinner() {

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), R.layout.spinner_layout, getResources().getStringArray(R.array.type));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        if (typeSpinner != null) {
            typeSpinner.setAdapter(adapter);

            typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                    switch (position) {
                        case 0:
                            displayChart(GENERAL_ICON);
                            break;
                        case 1:
                            displayChart(LOCATION_ICON);
                            break;
                        case 2:
                            displayChart(CATEGORY_ICON);
                            break;
                        default:
                            displayChart(GENERAL_ICON);
                            break;
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parentView) {
                    // your code here
                }

            });

        }
    }

}