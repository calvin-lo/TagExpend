package com.uoit.calvin.thesis_2016;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

class YearsSolventRecyclerViewAdapter extends RecyclerView.Adapter<YearsSolventViewHolders> {

    private List<String> years;
    private Context context;
    private MainActivity activity;


    YearsSolventRecyclerViewAdapter(MainActivity activity, Context context, List<String> years) {
        this.years = years;
        this.context = context;
        this.activity = activity;
    }


    @Override
    public YearsSolventViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {

        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.month_solvent_list, null);
        return new YearsSolventViewHolders(activity, context, layoutView);
    }

    @Override
    public void onBindViewHolder(YearsSolventViewHolders holder, int position) {
        holder.tv_title.setText(years.get(position));
    }



    @Override
    public int getItemCount() {
        return this.years.size();
    }


}
