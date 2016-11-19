package com.uoit.calvin.thesis2;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

public class TagActivity extends AppCompatActivity {

    public String tag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag);
        tag = getIntent().getStringExtra("tag");

        Toolbar toolBar = (Toolbar) findViewById(R.id.tagToolbar);
        if (toolBar != null) {
            toolBar.setTitle(tag);
        }
        setSupportActionBar(toolBar);
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }

        // set the title
        TransactionDBHelper transDB = new TransactionDBHelper(this);
        List<Transaction> transList = transDB.getTransByTag(tag);

        ArrayAdapter arrayAdapter = new ArrayAdapter<>(this, R.layout.activity_listview, transList);
        ListView transListView = (ListView) findViewById(R.id.transListView);
        if (transListView != null) {
            transListView.setAdapter(arrayAdapter);
            registerForContextMenu(transListView);
        }

        TextView textViewTotal = (TextView) findViewById(R.id.textViewTotal);
        TagDBHelper tagDBHelper = new TagDBHelper(this.getApplicationContext());
        List<Tag> tagList = tagDBHelper.getTagsList("*");
        for (Tag t : tagList) {
            if (t.toString().equals(tag)) {
                String text = "$" + t.getAmount();
                if (textViewTotal != null) {
                    textViewTotal.setText(text);
                }
            }
        }
    }

    public void clickDelete(View v) {
        TagDBHelper tagDB = new TagDBHelper(getApplicationContext());
        tagDB.deleteTag(tag);
        tagDB.close();
        finish();
    }
}


