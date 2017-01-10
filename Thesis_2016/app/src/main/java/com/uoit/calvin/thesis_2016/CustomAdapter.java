package com.uoit.calvin.thesis_2016;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

class CustomAdapter extends BaseAdapter {

    private Context context;
    private String[]  title;
    private int[] icon;

    CustomAdapter(Context context, String[] text1, int[] imageIds) {
        this.context = context;
        title = text1;
        icon = imageIds;

    }

    public int getCount() {
        return title.length;
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
                row = inflater.inflate(R.layout.row, parent, false);
                TextView tv;
                ImageView iv;
                iv = (ImageView) row.findViewById(R.id.imgIcon);
                tv = (TextView) row.findViewById(R.id.txtTitle);
                tv.setText(title[position]);
                iv.setImageResource(icon[position]);
                return row;
            } else {
                return convertView;
            }
    }
}