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

public class  DetailsActivity extends AppCompatActivity {


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
        SharedPreferences sharedpreferences = getSharedPreferences(getString(R.string.shared_pref_name_user), Context.MODE_PRIVATE);
        username = sharedpreferences.getString(getString(R.string.shared_pref_arg_username), null);
        id = getIntent().getLongExtra(getString(R.string.intent_extra_id), 0);
        transactionDBHelper = new TransactionDBHelper(this);
        transaction = transactionDBHelper.getTransByID(id, username);

        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
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

        id = getIntent().getLongExtra(getString(R.string.intent_extra_id), 0);
        transactionDBHelper = new TransactionDBHelper(this);
        transaction = transactionDBHelper.getTransByID(id, username);

        // user
        TextView tv_display_name = (TextView) findViewById(R.id.detail_tv_display_name);
        if (tv_display_name != null) {
            tv_display_name.setText(transaction.getUser().getDisplayName());
        }

        TextView tv_username = (TextView) findViewById(R.id.detail_tv_username);
        if (tv_username != null) {
            String s = getString(R.string.icon_at) + transaction.getUser().getUsername();
            tv_username.setText(s);
        }

        // message
        TextView tv_msg = (TextView) findViewById(R.id.detail_tv_msg);
        if (tv_msg != null) {
            tv_msg.setText(transaction.getMessage());
        }

        // time
        TextView tv_time = (TextView) findViewById(R.id.detail_tv_time);
        if (tv_time != null) {
            tv_time.setText(transaction.getTimestamp());
        }

        // delete button
        ImageButton ib_delete = (ImageButton) findViewById(R.id.detail_ib_delete);
        ImageButton ib_update = (ImageButton) findViewById(R.id.detail_ib_update);
        if (!username.equals(getResources().getString(R.string.user_default))) {
            if (ib_delete != null) {
                ib_delete.setVisibility(View.GONE);
            }
            if (ib_update != null) {
                ib_update.setVisibility(View.GONE);
            }
        }

        // Tag

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.detail_recycler_view);
        if (recyclerView != null) {
            recyclerView.setHasFixedSize(true);

            StaggeredGridLayoutManager GridLayoutManager = new StaggeredGridLayoutManager(3, 1);
            recyclerView.setLayoutManager(GridLayoutManager);

            List<Tag> tagsList = transaction.getTagsList();

            SolventRecyclerViewAdapter rcAdapter = new SolventRecyclerViewAdapter(this, tagsList);
            recyclerView.setAdapter(rcAdapter);
        }

    }

    public void update(View v) {
        Intent intent = new Intent(this, FormActivity.class);
        intent.putExtra(getString(R.string.intent_extra_original_msg), transaction.getMessage());
        startActivityForResult(intent, SAVING_DATA);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {
            if (requestCode == SAVING_DATA) {
                TransactionDBHelper transactionDBHelper = new TransactionDBHelper(this);
                TagDBHelper tagDBHelper = new TagDBHelper(this);
                Helper helper = new Helper(this);
                String time = transaction.getTimestamp();

                List<Tag> tagList = helper.parseTag(transactionDBHelper.getTransByID(id, username).getMessage(), username);

                SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.shared_pref_name_user), Context.MODE_PRIVATE);
                User user = new User(this,
                        sharedPreferences.getString(getString(R.string.shared_pref_arg_default_display_name), getString(R.string.user_default)),
                        getResources().getString(R.string.user_default));

                for (Tag t : tagList) {
                    t.setUser(user);
                    tagDBHelper.updateTag(t);
                }



                String message = data.getStringExtra(getString(R.string.intent_extra_trans));

                transaction.setMessage(message);
                transaction.setTags(helper.parseTag(message));
                transaction.setGeneral(helper.parseGeneral(message));
                transaction.setLocation(helper.parseLocation(message));
                transaction.setCategory(helper.parseCategory(message));
                transaction.setTimestamp(time);
                transaction.setAmount(helper.getAmount(message));
                transaction.setUser(user);
                transactionDBHelper.updateTransaction(transaction);

                // add the tag to tag cloud
                for (Tag t : transaction.getTagsList()) {
                    t.setUser(user);
                    tagDBHelper.addTag(t);
                }

                transactionDBHelper.close();
                tagDBHelper.close();

            }
        }
        display();
    }

    public void delete(View v) {

        new Helper(this).deleteTrans(id, username);

        finish();

    }
}
