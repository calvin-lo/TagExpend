package com.uoit.calvin.thesis_2016;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

public class YearsSolventViewHolders extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView tv_title;
    private Context context;
    private MainActivity activity;

    public YearsSolventViewHolders(MainActivity activity, Context context, View v) {
        super(v);
        v.setOnClickListener(this);
        tv_title = (TextView) v.findViewById(R.id.month_solvent_tv_title);
        this.context = context;
        this.activity = activity;
    }

    @Override
    public void onClick(View v) {
        activity.changeYear(tv_title.getText().toString());
    }
}
