package com.uoit.calvin.thesis_2016;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

class MainListViewAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Transaction> transactionList;

    private static final int SAVING_DATA = 1;
    private String user;


    MainListViewAdapter(Context context, ArrayList<Transaction> transactionsList, String user) {
        this.context = context;
        this.transactionList = transactionsList;
        this.user = user;
    }

    public int getCount() {
        return transactionList.size();
    }

    public Object getItem(int arg0) {
        return null;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            View row;
            row = inflater.inflate(R.layout.main_listview, parent, false);
            ListView lv;
            lv = (ListView) row.findViewById(R.id.transactionList);

            ListViewAdapter arrayAdapter = new ListViewAdapter(context.getApplicationContext(), transactionList, user);
            if (lv != null) {
                lv.setAdapter(arrayAdapter);
            }
            getTotalHeightOfListView(lv);


            return row;
        } else {
            return convertView;
        }
    }

    private static void getTotalHeightOfListView(ListView listView) {

        ListAdapter mAdapter = listView.getAdapter();

        int totalHeight = 0;

        for (int i = 0; i < mAdapter.getCount(); i++) {
            View mView = mAdapter.getView(i, null, listView);

            mView.measure(
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),

                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

            totalHeight += mView.getMeasuredHeight();

        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight
                + (listView.getDividerHeight() * (mAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();

    }

}