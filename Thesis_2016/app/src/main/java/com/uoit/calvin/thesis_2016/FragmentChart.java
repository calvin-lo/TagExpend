package com.uoit.calvin.thesis_2016;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
    private static final int MARGIN = 1100;
    private String GENERAL_ICON;
    private String LOCATION_ICON;
    private String CATEGORY_ICON;

    private Spinner spinner_type;
    private String username;

    public FragmentChart() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v =  inflater.inflate(R.layout.fragment_chart, container, false);

        GENERAL_ICON = v.getResources().getString(R.string.icon_general);
        LOCATION_ICON = v.getResources().getString(R.string.icon_location);
        CATEGORY_ICON = v.getResources().getString(R.string.icon_category);


        SharedPreferences sharedpreferences = getActivity().getSharedPreferences(getString(R.string.shared_pref_name_user), Context.MODE_PRIVATE);
        username = sharedpreferences.getString(getString(R.string.shared_pref_arg_username), getString(R.string.user_default));

        spinner_type = (Spinner) v.findViewById(R.id.chart_spinner_type);
        setTypeSpinner();

        return v;
    }

    public void displayChart(String type) {

        LinearLayout layout_chart = (LinearLayout) v.findViewById(R.id.chart_layout_chart);
        layout_chart.removeAllViews();
        TagDBHelper tagDBHelper = new TagDBHelper(v.getContext());

        PieChart pieChart = new PieChart(v.getContext());
        TransactionDBHelper transactionDBHelper = new TransactionDBHelper(v.getContext());
        int year = ((MainActivity)getActivity()).getYear();
        int month = (((MainActivity)getActivity()).getMonth());
        List<Tag> tagList = transactionDBHelper.getTransTagsByTime(year, month, username);
        transactionDBHelper.close();
        //List<Tag> tagList = tagDBHelper.getTagsList(type, username);

        String s = tagList.size() + " " + getString(R.string.main_subtitle_tags);
        //((MainActivity)getActivity()).setToolbarSubtitle(s);

        ArrayList<PieEntry> entries = new ArrayList<>();

        for (Tag t : tagList) {
            if (t.getAmount() > 0) {
                entries.add(new PieEntry(t.getAmount(), t.getTitle()));
            }
        }

        PieDataSet pieDataSet = new PieDataSet(entries, "");
        pieDataSet.setColors(ColorTemplate.JOYFUL_COLORS);

        PieData pieData = new PieData(pieDataSet);
        pieData.setDrawValues(false);
        pieChart.setDrawHoleEnabled(false);
        pieChart.setData(pieData);
        pieChart.getDescription().setEnabled(false);
        layout_chart.addView(pieChart, MARGIN, MARGIN);

        tagDBHelper.close();
    }

    public void setTypeSpinner() {

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.type));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        if (spinner_type != null) {
            spinner_type.setAdapter(adapter);

            spinner_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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