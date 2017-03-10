package com.uoit.calvin.thesis_2016;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
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
import android.widget.ImageButton;
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
    private static final int RESULT_OK = 1;
    private static final int SAVING_DATA = 1;
    String user;
    long id;
    TransactionDBHelper transactionDBHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        // get the transaction
        SharedPreferences sharedpreferences = getSharedPreferences("USER", Context.MODE_PRIVATE);
        user = sharedpreferences.getString("user", null);
        id = getIntent().getLongExtra("ID", 0);
        transactionDBHelper = new TransactionDBHelper(this);
        transaction = transactionDBHelper.getTransByID(id, user);

        Toolbar toolbar = (Toolbar) findViewById(R.id.detailToolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setBackgroundDrawable(new ColorDrawable(transaction.getColor()));
            ab.setDisplayShowTitleEnabled(false);
            ab.setHomeAsUpIndicator(getDrawable(R.drawable.ic_clear_white_24dp));
        }

        display();
    }

    public void display() {

        id = getIntent().getLongExtra("ID", 0);
        transactionDBHelper = new TransactionDBHelper(this);
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

        // delete button
        ImageButton deleteButton = (ImageButton) findViewById(R.id.deleteButton);
        ImageButton updateButton = (ImageButton) findViewById(R.id.updateButton);
        if (!user.equals(getResources().getString(R.string.default_user))) {
            if (deleteButton != null) {
                deleteButton.setVisibility(View.GONE);
            }
            if (updateButton != null) {
                updateButton.setVisibility(View.GONE);
            }
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

    public void update(View v) {
        Intent intent = new Intent(this, ActivityUpdate.class);
        intent.putExtra("original", transaction.getMessage());
        startActivityForResult(intent, SAVING_DATA);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {
            if (requestCode == SAVING_DATA) {
                TransactionDBHelper transDB = new TransactionDBHelper(this);
                TagDBHelper tagDB = new TagDBHelper(this);
                Helper helper = new Helper(this);
                String time = transaction.getTimestamp();

                List<Tag> tagList = helper.parseTag(transDB.getTransByID(id, user).getMessage(), user);

                //transDB.deleteTransactions(id);

                for (Tag t : tagList) {
                    tagDB.updateTag(t);
                }



                String message = data.getStringExtra("trans");

                transaction.setMessage(message);
                transaction.setTags(helper.parseTag(message));
                transaction.setGeneral(helper.parseGeneral(message));
                transaction.setLocation(helper.parseLocation(message));
                transaction.setCategory(helper.parseCategory(message));
                transaction.setTimestamp(time);
                transaction.setAmount(helper.getAmount(message));
                SharedPreferences sharedpreferences = this.getSharedPreferences("USER", Context.MODE_PRIVATE);;
                transaction.setName(sharedpreferences.getString("defaultUser", getResources().getString(R.string.default_user)));
                transaction.setUser(getResources().getString(R.string.default_user));
                transDB.updateTransaction(transaction);

                // add the tag to tag cloud
                for (Tag t : transaction.getTagsList()) {
                    t.setName(sharedpreferences.getString("defaultUser", getResources().getString(R.string.default_user)));
                    t.setUser(getResources().getString(R.string.default_user));
                    tagDB.addTag(t);
                }

                transDB.close();
                tagDB.close();

            }
        }
        display();
    }

    public void delete(View v) {

        TransactionDBHelper transDB = new TransactionDBHelper(this);

        Helper helper = new Helper(this);
        List<Tag> tagList = helper.parseTag(transDB.getTransByID(id, user).getMessage(), user);

        transDB.deleteTransactions(id);

        // update tag cloud
        TagDBHelper tagDB = new TagDBHelper(this);
        for (Tag t : tagList) {
            tagDB.updateTag(t);
        }
        transDB.close();
        tagDB.close();

        finish();

    }
}
