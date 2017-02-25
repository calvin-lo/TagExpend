package com.uoit.calvin.thesis_2016;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import io.fabric.sdk.android.Fabric;

public class SettingActivity extends AppCompatActivity {

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "PvpXWnRXyy9ggCnhsh26aGOLc";
    private static final String TWITTER_SECRET = "VX0q8UBgeIiT4UT5fzygdfq49xiwWzTOoL0wKxZaGs0sg7Qjfy";
    Context context;
    Helper helper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        this.context = this;

        final Helper helper = new Helper(this);

        Toolbar myChildToolbar = (Toolbar) findViewById(R.id.settingToolbar);
        setSupportActionBar(myChildToolbar);
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }

        String[] settingList = getResources().getStringArray(R.array.setting_list);
        int[] drawableIds = {R.drawable.ic_delete_forever_black_24dp, R.drawable.ic_delete_forever_black_24dp, R.drawable.twitter, R.drawable.ic_perm_contact_calendar_black_24dp};


        final CustomAdapter adapter = new CustomAdapter(this,  settingList, drawableIds);
        ListView settingListView = (ListView) findViewById(R.id.settingList);
        if (settingListView != null) {
            settingListView.setAdapter(adapter);
            registerForContextMenu(settingListView);

            settingListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView parent, View v, int position, long id){
                    switch (position) {
                        // Reset Transaction Database
                        case 0:
                            deleteDatabase(getResources().getString(R.string.transDB));
                            break;
                        //Reset Tag Database
                        case 1:
                            deleteDatabase(getResources().getString(R.string.tagDB));
                            break;
                        case 2:
                            TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
                            Fabric.with(getApplicationContext(), new Twitter(authConfig));
                            Intent intent = new Intent(getApplicationContext(), LoginWithTwitterActivity.class);
                            startActivity(intent);
                            break;
                        case 3:

                            final EditText input = new EditText(context);
                            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    LinearLayout.LayoutParams.MATCH_PARENT);
                            input.setLayoutParams(lp);

                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setTitle(R.string.dialog_updateUser)
                                    .setView(input)
                                    .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            String newUser;
                                            newUser = input.getText().toString();

                                            TransactionDBHelper transactionDBHelper = new TransactionDBHelper(context);
                                            TagDBHelper tagDBHelper = new TagDBHelper(context);
                                            SharedPreferences sharedpreferences;
                                            sharedpreferences = getSharedPreferences("USER", Context.MODE_PRIVATE);
                                            String oldUser = sharedpreferences.getString("defaultUser", getResources().getString(R.string.default_user));

                                            helper.setDefaultUser(newUser);
                                            helper.setUser(getResources().getString(R.string.default_user));
                                            transactionDBHelper.updateUser(newUser, oldUser);
                                            tagDBHelper.updateUser(newUser, oldUser);

                                        }
                                    })
                                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                        }
                                    });

                            AlertDialog dialog = builder.create();
                            dialog.show();
                            break;
                    }

                }
            });
        }


    }
}
