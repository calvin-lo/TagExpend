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
    private List<List<Transaction>> transactionList;
    private int selectedPosition;
    private List<Date> uniqueDate;

    private static final int SAVING_DATA = 1;


    MainListViewAdapter(Context context, List<List<Transaction>> transactionsList, List<Date> uniqueDate, int selectedPosition) {
        this.context = context;
        this.transactionList = transactionsList;
        this.uniqueDate = uniqueDate;
        this.selectedPosition = selectedPosition;
    }

    public int getCount() {
        return uniqueDate.size();
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
            TextView tv;
            ListView lv;
            tv = (TextView) row.findViewById(R.id.dateTV);
            lv = (ListView) row.findViewById(R.id.transactionList);
            String s = Integer.toString(new Helper().getDay(uniqueDate.get(position)));
            tv.setText(s);
            if (position == this.selectedPosition) {
                tv.setTextColor(context.getResources().getColor(R.color.highlight));
            }

            ListViewAdapter arrayAdapter = new ListViewAdapter(context.getApplicationContext(), new ArrayList<>(transactionList.get(position)));
            if (lv != null) {
                lv.setAdapter(arrayAdapter);
            }

            if (arrayAdapter.getCount() == 0) {
                if (lv != null) {
                    lv.setVisibility(View.GONE);
                }
                TextView label = (TextView) row.findViewById(R.id.addTV);
                if (label != null) {
                    label.setVisibility(View.VISIBLE);
                    final Context parentContext = parent.getContext();
                    label.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent( parentContext, FormActivity.class);
                            ((MainActivity)parentContext).startActivityForResult(intent, SAVING_DATA);
                        }
                    });
                }
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