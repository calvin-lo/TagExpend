package com.uoit.calvin.thesis2;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.List;

/**
 * Created by calvin on 05/10/16.
 */

public class PrimaryFragment extends Fragment {

    DBHelper mydb;
    ListView transList;
    View x;
    static final int SAVING_DATA = 1;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        x = inflater.inflate(R.layout.primary_layout,null);
        FloatingActionButton btnFab = (FloatingActionButton) x.findViewById(R.id.fab1);
        btnFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), FormActivity.class);
                startActivityForResult(intent,SAVING_DATA);
            }
        });
        /**
         * Databases
         */
        mydb = new DBHelper(getContext());

        List<String> tags = mydb.getAllData();
        ArrayAdapter arrayAdapter = new ArrayAdapter<>(getContext(), R.layout.activity_listview, tags);

        transList = (ListView) x.findViewById(R.id.transactionList);
        transList.setAdapter(arrayAdapter);

        return x;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        getActivity();
        String tag = "";
        if(requestCode == 1 && resultCode == getActivity().RESULT_OK) {
            tag = data.getExtras().getString("result");
        }

        // update list view
        mydb = new DBHelper(getContext());
        mydb.addTransactions(tag);
        List<String> tags = mydb.getAllData();
        ArrayAdapter arrayAdapter = new ArrayAdapter<>(getContext(), R.layout.activity_listview, tags);

        transList = (ListView) x.findViewById(R.id.transactionList);
        transList.setAdapter(arrayAdapter);
    }


}
