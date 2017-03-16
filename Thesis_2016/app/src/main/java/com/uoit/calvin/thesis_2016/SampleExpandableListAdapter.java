package com.uoit.calvin.thesis_2016;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ExpandableListAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SampleExpandableListAdapter extends BaseExpandableListAdapter implements ExpandableListAdapter {
    public Context context;
    private LayoutInflater vi;

    private ArrayList<Transaction> transactionsList;
    String username;

    private static final int GROUP_ITEM_RESOURCE = R.layout.list_group;
    private static final int CHILD_ITEM_RESOURCE = R.layout.list_item;

    public SampleExpandableListAdapter(Context context, Activity activity, ArrayList<Transaction> objects, String username) {
        this.context = context;
        this.transactionsList = objects;
        this.username = username;
        vi = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    public String getChild(int groupPosition, int childPosition) {
        return "child-" + groupPosition + ":" + childPosition;
    }
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }
    public int getChildrenCount(int groupPosition) {
        return 1;
    }
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        View v = vi.inflate(R.layout.list_item, null);
        final int position = groupPosition;
        final Transaction t = transactionsList.get(groupPosition);
        v.setBackgroundColor(t.getColor());
        ChildViewHolder holder = new ChildViewHolder(v);


        holder.recyclerView.setHasFixedSize(true);

        StaggeredGridLayoutManager GridLayoutManager = new StaggeredGridLayoutManager(3, 1);
        holder.recyclerView.setLayoutManager(GridLayoutManager);

        List<Tag> tagList = t.getTagsList();

        SolventRecyclerViewAdapter rcAdapter = new SolventRecyclerViewAdapter(context, tagList, 1);
        holder.recyclerView.setAdapter(rcAdapter);

        if (!transactionsList.get(position).getUser().getUsername().equals(context.getResources().getString(R.string.default_user))) {
            holder.deleteButton.setVisibility(View.GONE);
        } else {
            holder.deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    long id = t.getId();

                    new Helper(context).deleteTrans(id, username);
                    transactionsList.remove(position);
                    notifyDataSetChanged();

                }
            });

        }





        return v;
    }
    public String getGroup(int groupPosition) {
        return "group-" + groupPosition;
    }
    public int getGroupCount() {
        return transactionsList.size();
    }
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        View v = vi.inflate(R.layout.list_group, null);
        GroupViewHolder holder = new GroupViewHolder(v);

        Helper helper = new Helper(context);
        Transaction t = transactionsList.get(groupPosition);
        v.setBackgroundColor(t.getColor());

        holder.profile.setImageBitmap(helper.loadImageFromStorage(t.getUser()));

        String s = t.getMessage();
        holder.tagTv.setText(s);
        long time = helper.parseDate(t.getTimestamp());
        String time_s = "\u2022 " + new TimeAgo().getTimeAgo(time,context);
        holder.timeTv.setText(time_s);
        holder.displayNameTV.setText(t.getUser().getDisplayName());
        s = "@" + t.getUser().getUsername();
        holder.userNameTV.setText(s);


        if (isExpanded) {
            holder.tagTv.setMaxLines(Integer.MAX_VALUE);
            holder.tagTv.setEllipsize(null);
            holder.displayNameTV.setMaxLines(Integer.MAX_VALUE);
            holder.displayNameTV.setEllipsize(null);
            holder.userNameTV.setMaxLines(Integer.MAX_VALUE);
            holder.userNameTV.setEllipsize(null);
        } else {
            holder.tagTv.setMaxLines(1);
            holder.tagTv.setEllipsize(TextUtils.TruncateAt.END);
            holder.displayNameTV.setMaxLines(1);
            holder.displayNameTV.setEllipsize(TextUtils.TruncateAt.END);
            holder.userNameTV.setMaxLines(1);
            holder.userNameTV.setEllipsize(TextUtils.TruncateAt.END);
        }

        return v;
    }
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
    public boolean hasStableIds() {
        return true;
    }



    private class ChildViewHolder {

        private RecyclerView recyclerView;
        private ImageButton deleteButton;
        private ChildViewHolder(View v) {
            recyclerView = (RecyclerView)v.findViewById(R.id.recycler_view);
            deleteButton = (ImageButton)v.findViewById(R.id.deleteButton);
        }
    }
}