package com.uoit.calvin.thesis2;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class SettingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        Toolbar myChildToolbar = (Toolbar) findViewById(R.id.settingToolbar);
        setSupportActionBar(myChildToolbar);
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }

        List<String> settingList = new ArrayList<>();
        settingList.add("Reset Transaction Database");
        settingList.add("Reset Tag Database");
;
        ArrayAdapter arrayAdapter = new ArrayAdapter<>(this, R.layout.activity_listview, settingList);
        ListView settingListView = (ListView) findViewById(R.id.settingList);
        if (settingListView != null) {
            settingListView.setAdapter(arrayAdapter);
            registerForContextMenu(settingListView);

            settingListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView parent, View v, int position, long id){
                    switch (position) {
                        // Reset Transaction Database
                        case 0:
                            deleteDatabase("transDB");
                            break;
                        //Reset Tag Database
                        case 1:
                            deleteDatabase("tagCloudDB");
                            break;
                    }

                }
            });
        }


    }
}
