package com.uoit.calvin.thesis_2016;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;


public class FragmentTagCloud extends Fragment{

    private String user;

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

        SharedPreferences sharedpreferences = getActivity().getSharedPreferences("USER", Context.MODE_PRIVATE);
        user = sharedpreferences.getString("user", null);

        RecyclerView recyclerView = (RecyclerView)v.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);

        StaggeredGridLayoutManager GridLayoutManager = new StaggeredGridLayoutManager(3, 1);
        recyclerView.setLayoutManager(GridLayoutManager);

        List<Tag> tagList = new TagDBHelper(getContext()).getTagsList("*", user);

        SolventRecyclerViewAdapter rcAdapter = new SolventRecyclerViewAdapter(getContext(), tagList);
        recyclerView.setAdapter(rcAdapter);


        return v;
    }
}