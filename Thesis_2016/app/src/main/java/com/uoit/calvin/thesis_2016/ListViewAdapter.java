package com.uoit.calvin.thesis_2016;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.daimajia.swipe.SimpleSwipeListener;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.BaseSwipeAdapter;

import java.util.ArrayList;
import java.util.List;

class ListViewAdapter extends BaseSwipeAdapter {

    private Context context;
    private ArrayList<Transaction> transactionsList;

    ListViewAdapter(Context context, ArrayList<Transaction> objects) {
        this.context = context;
        this.transactionsList = objects;
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
        swipeLayout.addDrag(SwipeLayout.DragEdge.Left, v.findViewById(R.id.left_bottom));
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

        // Delete
        v.findViewById(R.id.swipe2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                long id = transactionsList.get(position).getId();

                TransactionDBHelper transDB = new TransactionDBHelper(context.getApplicationContext());

                Helper helper = new Helper();
                List<Tag> tagList = helper.parseTag(transDB.getTransByID(id).toString());

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

            }
        });

        // Left
        TextView swipe3TV = (TextView) v.findViewById(R.id.swipe3);
        String s = transactionsList.get(position).getTagsStr() + " $" + transactionsList.get(position).getAmount();
        swipe3TV.setText(s);
        return v;
    }

    @Override
    public void fillValues(int position, View convertView) {
        TextView tagTv = (TextView)convertView.findViewById(R.id.tag);
        String s = transactionsList.get(position).getTagsStr() + " $" + transactionsList.get(position).getAmount();
        tagTv.setText(s);
        TextView timeTv = (TextView)convertView.findViewById(R.id.time);
        timeTv.setText(transactionsList.get(position).getTimestamp());
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
}