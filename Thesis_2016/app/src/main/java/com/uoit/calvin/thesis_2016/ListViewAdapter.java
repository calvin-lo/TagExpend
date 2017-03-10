package com.uoit.calvin.thesis_2016;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.daimajia.swipe.SimpleSwipeListener;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.BaseSwipeAdapter;

import java.util.ArrayList;
import java.util.List;

class ListViewAdapter extends BaseSwipeAdapter {

    private Context context;
    private ArrayList<Transaction> transactionsList;
    private String user;

    ListViewAdapter(Context context, ArrayList<Transaction> objects, String user) {
        this.context = context;
        this.transactionsList = objects;
        this.user = user;
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipe;
    }

    @Override
    public View generateView(final int position, ViewGroup parent) {
        View v = LayoutInflater.from(context).inflate(R.layout.listview_item, null);
        final SwipeLayout swipeLayout = (SwipeLayout)v.findViewById(getSwipeLayoutResourceId(position));
        swipeLayout.setShowMode(SwipeLayout.ShowMode.PullOut);
        swipeLayout.addDrag(SwipeLayout.DragEdge.Left, v.findViewById(R.id.bottom_wrapper));
        swipeLayout.setRightSwipeEnabled(false);
        swipeLayout.addSwipeListener(new SimpleSwipeListener() {
            @Override
            public void onOpen(SwipeLayout layout) {
            }
        });

        // Details
        v.findViewById(R.id.swipe1).setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                   Intent intent = new Intent(context, DetailsActivity.class);
                   intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                   long id = transactionsList.get(position).getId();
                   intent.putExtra("ID", id);
                   context.startActivity(intent);

               }
        });

        ImageButton deleteButton = (ImageButton) v.findViewById(R.id.swipe2);

        if (!transactionsList.get(position).getUser().equals(context.getResources().getString(R.string.default_user))) {
            deleteButton.setVisibility(View.GONE);
        } else {
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    delete(position, swipeLayout);
                }
            });
        }

        return v;
    }

    @Override
    public void fillValues(int position, View convertView) {
        Transaction t = transactionsList.get(position);
        convertView.setBackgroundColor(t.getColor());
        TextView tagTv = (TextView)convertView.findViewById(R.id.tag);
        String s = t.getMessage();
        tagTv.setText(s);
        TextView timeTv = (TextView)convertView.findViewById(R.id.time);
        long time = new Helper(context).parseDate(t.getTimestamp());
        timeTv.setText(new TimeAgo().getTimeAgo(time,context));

        TextView userNameTV = (TextView)convertView.findViewById(R.id.userName);
        TextView displayNameTV = (TextView)convertView.findViewById(R.id.displayName);
        displayNameTV.setText(t.getName());
        s = "@" + t.getUser();
        userNameTV.setText(s);
    }

    @Override
    public int getCount() {
        return transactionsList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private void delete(int position, SwipeLayout swipeLayout) {
        long id = transactionsList.get(position).getId();

        TransactionDBHelper transDB = new TransactionDBHelper(context.getApplicationContext());

        Helper helper = new Helper(context);
        List<Tag> tagList = helper.parseTag(transDB.getTransByID(id, user).getMessage(), user);

        transDB.deleteTransactions(id);

        // update tag cloud
        TagDBHelper tagDB = new TagDBHelper(context.getApplicationContext());
        for (Tag t : tagList) {
            tagDB.updateTag(t);
        }

        mItemManger.removeShownLayouts(swipeLayout);
        transactionsList.remove(position);
        notifyDataSetChanged();
        mItemManger.closeAllItems();

        transDB.close();
        tagDB.close();

    }
}