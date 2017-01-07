package com.uoit.calvin.thesis_2016;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
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

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;

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
                ((MainActivity)getActivity()).setupTabLayout();
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

                    String input = data.getStringExtra("trans");
                    List<Tag> tagList = helper.parseTag(input);

                    Transaction trans = new Transaction();
                    trans.setTags(tagList);
                    trans.setTimestamp(helper.getCurrentTime());
                    trans.setAmout(helper.getAmount(input));
                    transDB.addTransactions(trans);

                    // add the tag to tag cloud
                    tagDB = new TagDBHelper(getContext().getApplicationContext());
                    for (Tag t : tagList) {
                        Log.i("MYTAGTYPE", t.getType());
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
      /*  // Set the transaction
        ArrayAdapter arrayAdapter = new ArrayAdapter<>(getContext(), R.layout.activity_listview, transList);
        SwipeMenuListView transListView = (SwipeMenuListView) x.findViewById(R.id.transactionList);
        //ListView transListView = (ListView) x.findViewById(R.id.transactionList);
        transListView.setAdapter(arrayAdapter);
        registerForContextMenu(transListView);

        SwipeMenuCreator creator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                //create an action that will be showed on swiping an item in the list
                SwipeMenuItem item1 = new SwipeMenuItem(
                        x.getContext());
                item1.setBackground(new ColorDrawable(Color.DKGRAY));
                // set width of an option (px)
                item1.setWidth(200);
                item1.setTitle("Action 1");
                item1.setTitleSize(18);
                item1.setTitleColor(Color.WHITE);
                menu.addMenuItem(item1);

                SwipeMenuItem item2 = new SwipeMenuItem(
                       x.getContext());
                // set item background
                item2.setBackground(new ColorDrawable(Color.RED));
                item2.setWidth(200);
                item2.setTitle("Action 2");
                item2.setTitleSize(18);
                item2.setTitleColor(Color.WHITE);
                menu.addMenuItem(item2);
            }
        };
        //set MenuCreator
        transListView.setMenuCreator(creator);
        // set SwipeListener
        transListView.setOnSwipeListener(new SwipeMenuListView.OnSwipeListener() {

            @Override
            public void onSwipeStart(int position) {
                // swipe start
            }

            @Override
            public void onSwipeEnd(int position) {
                // swipe end
            }
        });



        // Set the ID
        ArrayAdapter arrayAdapterID = new ArrayAdapter<>(getContext(), R.layout.activity_listview, transIds);
        ListView idList = (ListView) x.findViewById(R.id.transactionListID);
        idList.setAdapter(arrayAdapterID);
        registerForContextMenu(idList);
*/
        transDB.close();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
       /* if (v.getId() == R.id.transactionList) {
            String[] menuItems = getResources().getStringArray(R.array.update_menu);
            for (int i = 0; i < menuItems.length; i++) {
                menu.add(Menu.NONE, i, i, menuItems[i]);
            }
        }*/
    }


   @Override
    public boolean onContextItemSelected(MenuItem item) {
/*
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        int menuItemIndex = item.getItemId();
        String[] menuItems = getResources().getStringArray(R.array.update_menu);
        String menuItemName = menuItems[menuItemIndex];
        // Delete
        switch (menuItemName) {
            case "Delete":
                ListView l = (ListView) x.findViewById(R.id.transactionListID);
                String ID = l.getItemAtPosition(info.position).toString();
                Long id = Long.parseLong(ID);
                transDB = new TransactionDBHelper(getContext().getApplicationContext());

                Helper helper = new Helper();
                List<Tag> tagList = helper.parseTag(transDB.getTransByID(id).toString());

                transDB.deleteTransactions(id);

                // update tag cloud

                tagDB = new TagDBHelper(getContext().getApplicationContext());
                for (Tag t : tagList) {
                    tagDB.updateTag(t);
                }


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
*/

        return true;
    }


}
