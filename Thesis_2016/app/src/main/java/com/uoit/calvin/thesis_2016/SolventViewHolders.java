package com.uoit.calvin.thesis_2016;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

public class SolventViewHolders extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView tv_title;
    private Context context;

    public SolventViewHolders(Context context,View v) {
        super(v);
        v.setOnClickListener(this);
        tv_title = (TextView) v.findViewById(R.id.solvent_tv_title);
        this.context = context;
    }

    @Override
    public void onClick(View v) {
        String tag = tv_title.getText().toString();
        Intent intent = new Intent(context, TagActivity.class);
        intent.putExtra(context.getString(R.string.intent_extra_tag), tag);
        context.startActivity(intent);
    }
}
