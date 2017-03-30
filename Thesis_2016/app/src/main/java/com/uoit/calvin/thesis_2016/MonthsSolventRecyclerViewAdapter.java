package com.uoit.calvin.thesis_2016;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

class MonthsSolventRecyclerViewAdapter extends RecyclerView.Adapter<MonthsSolventViewHolders> {

    private String[] months;
    private Context context;
    private MainActivity activity;


    MonthsSolventRecyclerViewAdapter(MainActivity activity, Context context, String[] months) {
        this.months = months;
        this.context = context;
        this.activity = activity;
    }


    @Override
    public MonthsSolventViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {

        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.month_solvent_list, null);
        return new MonthsSolventViewHolders(activity, context, layoutView);
    }

    @Override
    public void onBindViewHolder(MonthsSolventViewHolders holder, int position) {
        holder.tv_title.setText(months[position]);
    }



    @Override
    public int getItemCount() {
        return this.months.length;
    }


}
