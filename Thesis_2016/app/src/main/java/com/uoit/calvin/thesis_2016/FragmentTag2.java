package com.uoit.calvin.thesis_2016;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class FragmentTag2 extends Fragment{

    private List<Transaction> transList;

    View v;

    public FragmentTag2() {
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
        v =  inflater.inflate(R.layout.fragment_tag2, container, false);
        SharedPreferences sharedPreferences = getContext().getSharedPreferences(getString(R.string.shared_pref_name_user), Context.MODE_PRIVATE);
        String username = sharedPreferences.getString(getString(R.string.shared_pref_arg_username), null);

        String tag = getActivity().getIntent().getStringExtra(getString(R.string.intent_extra_tag));

        TransactionDBHelper transactionDBHelper = new TransactionDBHelper(getContext());
        transList = transactionDBHelper.getTransByTag(tag, username);

        transactionDBHelper.close();
        setYearSpinner();
        setMonthSpinner();
        int currYear = Calendar.getInstance().get(Calendar.YEAR);
        displayTrend(currYear, -1);

        transactionDBHelper.close();


        return v;
    }

    public void setMonthSpinner() {

        Spinner month_spinner = (Spinner) v.findViewById(R.id.tag_trend_spinner_month);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.months,  android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        if (month_spinner != null) {
            month_spinner.setAdapter(adapter);
        }

        if (month_spinner != null) {
            month_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                    int month = new Helper(getContext()).parseMonthToInt(parentView.getItemAtPosition(position).toString());;
                    Spinner yearSpinner = (Spinner) v.findViewById(R.id.tag_trend_spinner_year);
                    if (yearSpinner != null) {
                        int year = Integer.parseInt(yearSpinner.getSelectedItem().toString());
                        displayTrend(year, month);
                    }
                }
                @Override
                public void onNothingSelected(AdapterView<?> parentView) {

                }
            });
        }

    }

    public void setYearSpinner() {
        Spinner spinner_year = (Spinner) v.findViewById(R.id.tag_trend_spinner_year);

        int currYear = Calendar.getInstance().get(Calendar.YEAR);
        List<Integer> years = new ArrayList<>();
        for (int i = currYear; i >= 2000; i--) {
            years.add(i);
        }

        ArrayAdapter<Integer> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, years);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        if (spinner_year != null) {
            spinner_year.setAdapter(adapter);
            spinner_year.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                    int year = Integer.parseInt(parentView.getItemAtPosition(position).toString());
                    Spinner monthSpinner = (Spinner) v.findViewById(R.id.tag_trend_spinner_year);
                    if (monthSpinner != null) {
                        int month = new Helper(getContext()).parseMonthToInt(monthSpinner.getSelectedItem().toString());
                        displayTrend(year, month);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parentView) {

                }

            });
        }
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

        LineChart lineChart = (LineChart) v.findViewById(R.id.tag_trend_chart);

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

        LineDataSet lineDataSet = new LineDataSet(entries, getString(R.string.tag_frag_trend_label));
        LineData lineData = new LineData(lineDataSet);
        if (lineChart != null) {
            lineChart.setData(lineData);
            lineChart.invalidate();
            lineChart.getDescription().setEnabled(false);
            lineChart.getLegend().setEnabled(false);
            //lineChart.setScaleEnabled(false);

            XAxis xAxis = lineChart.getXAxis();
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
            YAxis yAxisLeft = lineChart.getAxisLeft();
            yAxisLeft.setAxisMinimum(0.0f);
            yAxisLeft.setDrawGridLines(false);


            YAxis yAxisRight = lineChart.getAxisRight();
            yAxisRight.setEnabled(false);

        }


    }
}