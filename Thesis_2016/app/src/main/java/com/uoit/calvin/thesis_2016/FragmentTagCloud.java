package com.uoit.calvin.thesis_2016;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;


public class FragmentTagCloud extends Fragment{

    private static final int RESULT_OK = 1;
    private static final int SAVING_DATA = 1;

    TransactionDBHelper transDB;
    TagDBHelper tagDB;

    View v;

    public FragmentTagCloud() {
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
        v =  inflater.inflate(R.layout.fragment_tag_cloud, container, false);

        RecyclerView recyclerView = (RecyclerView)v.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);

        StaggeredGridLayoutManager GridLayoutManager = new StaggeredGridLayoutManager(3, 1);
        recyclerView.setLayoutManager(GridLayoutManager);

        List<Tag> tagList = new TagDBHelper(getContext()).getTagsList("*");

        SolventRecyclerViewAdapter rcAdapter = new SolventRecyclerViewAdapter(getContext(), tagList);
        recyclerView.setAdapter(rcAdapter);


        return v;
    }
}