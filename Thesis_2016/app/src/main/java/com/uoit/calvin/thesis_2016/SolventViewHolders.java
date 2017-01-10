package com.uoit.calvin.thesis_2016;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class SolventViewHolders extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView textView;
    private Context context;

    public SolventViewHolders(Context context,View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);
        textView = (TextView) itemView.findViewById(R.id.country_name);
        this.context = context;
    }

    @Override
    public void onClick(View view) {
        String tag = textView.getText().toString();
        Intent intent = new Intent(context, TagActivity.class);
        // symbol ☆
        intent.putExtra("tag", tag);
        context.startActivity(intent);
    }
}
