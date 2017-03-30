package com.uoit.calvin.thesis_2016;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

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

        Toolbar toolbar = (Toolbar) findViewById(R.id.setting_toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }

        final SharedPreferences sharedPreferences = context.getSharedPreferences(getString(R.string.shared_pref_name_main), Context.MODE_PRIVATE);

        final String[] settingList = getResources().getStringArray(R.array.setting_list);
        final int[] drawableIds = {
                R.drawable.ic_account_circle_black_24dp,
                R.drawable.twitter_24,
                R.drawable.twitter_24,
                R.drawable.twitter_24,
                R.drawable.ic_delete_forever_black_24dp,
                R.drawable.ic_delete_forever_black_24dp,
                R.drawable.ic_delete_forever_black_24dp
                };


        final boolean connected = sharedPreferences.getBoolean(getString(R.string.shared_pref_arg_twitter_connected), false);
        final SettingCustomAdapter adapter = new SettingCustomAdapter(this, settingList, drawableIds, connected);
        final ListView lv_setting = (ListView) findViewById(R.id.setting_lv);
        if (lv_setting != null) {
            lv_setting.setAdapter(adapter);
            registerForContextMenu(lv_setting);

            lv_setting.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView parent, View v, int position, long id) {
                    switch (position) {
                        // Reset Transaction Database
                        case 4:
                            deleteDatabase(getResources().getString(R.string.db_trans));
                            break;
                        //Reset Tag Database
                        case 5:
                            deleteDatabase(getResources().getString(R.string.db_tag));
                            break;
                        case 6:
                            deleteDatabase(getString(R.string.db_user));
                            break;
                        case 2:
                            loginTwitter();
                        case 3:
                            logoutTwitter();
                            break;
                        case 0:
                            setDisplayName();
                            break;

                    }
                    boolean connected2= sharedPreferences.getBoolean(getString(R.string.shared_pref_arg_twitter_connected), false);
                    adapter.update(connected2);
                }
            });
        }
    }

    public void setDisplayName() {

        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_set_name);
        final EditText et_dialog = (EditText) dialog.findViewById(R.id.dialog_set_name_et);
        Button button_ok = (Button) dialog.findViewById(R.id.dialog_set_name_ok);
        Button button_cancel = (Button) dialog.findViewById(R.id.dialog_set_name_cancel);
        button_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newUser = et_dialog.getText().toString();
                new Helper(context).setDefaultDisplayName(newUser);

                // Update the username
                TransactionDBHelper transactionDBHelper = new TransactionDBHelper(context);
                TagDBHelper tagDBHelper = new TagDBHelper(context);
                UserDBHelper userDBHelper = new UserDBHelper(context);

                User user = new User(context, newUser, getString(R.string.user_default));
                user.setSinceID(-1);

                userDBHelper.updateUser(user);
                transactionDBHelper.updateUser(user);
                tagDBHelper.updateUser(user);

                Toast.makeText(context, getString(R.string.dialog_success), Toast.LENGTH_SHORT).show();
                dialog.dismiss();

                tagDBHelper.close();
                transactionDBHelper.close();
                userDBHelper.close();
            }
        });
        button_cancel.setOnClickListener(new View.OnClickListener() {
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
                    Toast.makeText(context, getString(R.string.twitter_msg_connected), Toast.LENGTH_SHORT).show();
                    helper.setTwitterConnected(true);
                }

                @Override
                public void failure(TwitterException exception) {
                    Toast.makeText(context, getString(R.string.twitter_msg_connect_fail), Toast.LENGTH_SHORT).show();
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
            Toast.makeText(this, getString(R.string.twitter_msg_disconnected), Toast.LENGTH_SHORT).show();
            helper.setTwitterConnected(false);
        } else {
            Toast.makeText(this, getString(R.string.twitter_msg_no_session), Toast.LENGTH_SHORT).show();
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
        loginButton.onActivityResult(requestCode, resultCode, data);
    }

}
