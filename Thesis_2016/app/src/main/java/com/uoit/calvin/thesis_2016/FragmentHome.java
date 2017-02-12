package com.uoit.calvin.thesis_2016;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;


public class FragmentHome extends Fragment{

    private static final int RESULT_OK = 1;
    private static final int SAVING_DATA = 1;

    TransactionDBHelper transDB;
    TagDBHelper tagDB;

    View v;

    public FragmentHome() {
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
        v =  inflater.inflate(R.layout.fragment_home, container, false);

        FloatingActionButton btnFab = (FloatingActionButton) v.findViewById(R.id.addFab);
        btnFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), FormActivity.class);
                startActivityForResult(intent, SAVING_DATA);
            }
        });


        displayTransList();
        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case SAVING_DATA:
                    // add the transaction
                    transDB = new TransactionDBHelper(v.getContext().getApplicationContext());
                    Helper helper = new Helper();

                    String message = data.getStringExtra("trans");

                    Transaction trans = new Transaction();
                    trans.setMessage(message);
                    trans.setTags(helper.parseTag(message));
                    trans.setGeneral(helper.parseGeneral(message));
                    trans.setLocation(helper.parseLocation(message));
                    trans.setCategory(helper.parseCategory(message));
                    trans.setTimestamp(helper.getCurrentTime());
                    trans.setAmount(helper.getAmount(message));
                    transDB.addTransactions(trans);

                    // add the tag to tag cloud
                    tagDB = new TagDBHelper(v.getContext().getApplicationContext());
                    for (Tag t : trans.getTagsList()) {
                        tagDB.addTag(t);
                    }

                    transDB.close();
                    tagDB.close();
                    break;
            }
        }

        displayTransList();
    }

    public void displayTransList() {
        transDB = new TransactionDBHelper(v.getContext().getApplicationContext());

        List<Transaction> transList = transDB.getAllData();;
        List<List<Transaction>> myList = new ArrayList<>();

        List<Date> uniqueDate = new ArrayList<>();

        int todayPosition = -1;
        Helper helper = new Helper();
        Date todayDate = helper.timeToDate(helper.getCurrentTime());

        // Date Sorting
        for (Transaction t : transList) {
            boolean dup = false;
            for (Date date : uniqueDate) {
                if (helper.getYear(t.getDate())== helper.getYear(date)
                        && helper.getMonth(t.getDate()) == helper.getMonth(date)
                        && helper.getDay(t.getDate()) == helper.getDay(date)) {
                    dup = true;
                }
            }
            if (!dup) {
                uniqueDate.add(t.getDate());
            }
        }

        boolean haveToday = false;
        for (Transaction t : transList) {
            if (helper.getYear(t.getDate())== helper.getYear(todayDate)
                    && helper.getMonth(t.getDate()) == helper.getMonth(todayDate)
                    && helper.getDay(t.getDate()) == helper.getDay(todayDate)) {
                haveToday = true;
            }
        }
        if (!haveToday) {
            uniqueDate.add(todayDate);
        }

        Collections.sort(transList);
        Collections.reverse(transList);
        Collections.sort(uniqueDate);
        Collections.reverse(uniqueDate);

        for (Date date : uniqueDate) {
            List<Transaction> temp = new ArrayList<>();
            for (Transaction t : transList) {
                if (helper.getYear(t.getDate())== helper.getYear(date)
                        && helper.getMonth(t.getDate()) == helper.getMonth(date)
                        && helper.getDay(t.getDate()) == helper.getDay(date)) {
                    temp.add(t);
                }
            }
            myList.add(temp);
        }

        for (int i = 0; i < uniqueDate.size(); i++) {
            if (helper.getYear(uniqueDate.get(i)) == helper.getYear(todayDate)
                    && helper.getMonth(uniqueDate.get(i))  == helper.getMonth(todayDate)
                    && helper.getDay(uniqueDate.get(i))  == helper.getDay(todayDate)) {
                todayPosition = i;
            }
        }


        if (myList.size() > 0) {
            MainListViewAdapter mainAdapter = new MainListViewAdapter(v.getContext().getApplicationContext(), myList, uniqueDate, todayPosition);
            ListView transListView = (ListView) v.findViewById(R.id.transactionList);
            if (transListView != null) {
                transListView.setAdapter(mainAdapter);
                transListView.setSelectionFromTop(todayPosition,0);
                registerForContextMenu(transListView);
            }
        }


        transDB.close();
    }

}