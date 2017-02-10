package com.uoit.calvin.thesis_2016;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public class SolventRecyclerViewAdapter  extends RecyclerView.Adapter<SolventViewHolders> {

    private List<Tag> itemList;
    private Context context;
    float min = 15;
    float max = 30;

    public SolventRecyclerViewAdapter(Context context, List<Tag> itemList) {
        this.itemList = itemList;
        this.context = context;
    }

    @Override
    public SolventViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {

        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.solvent_list, null);
        SolventViewHolders rcv = new SolventViewHolders(context, layoutView);
        return rcv;
    }

    @Override
    public void onBindViewHolder(SolventViewHolders holder, int position) {
        holder.textView.setText(itemList.get(position).toString());
        float textSize = itemList.get(position).getAmount();
        if (textSize < min) {
            textSize = min;
        } else if (textSize > max) {
            textSize = max;
        }
        holder.textView.setTextSize(textSize);
    }

    @Override
    public int getItemCount() {
        return this.itemList.size();
    }
}
