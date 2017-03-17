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

    View v;

    public FragmentTagCloud() {
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
        v =  inflater.inflate(R.layout.fragment_tag_cloud, container, false);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(getString(R.string.shared_pref_name_user), Context.MODE_PRIVATE);
        String username = sharedPreferences.getString(getString(R.string.shared_pref_arg_username), getContext().getResources().getString(R.string.user_default));

        RecyclerView recyclerView = (RecyclerView)v.findViewById(R.id.tag_cloud_recycler_view);
        recyclerView.setHasFixedSize(true);

        StaggeredGridLayoutManager GridLayoutManager = new StaggeredGridLayoutManager(3, 1);
        recyclerView.setLayoutManager(GridLayoutManager);

        List<Tag> tagList = new TagDBHelper(getContext()).getTagsList(getString(R.string.icon_all), username);

        SolventRecyclerViewAdapter rcAdapter = new SolventRecyclerViewAdapter(getContext(), tagList);
        recyclerView.setAdapter(rcAdapter);


        return v;
    }
}