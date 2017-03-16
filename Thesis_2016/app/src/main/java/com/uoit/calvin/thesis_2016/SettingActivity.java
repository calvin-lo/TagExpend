package com.uoit.calvin.thesis_2016;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import java.util.ArrayList;

import io.fabric.sdk.android.Fabric;

public class SettingActivity extends AppCompatActivity {

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "PvpXWnRXyy9ggCnhsh26aGOLc";
    private static final String TWITTER_SECRET = "VX0q8UBgeIiT4UT5fzygdfq49xiwWzTOoL0wKxZaGs0sg7Qjfy";
    Context context;
    Helper helper;

    private TwitterLoginButton loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        this.context = this;

        helper = new Helper(this);

        Toolbar myChildToolbar = (Toolbar) findViewById(R.id.settingToolbar);
        setSupportActionBar(myChildToolbar);
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }

        final SharedPreferences sharedpreferences = context.getSharedPreferences("MAIN", Context.MODE_PRIVATE);

        final String[] settingList = getResources().getStringArray(R.array.setting_list);
        final int[] drawableIds = {
                R.drawable.ic_contacts_black_24dp,
                R.drawable.twitter_24,
                R.drawable.twitter_24,
                R.drawable.twitter_24,
                R.drawable.ic_delete_forever_black_24dp,
                R.drawable.ic_delete_forever_black_24dp
                };


        final boolean connected = sharedpreferences.getBoolean("twitterConnected", false);
        final SettingCustomAdapter adapter = new SettingCustomAdapter(this, settingList, drawableIds, connected);
        final ListView settingListView = (ListView) findViewById(R.id.settingList);
        if (settingListView != null) {
            settingListView.setAdapter(adapter);
            registerForContextMenu(settingListView);

            settingListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView parent, View v, int position, long id) {
                    switch (position) {
                        // Reset Transaction Database
                        case 4:
                            deleteDatabase(getResources().getString(R.string.transDB));
                            break;
                        //Reset Tag Database
                        case 5:
                            deleteDatabase(getResources().getString(R.string.tagDB));
                            break;
                        case 2:
                            loginTwitter();
                        case 3:
                            logoutTwitter();
                            break;
                        case 0:
                            setDisplayname();
                            break;

                    }
                    boolean connected2= sharedpreferences.getBoolean("twitterConnected", false);
                    adapter.update(connected2);
                }
            });
        }
    }

    public void setDisplayname() {

        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_set_name);
        final EditText editText = (EditText) dialog.findViewById(R.id.edit_name);
        Button okayButton = (Button) dialog.findViewById(R.id.dialogButtonOK);
        Button cancelButton = (Button) dialog.findViewById(R.id.dialogButtonCancel);
        okayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newUser = editText.getText().toString();
                new Helper(context).setDefaultUser(newUser);

                // Update the username
                TransactionDBHelper transactionDBHelper = new TransactionDBHelper(context);
                TagDBHelper tagDBHelper = new TagDBHelper(context);
                UserDBHelper userDBHelper = new UserDBHelper(context);

                User user = new User(context, newUser, getString(R.string.default_user));

                userDBHelper.updateUser(user);
                transactionDBHelper.updateUser(user);
                tagDBHelper.updateUser(user);

                Toast.makeText(context, getString(R.string.dialog_success), Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


        dialog.show();
    }

    public void loginTwitter() {
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(getApplicationContext(), new Twitter(authConfig));
        loginButton = (TwitterLoginButton) findViewById(R.id.twitter_login_button);
        if (loginButton != null) {
            loginButton.setCallback(new Callback<TwitterSession>() {
                @Override
                public void success(Result<TwitterSession> result) {
                    Toast.makeText(context, getString(R.string.twitter_connected_msg), Toast.LENGTH_SHORT).show();
                    helper.setTwitterConnected(true);
                }

                @Override
                public void failure(TwitterException exception) {
                    Toast.makeText(context, getString(R.string.twitter_connect_fail_msg), Toast.LENGTH_SHORT).show();
                }
            });
            loginButton.performClick();

        }
    }

    public void logoutTwitter() {
        TwitterSession twitterSession = TwitterCore.getInstance().getSessionManager().getActiveSession();
        if (twitterSession != null) {
            ClearCookies(getApplicationContext());
            Twitter.getSessionManager().clearActiveSession();
            Twitter.logOut();
            Toast.makeText(this, getString(R.string.twitter_disconnected_msg), Toast.LENGTH_SHORT).show();
            helper.setTwitterConnected(false);
        } else {
            Toast.makeText(this, getString(R.string.twitter_no_session_msg), Toast.LENGTH_SHORT).show();
        }
    }

    public static void ClearCookies(Context context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                CookieManager.getInstance().removeAllCookies(null);
                CookieManager.getInstance().flush();
            } else {
                CookieSyncManager cookieSyncMngr=CookieSyncManager.createInstance(context);
                cookieSyncMngr.startSync();
                CookieManager cookieManager=CookieManager.getInstance();
                cookieManager.removeAllCookie();
                cookieManager.removeSessionCookie();
                cookieSyncMngr.stopSync();
                cookieSyncMngr.sync();
            }
        }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Make sure that the loginButton hears the result from any
        // Activity that it triggered.
        loginButton.onActivityResult(requestCode, resultCode, data);
    }

}
