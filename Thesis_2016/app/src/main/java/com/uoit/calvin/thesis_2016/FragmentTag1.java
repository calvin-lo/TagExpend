package com.uoit.calvin.thesis_2016;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class FragmentTag1 extends Fragment{

    private String username;

    View v;

    public FragmentTag1() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v =  inflater.inflate(R.layout.fragment_tag1, container, false);

        SharedPreferences sharedpreferences = getContext().getSharedPreferences("USER", Context.MODE_PRIVATE);
        username = sharedpreferences.getString("username", null);

        String tag = getActivity().getIntent().getStringExtra("tag");


        TransactionDBHelper transDB = new TransactionDBHelper(getContext());
        ArrayList<Transaction> transList = new ArrayList<>(transDB.getTransByTag(tag, username));

        Collections.sort(transList);
        Collections.reverse(transList);

        SampleExpandableListAdapter listAdapter;
        ExpandableListView expListView = (ExpandableListView) v.findViewById(R.id.transactionList);
        listAdapter = new SampleExpandableListAdapter(getContext(), getActivity(), transList, username);

        expListView.setAdapter(listAdapter);
        expListView.setGroupIndicator(new ColorDrawable(ContextCompat.getColor(getContext(), R.color.tw__transparent)));
        transDB.close();


        return v;
    }
}