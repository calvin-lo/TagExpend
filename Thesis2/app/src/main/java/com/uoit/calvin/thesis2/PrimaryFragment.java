package com.uoit.calvin.thesis2;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
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

/**
 * Created by calvin on 05/10/16.
 */

public class PrimaryFragment extends Fragment {

    DBHelper mydb;
    ListView transList;
    ListView idList;
    View x;
    static final int SAVING_DATA = 1;
    List<String> tags;
    List<Long> tagIds;


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

        //SimpleCursorAdapter mAdapter = new SimpleCursorAdapter(getContext(),);

        /**
         * Databases
         */
        displayTagList();

        return x;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Tag tag = new Tag();
        mydb = new DBHelper(getContext());
        if(requestCode == 1 && resultCode == getActivity().RESULT_OK) {
            tag.setTag(data.getExtras().getString("result"));
            mydb.addTransactions(tag);
        }
        mydb.close();

        displayTagList();
    }

    public void displayTagList() {
        mydb = new DBHelper(getContext());

        List<Tag> tag = mydb.getAllData();
        tags = new ArrayList<>();
        tagIds = new ArrayList<>();
        for (Tag t : tag) {
            tags.add(t.getTag());
        }
        for (Tag t : tag) {
            tagIds.add(t.getId());
        }
        // Set the tag
        ArrayAdapter arrayAdapter = new ArrayAdapter<>(getContext(), R.layout.activity_listview, tag);
        transList = (ListView) x.findViewById(R.id.transactionList);
        transList.setAdapter(arrayAdapter);
        registerForContextMenu(transList);

        // Set the ID
        ArrayAdapter arrayAdapterID = new ArrayAdapter<>(getContext(), R.layout.activity_listview,tagIds);
        idList = (ListView) x.findViewById(R.id.transactionListID);
        idList.setAdapter(arrayAdapterID);
        registerForContextMenu(idList);

        mydb.close();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId() == R.id.transactionList) {
            String[] menuItems = getResources().getStringArray(R.array.update_menu);
            for (int i = 0; i<menuItems.length; i++) {
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
        if (menuItemName.equals("Delete")) {
            ListView l = (ListView) x.findViewById(R.id.transactionListID);
            String ID = l.getItemAtPosition(info.position).toString();
            mydb = new DBHelper(getContext());
            mydb.deleteTransactions((Long.parseLong(ID)));
            displayTagList();
        }

        return true;
    }


}
