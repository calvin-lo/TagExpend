package com.uoit.calvin.thesis_2016;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import java.util.List;


public class FragmentChart extends Fragment{

    View v;
    private static final int MARGIN = 1080;
    private String GENERAL_ICON;
    private String LOCATION_ICON;
    private String DOLLAR_ICON;
    private String CATEGORY_ICON;

    private Spinner typeSpinner;
    private String username;

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

        GENERAL_ICON = v.getResources().getString(R.string.generalIcon);
        LOCATION_ICON = v.getResources().getString(R.string.locationIcon);
        DOLLAR_ICON = v.getResources().getString(R.string.dollarIcon);
        CATEGORY_ICON = v.getResources().getString(R.string.categoryIcon);


        SharedPreferences sharedpreferences = getActivity().getSharedPreferences("USER", Context.MODE_PRIVATE);
        username = sharedpreferences.getString("username", null);

        typeSpinner = (Spinner) v.findViewById(R.id.typeSpinner);
        setTypeSpinner();

        return v;
    }

    public void displayChart(String type) {

        LinearLayout linearLayout = (LinearLayout) v.findViewById(R.id.chartLayout);
        linearLayout.removeAllViews();
        TagDBHelper tagDBHelper = new TagDBHelper(v.getContext());

        PieChart chart = new PieChart(v.getContext());
        List<Tag> tagList = tagDBHelper.getTagsList(type, username);

        ArrayList<PieEntry> entries = new ArrayList<>();

        for (Tag t : tagList) {
            if (t.getAmount() > 0) {
                entries.add(new PieEntry(t.getAmount(), t.getTitle()));
            }
        }

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(ColorTemplate.JOYFUL_COLORS);

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