package com.uoit.calvin.thesis_2016;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class PrimaryFragment extends Fragment {

    private static final int DETAILS = 3;
    private static final int UPDATE = 2;
    private static final int SAVING_DATA = 1;
    private static final int RESULT_OK = 1;

    TransactionDBHelper transDB;
    TagDBHelper tagDB;
    View x;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        x = inflater.inflate(R.layout.primary_layout, container, false);
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
        displayTransList();

        return x;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == this.RESULT_OK) {
            switch (requestCode) {
                case SAVING_DATA:
                    // add the transaction
                    transDB = new TransactionDBHelper(getContext().getApplicationContext());
                    Helper helper = new Helper();
                    List<Tag> tags = helper.parseTag(data.getStringExtra("trans"));

                    Transaction trans = new Transaction(tags);
                    transDB.addTransactions(trans);

                    // add the tag to tag cloud
                    tagDB = new TagDBHelper(getContext().getApplicationContext());
                    for (Tag t : tags) {
                        tagDB.addTag(t);
                    }

                    transDB.close();
                    tagDB.close();
                    break;
                case UPDATE:
                    break;

            }
        }
        ((MainActivity)getActivity()).updateDrawer();
        displayTransList();
    }

    public void displayTransList() {
        transDB = new TransactionDBHelper(getContext().getApplicationContext());

        List<Transaction> transList = transDB.getAllData();
        List<Long> transIds = new ArrayList<>();
        for (Transaction t : transList) {
            transIds.add(t.getId());
        }
        // Set the transaction
        ArrayAdapter arrayAdapter = new ArrayAdapter<>(getContext(), R.layout.activity_listview, transList);
        ListView transListView = (ListView) x.findViewById(R.id.transactionList);
        transListView.setAdapter(arrayAdapter);
        registerForContextMenu(transListView);

        // Set the ID
        ArrayAdapter arrayAdapterID = new ArrayAdapter<>(getContext(), R.layout.activity_listview, transIds);
        ListView idList = (ListView) x.findViewById(R.id.transactionListID);
        idList.setAdapter(arrayAdapterID);
        registerForContextMenu(idList);

        transDB.close();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId() == R.id.transactionList) {
            String[] menuItems = getResources().getStringArray(R.array.update_menu);
            for (int i = 0; i < menuItems.length; i++) {
                menu.add(Menu.NONE, i, i, menuItems[i]);
            }
        }
    }


   @Override
    public boolean onContextItemSelected(MenuItem item) {

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        int menuItemIndex = item.getItemId();
        String[] menuItems = getResources().getStringArray(R.array.update_menu);
        String menuItemName = menuItems[menuItemIndex];
        // Delete
        switch (menuItemName) {
            case "Delete":
                ListView l = (ListView) x.findViewById(R.id.transactionListID);
                String ID = l.getItemAtPosition(info.position).toString();
                transDB = new TransactionDBHelper(getContext().getApplicationContext());
                transDB.deleteTransactions((Long.parseLong(ID)));
                displayTransList();
                break;
            case "Edit":
                Intent editIntent = new Intent(getContext(), FormActivity.class);
                startActivityForResult(editIntent, UPDATE);
                break;
            case "Details":
                Intent detailsIntent = new Intent(getContext(), DetailsActivity.class);
                startActivityForResult(detailsIntent, DETAILS);
                break;
        }


        return true;
    }


}
