package com.uoit.calvin.thesis_2016;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.swipe.SimpleSwipeListener;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.BaseSwipeAdapter;

import java.util.ArrayList;
import java.util.List;

public class ListViewAdapter extends BaseSwipeAdapter {

    private Context mContext;
    private ArrayList<Transaction> transactionsList;

    public ListViewAdapter(Context mContext, ArrayList<Transaction> objects) {
        this.mContext = mContext;
        this.transactionsList = objects;
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipe;
    }

    @Override
    public View generateView(final int position, ViewGroup parent) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.listview_item, null);
        final SwipeLayout swipeLayout = (SwipeLayout)v.findViewById(getSwipeLayoutResourceId(position));
        swipeLayout.setShowMode(SwipeLayout.ShowMode.PullOut);
        swipeLayout.addDrag(SwipeLayout.DragEdge.Left, v.findViewById(R.id.left_bottom));
        swipeLayout.addSwipeListener(new SimpleSwipeListener() {
            @Override
            public void onOpen(SwipeLayout layout) {
            }
        });
        swipeLayout.setOnDoubleClickListener(new SwipeLayout.DoubleClickListener() {
            @Override
            public void onDoubleClick(SwipeLayout layout, boolean surface) {
                Toast.makeText(mContext, "DoubleClick", Toast.LENGTH_SHORT).show();
            }
        });

        v.findViewById(R.id.deleteTv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(mContext, "click delete " + transactionsList.get(position).getId(), Toast.LENGTH_SHORT).show();

                long id = transactionsList.get(position).getId();

                TransactionDBHelper transDB = new TransactionDBHelper(mContext.getApplicationContext());

                Helper helper = new Helper();
                List<Tag> tagList = helper.parseTag(transDB.getTransByID(id).toString());

                transDB.deleteTransactions(id);

                // update tag cloud

                TagDBHelper tagDB = new TagDBHelper(mContext.getApplicationContext());
                for (Tag t : tagList) {
                    tagDB.updateTag(t);
                }

                mItemManger.removeShownLayouts(swipeLayout);
                transactionsList.remove(position);
                notifyDataSetChanged();
                mItemManger.closeAllItems();

            }
        });
        return v;
    }

    @Override
    public void fillValues(int position, View convertView) {
        TextView tagTv = (TextView)convertView.findViewById(R.id.tag);
        tagTv.setText(transactionsList.get(position).getTagsStr());
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