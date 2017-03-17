package com.uoit.calvin.thesis_2016;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ImageButton;

import java.util.ArrayList;
import java.util.List;

public class MainExpandableListAdapter extends BaseExpandableListAdapter implements ExpandableListAdapter {
    public Context context;
    private LayoutInflater vi;

    private ArrayList<Transaction> transactionsList;
    String username;

    private static final int GROUP_ITEM_RESOURCE = R.layout.list_group;
    private static final int CHILD_ITEM_RESOURCE = R.layout.list_item;

    public MainExpandableListAdapter(Context context, Activity activity, ArrayList<Transaction> objects, String username) {
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

        if (!transactionsList.get(position).getUser().getUsername().equals(context.getResources().getString(R.string.user_default))) {
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

        holder.iv_profile.setImageBitmap(helper.loadImageFromStorage(t.getUser()));

        String s = t.getMessage();
        holder.tv_msg.setText(s);
        long time = helper.parseDate(t.getTimestamp());
        String time_s = "\u2022 " + new TimeAgo().getTimeAgo(time,context);
        holder.tv_time.setText(time_s);
        holder.tv_display_name.setText(t.getUser().getDisplayName());
        s = "@" + t.getUser().getUsername();
        holder.tv_username.setText(s);


        if (isExpanded) {
            holder.tv_msg.setMaxLines(Integer.MAX_VALUE);
            holder.tv_msg.setEllipsize(null);
            holder.tv_display_name.setMaxLines(Integer.MAX_VALUE);
            holder.tv_display_name.setEllipsize(null);
            holder.tv_username.setMaxLines(Integer.MAX_VALUE);
            holder.tv_username.setEllipsize(null);
        } else {
            holder.tv_msg.setMaxLines(1);
            holder.tv_msg.setEllipsize(TextUtils.TruncateAt.END);
            holder.tv_display_name.setMaxLines(1);
            holder.tv_display_name.setEllipsize(TextUtils.TruncateAt.END);
            holder.tv_username.setMaxLines(1);
            holder.tv_username.setEllipsize(TextUtils.TruncateAt.END);
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
            recyclerView = (RecyclerView)v.findViewById(R.id.list_recycler_view);
            deleteButton = (ImageButton)v.findViewById(R.id.list_button_delete);
        }
    }
}