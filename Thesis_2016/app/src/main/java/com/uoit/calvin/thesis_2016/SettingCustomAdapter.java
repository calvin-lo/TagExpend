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

class SettingCustomAdapter extends BaseAdapter {

    private Context context;
    private String[]  title;
    private int[] icon;
    private boolean connected;

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
                TextView tv_title = (TextView) row.findViewById(R.id.setting_row_tv_title);
                ImageView iv_icon = (ImageView) row.findViewById(R.id.setting_row_iv_icon);
                tv_title.setText(title[position]);
                iv_icon.setImageResource(icon[position]);
                SharedPreferences sharedPreferences = context.getSharedPreferences(context.getString(R.string.shared_pref_name_main), Context.MODE_PRIVATE);
                if (position == 1) {
                    CheckBox cb_auto_post = (CheckBox) row.findViewById(R.id.setting_row_cb_auto_post);
                    cb_auto_post.setVisibility(View.VISIBLE);
                    cb_auto_post.setChecked(sharedPreferences.getBoolean(context.getString(R.string.shared_pref_arg_auto_post), false));
                    cb_auto_post.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                            Helper helper = new Helper(context);
                            helper.setAutoPost(b);
                        }
                    });
                }
                if (position == 2) {
                    TextView tv_status = (TextView) row.findViewById(R.id.setting_row_tv_status);
                    if (tv_status != null) {
                        tv_status.setVisibility(View.VISIBLE);
                        if (connected) {
                            tv_status.setText(context.getString(R.string.twitter_status_connected));
                        } else {
                            tv_status.setText(context.getString(R.string.twitter_status_disconnected));
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