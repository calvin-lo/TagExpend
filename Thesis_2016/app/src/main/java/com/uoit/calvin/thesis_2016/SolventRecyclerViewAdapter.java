package com.uoit.calvin.thesis_2016;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

class SolventRecyclerViewAdapter  extends RecyclerView.Adapter<SolventViewHolders> {

    private List<Tag> tagsList;
    private Context context;
    private static final float min = 15;
    private static final float max = 30;
    private int type;
    private int color;

    SolventRecyclerViewAdapter(Context context, List<Tag> itemList) {
        this.tagsList = itemList;
        this.context = context;
        this.type = 0;
        this.color = ContextCompat.getColor(context, R.color.box);
    }

    SolventRecyclerViewAdapter(Context context, List<Tag> itemList, int type, int color) {
        this.tagsList = itemList;
        this.context = context;
        this.type = type;
        this.color = color;
    }

    @Override
    public SolventViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {

        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.solvent_list, null);
        return new SolventViewHolders(context, layoutView);
    }

    @Override
    public void onBindViewHolder(SolventViewHolders holder, int position) {

        if (type == 0) {

            float textSize = tagsList.get(position).getAmount();
            if (textSize < min) {
                textSize = min;
            } else if (textSize > max) {
                textSize = max;
            }
            holder.tv_title.setTextSize(textSize);
        } else if (type == 1) {
            Helper helper = new Helper(context);
            holder.tv_title.setTextSize(12.0f);
            color = helper.lighter(color, 0.3f);
            holder.tv_title.setBackground(new ColorDrawable(color));
        }

        holder.tv_title.setText(tagsList.get(position).toString());
        if (tagsList.get(position).getColor() != 0) {
            holder.tv_title.setBackground(new ColorDrawable(tagsList.get(position).getColor()));
        }
    }



    @Override
    public int getItemCount() {
        return this.tagsList.size();
    }
}
