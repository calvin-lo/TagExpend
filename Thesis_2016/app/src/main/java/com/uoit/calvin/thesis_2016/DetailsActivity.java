package com.uoit.calvin.thesis_2016;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import java.util.List;

public class DetailsActivity extends AppCompatActivity {


    private Transaction transaction;
    private static final int RESULT_OK = 1;
    private static final int SAVING_DATA = 1;
    String username;
    long id;
    TransactionDBHelper transactionDBHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        // get the transaction
        SharedPreferences sharedpreferences = getSharedPreferences("USER", Context.MODE_PRIVATE);
        username = sharedpreferences.getString("username", null);
        id = getIntent().getLongExtra("ID", 0);
        transactionDBHelper = new TransactionDBHelper(this);
        transaction = transactionDBHelper.getTransByID(id, username);

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
        transaction = transactionDBHelper.getTransByID(id, username);

        // user
        TextView nameTV = (TextView) findViewById(R.id.nameTV);
        if (nameTV != null) {
            nameTV.setText(transaction.getUser().getDisplayName());
        }

        TextView userTV = (TextView) findViewById(R.id.userTV);
        if (userTV != null) {
            String s = "@" + transaction.getUser().getUsername();
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
        if (!username.equals(getResources().getString(R.string.default_user))) {
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
        Intent intent = new Intent(this, FormActivity.class);
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

                List<Tag> tagList = helper.parseTag(transDB.getTransByID(id, username).getMessage(), username);

                SharedPreferences sharedpreferences = getSharedPreferences("USER", Context.MODE_PRIVATE);
                User user = new User(this,
                        sharedpreferences.getString("defaultUsername", getResources().getString(R.string.default_user)),
                        getResources().getString(R.string.default_user));

                for (Tag t : tagList) {
                    t.setUser(user);
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
                transaction.setUser(user);
                transDB.updateTransaction(transaction);

                // add the tag to tag cloud
                for (Tag t : transaction.getTagsList()) {
                    t.setUser(user);
                    tagDB.addTag(t);
                }

                transDB.close();
                tagDB.close();

            }
        }
        display();
    }

    public void delete(View v) {

        new Helper(this).deleteTrans(id, username);

        finish();

    }
}
