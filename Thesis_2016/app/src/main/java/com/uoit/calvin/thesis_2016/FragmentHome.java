package com.uoit.calvin.thesis_2016;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
    String user;

    View v;

    public FragmentHome() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v =  inflater.inflate(R.layout.fragment_home, container, false);

        SharedPreferences sharedpreferences = getActivity().getSharedPreferences("USER", Context.MODE_PRIVATE);
        user = sharedpreferences.getString("user", null);

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
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.main_activity_actions, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Intent intent = new Intent(getContext(), TagActivity.class);
                intent.putExtra("tag", query);
                getContext().startActivity(intent);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case SAVING_DATA:
                    // add the transaction
                    transDB = new TransactionDBHelper(v.getContext().getApplicationContext());
                    Helper helper = new Helper(getContext());

                    String message = data.getStringExtra("trans");

                    Transaction trans = new Transaction(getContext());
                    trans.setMessage(message);
                    trans.setTags(helper.parseTag(message));
                    trans.setGeneral(helper.parseGeneral(message));
                    trans.setLocation(helper.parseLocation(message));
                    trans.setCategory(helper.parseCategory(message));
                    trans.setTimestamp(helper.getCurrentTime());
                    trans.setAmount(helper.getAmount(message));
                    trans.setColor(data.getIntExtra("color", 0));
                    SharedPreferences sharedpreferences = getContext().getSharedPreferences("USER", Context.MODE_PRIVATE);;
                    trans.setName(sharedpreferences.getString("defaultUser", getResources().getString(R.string.default_user)));
                    trans.setUser(getResources().getString(R.string.default_user));
                    transDB.addTransactions(trans);

                    // add the tag to tag cloud
                    tagDB = new TagDBHelper(v.getContext().getApplicationContext());
                    for (Tag t : trans.getTagsList()) {
                        t.setName(sharedpreferences.getString("defaultUser", getResources().getString(R.string.default_user)));
                        t.setUser(v.getResources().getString(R.string.default_user));
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
        ArrayList<Transaction> transList = new ArrayList<>(transDB.getAllData(user));
        Collections.sort(transList);
        Collections.reverse(transList);
        if (transList.size() > 0) {
            ListViewAdapter mainAdapter = new ListViewAdapter(v.getContext().getApplicationContext(), transList, user);
            ListView transListView = (ListView) v.findViewById(R.id.transactionList);
            if (transListView != null) {
                transListView.setAdapter(mainAdapter);
                registerForContextMenu(transListView);
            }
        }


        /*
        List<Transaction> transList = transDB.getAllData(user);
        List<List<Transaction>> myList = new ArrayList<>();

        List<Date> uniqueDate = new ArrayList<>();

        int todayPosition = -1;
        Helper helper = new Helper(getContext());
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
            MainListViewAdapter mainAdapter = new MainListViewAdapter(v.getContext().getApplicationContext(), myList, uniqueDate, todayPosition , user);
            ListView transListView = (ListView) v.findViewById(R.id.transactionList);
            if (transListView != null) {
                transListView.setAdapter(mainAdapter);
                transListView.setSelectionFromTop(todayPosition,0);
                registerForContextMenu(transListView);
            }
        }
    */

        transDB.close();
    }

}