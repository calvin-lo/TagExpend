package com.uoit.calvin.thesis_2016;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


class GroupViewHolder {

    TextView tv_username;
    TextView tv_display_name;
    TextView tv_time;
    TextView tv_msg;
    ImageView iv_profile;
    GroupViewHolder(View v) {
        tv_username = (TextView)v.findViewById(R.id.list_tv_userName);
        tv_display_name = (TextView)v.findViewById(R.id.list_tv_displayName);
        tv_time = (TextView)v.findViewById(R.id.list_tv_time);
        tv_msg = (TextView)v.findViewById(R.id.list_tv_msg);
        iv_profile = (ImageView)v.findViewById(R.id.list_iv_profile);
    }
}