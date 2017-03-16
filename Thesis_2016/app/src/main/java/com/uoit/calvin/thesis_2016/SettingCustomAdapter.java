package com.uoit.calvin.thesis_2016;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterSession;

class SettingCustomAdapter extends BaseAdapter {

    private Context context;
    private String[]  title;
    private int[] icon;
    boolean connected;

    SettingCustomAdapter(Context context, String[] text1, int[] imageIds, boolean connected) {
        this.context = context;
        title = text1;
        icon = imageIds;
        this.connected = connected;

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
                row = inflater.inflate(R.layout.setting_row, parent, false);
                TextView tv;
                ImageView iv;
                iv = (ImageView) row.findViewById(R.id.imgIcon);
                tv = (TextView) row.findViewById(R.id.txtTitle);
                tv.setText(title[position]);
                iv.setImageResource(icon[position]);
                SharedPreferences sharedpreferences = context.getSharedPreferences("MAIN", Context.MODE_PRIVATE);
                if (position == 1) {
                    CheckBox cb = (CheckBox) row.findViewById(R.id.autoPost);
                    cb.setVisibility(View.VISIBLE);
                    cb.setChecked(sharedpreferences.getBoolean("autoPost", false));
                    cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                            Helper helper = new Helper(context);
                            helper.setAutoPost(b);
                        }
                    });
                }
                if (position == 2) {
                    TextView statusTV = (TextView) row.findViewById(R.id.txtStatus);
                    if (statusTV != null) {
                        statusTV.setVisibility(View.VISIBLE);
                        //boolean connected = sharedpreferences.getBoolean("twitterConnected", false);
                        if (connected) {
                            statusTV.setText(context.getString(R.string.twitter_status_connected));
                        } else {
                            statusTV.setText(context.getString(R.string.twitter_status_disconnected));
                        }
                    }
                }
                return row;
            } else {
                return convertView;
            }
    }

    public void update(boolean connected) {
        this.connected = connected;
        notifyDataSetChanged();
    }
}