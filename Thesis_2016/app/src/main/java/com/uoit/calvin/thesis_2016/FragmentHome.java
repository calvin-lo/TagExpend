package com.uoit.calvin.thesis_2016;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.SimpleExpandableListAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


public class FragmentHome extends Fragment{

    private static final int RESULT_OK = 1;
    private static final int SAVING_DATA = 1;

    TransactionDBHelper transDB;
    TagDBHelper tagDB;
    String username;
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
        username = sharedpreferences.getString("username", null);

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
                    SharedPreferences sharedpreferences = getContext().getSharedPreferences("USER", Context.MODE_PRIVATE);
                    User user = new User(getContext(),
                            sharedpreferences.getString("defaultUsername", getResources().getString(R.string.default_user)),
                            getResources().getString(R.string.default_user));
                    user.setProfileImage(R.drawable.ic_label_outline_white_big);
                    UserDBHelper userDBHelper = new UserDBHelper(getContext());
                    if (userDBHelper.checkDuplicate(user)) {
                        user = userDBHelper.getUserNyUsername(getResources().getString(R.string.default_user));
                        user.addCount();
                        userDBHelper.updateUser(user);
                    } else {
                        user.setCount(0);
                        userDBHelper.addUser(user);
                    }
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
                    trans.setUser(user);
                    transDB.addTransactions(trans);

                    // add the tag to tag cloud
                    tagDB = new TagDBHelper(v.getContext().getApplicationContext());
                    for (Tag t : trans.getTagsList()) {
                        t.setUser(user);
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
        ArrayList<Transaction> transList = new ArrayList<>(transDB.getAllData(username));
        Collections.sort(transList);
        Collections.reverse(transList);
        if (transList.size() > 0) {
        /*    ListViewAdapter mainAdapter = new ListViewAdapter(v.getContext().getApplicationContext(), transList, username);
            ExpandableListView transListView = (ExpandableListView) v.findViewById(R.id.transactionList);
            if (transListView != null) {
                transListView.setAdapter(mainAdapter);
                registerForContextMenu(transListView);
            }*/

            SampleExpandableListAdapter listAdapter;
            ExpandableListView expListView = (ExpandableListView) v.findViewById(R.id.transactionList);
            listAdapter = new SampleExpandableListAdapter(getContext(), getActivity(), transList, username);

            expListView.setAdapter(listAdapter);
            expListView.setGroupIndicator(new ColorDrawable(ContextCompat.getColor(getContext(), R.color.tw__transparent)));
        }

        transDB.close();
    }


}