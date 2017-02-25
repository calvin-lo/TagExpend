package com.uoit.calvin.thesis_2016;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Interpolator;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DetailsActivity extends AppCompatActivity {


    private Transaction transaction;
    String user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        Toolbar toolbar = (Toolbar) findViewById(R.id.detailToolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }

        SharedPreferences sharedpreferences = getSharedPreferences("USER", Context.MODE_PRIVATE);
        user = sharedpreferences.getString("user", null);

        // get the transaction
        long id = getIntent().getLongExtra("ID", 0);
        TransactionDBHelper transactionDBHelper = new TransactionDBHelper(this);
        transaction = transactionDBHelper.getTransByID(id, user);

        // user
        TextView nameTV = (TextView) findViewById(R.id.nameTV);
        if (nameTV != null) {
            nameTV.setText(transaction.getName());
        }

        TextView userTV = (TextView) findViewById(R.id.userTV);
        if (userTV != null) {
            String s = "@" + transaction.getUser();
            userTV.setText(s);
        }

        // message
        TextView msgTV = (TextView) findViewById(R.id.messageTextView);
        if (msgTV != null) {
            msgTV.setText(transaction.getMessage());
        }

        // time
        TextView timeTV = (TextView) findViewById(R.id.timeTextView);
        if (timeTV != null) {
            timeTV.setText(transaction.getTimestamp());
        }

        // Tag

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);

        StaggeredGridLayoutManager GridLayoutManager = new StaggeredGridLayoutManager(3, 1);
        recyclerView.setLayoutManager(GridLayoutManager);

        List<Tag> tagList = transaction.getTagsList();

        SolventRecyclerViewAdapter rcAdapter = new SolventRecyclerViewAdapter(this, tagList);
        recyclerView.setAdapter(rcAdapter);





    }
}
