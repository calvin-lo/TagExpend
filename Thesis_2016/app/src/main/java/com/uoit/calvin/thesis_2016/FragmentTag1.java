package com.uoit.calvin.thesis_2016;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import java.util.ArrayList;
import java.util.Collections;


public class FragmentTag1 extends Fragment{

    private String username;

    View v;

    public FragmentTag1() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v =  inflater.inflate(R.layout.fragment_tag1, container, false);

        SharedPreferences sharedPreferences = getContext().getSharedPreferences(getString(R.string.shared_pref_name_user), Context.MODE_PRIVATE);
        username = sharedPreferences.getString(getString(R.string.shared_pref_arg_username), null);

        String tag = getActivity().getIntent().getStringExtra(getString(R.string.intent_extra_tag));


        TransactionDBHelper transactionDBHelper = new TransactionDBHelper(getContext());
        ArrayList<Transaction> transList = new ArrayList<>(transactionDBHelper.getTransByTag(tag, username));

        Collections.sort(transList);
        Collections.reverse(transList);

        MainExpandableListAdapter listAdapter;
        ExpandableListView lv_transaction = (ExpandableListView) v.findViewById(R.id.transactionList);
        listAdapter = new MainExpandableListAdapter(getContext(), getActivity(), transList, username);

        lv_transaction.setAdapter(listAdapter);
        lv_transaction.setGroupIndicator(new ColorDrawable(ContextCompat.getColor(getContext(), R.color.tw__transparent)));
        transactionDBHelper.close();


        return v;
    }
}