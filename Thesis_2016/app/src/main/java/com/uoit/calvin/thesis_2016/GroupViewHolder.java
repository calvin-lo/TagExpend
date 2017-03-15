package com.uoit.calvin.thesis_2016;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


public class GroupViewHolder {

    public TextView userNameTV;
    public TextView displayNameTV;
    public TextView timeTv;
    public TextView tagTv;
    public ImageView profile;
    public GroupViewHolder(View v) {
        userNameTV = (TextView)v.findViewById(R.id.userName);
        displayNameTV = (TextView)v.findViewById(R.id.displayName);
        timeTv = (TextView)v.findViewById(R.id.time);
        tagTv = (TextView)v.findViewById(R.id.tag);
        profile = (ImageView)v.findViewById(R.id.profile);
    }
}