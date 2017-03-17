package com.uoit.calvin.thesis_2016;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;

import android.graphics.drawable.ColorDrawable;

import android.os.AsyncTask;
import android.os.Bundle;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.text.InputType;

import android.view.LayoutInflater;
import android.view.View;

import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Toast;

import com.android.colorpicker.ColorPickerPalette;
import com.android.colorpicker.ColorPickerSwatch;
import com.twitter.sdk.android.Twitter;

import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;

import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.services.StatusesService;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;

import java.io.IOException;

import java.util.concurrent.ThreadLocalRandom;

import io.fabric.sdk.android.Fabric;
import retrofit2.Call;
import retrofit2.Response;

public class FormActivity extends AppCompatActivity{

    final int RESULT_OK = 1;
    Helper helper;
    MultiAutoCompleteTextView tv_input;

    int selectedColor;
    int colors[];


    ActionBar ab;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);
        Toolbar toolbar = (Toolbar) findViewById(R.id.form_toolbar);
        setSupportActionBar(toolbar);
        ab = getSupportActionBar();
        helper = new Helper(this);
        colors = helper.getMaterialColor();
        int randomNum = ThreadLocalRandom.current().nextInt(0, colors.length);
        selectedColor = colors[randomNum];
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setBackgroundDrawable(new ColorDrawable(selectedColor));
            ab.setHomeAsUpIndicator(getDrawable(R.drawable.ic_clear_white_24dp));
        }

        if (toolbar != null) {
            toolbar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    selectColor(view);
                }
            });

        }
        TwitterAuthConfig authConfig =  new TwitterAuthConfig("consumerKey", "consumerSecret");
        Fabric.with(this, new TwitterCore(authConfig), new TweetComposer());

        String original = getIntent().getStringExtra("original");

        tv_input = (MultiAutoCompleteTextView) findViewById(R.id.form_tv_input);

        if (tv_input != null && original != null) {
            tv_input.setText(original);
            tv_input.setSelection(tv_input.getText().length());
        }



        tv_input.setTokenizer(new SpaceTokenizer());
        TagDBHelper tagDBHelper = new TagDBHelper(this);
        String tagList[] = tagDBHelper.getTagsStringList("*");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, tagList);
        tv_input.setAdapter(adapter);
        tv_input.setThreshold(1);

        SharedPreferences sharedpreferences = getSharedPreferences("MAIN", Context.MODE_PRIVATE);
        if (sharedpreferences.getBoolean("autoPost", false)) {
            ImageButton ib_twitter = (ImageButton) findViewById(R.id.button_twitter);
            if (ib_twitter != null) {
                ib_twitter.setVisibility(View.GONE);
            }
        }
    }

    public void clickSymbol(String type) {
        if (tv_input != null) {
            String code = "";
            switch (type) {
                case "star":
                    code = getResources().getString(R.string.icon_general);
                    break;
                case "at":
                    code = getResources().getString(R.string.icon_location);
                    break;
                case "dollar":
                    code = getResources().getString(R.string.icon_dollar);
                    break;
                case "category":
                    code = getResources().getString(R.string.icon_category);
                    break;
                default:
                    code = getResources().getString(R.string.icon_general);
                    break;

            }

            int pos_start = tv_input.getSelectionStart();
            int pos_end = tv_input.getSelectionEnd();
            int length = tv_input.getText().toString().length();
            String oldString = tv_input.getText().toString();
            String oldString_start = tv_input.getText().toString().substring(0, pos_start);
            String oldString_end = tv_input.getText().toString().substring(pos_end, length);
            String newString;
            if (length == 0) {
                newString = code;
                tv_input.setText(newString);
                tv_input.setSelection(1);
            } else if (pos_end == 0) {
                newString = oldString_start + code + oldString_end;
                tv_input.setText(newString);
                tv_input.setSelection(pos_end + 1);
            } else if (oldString.charAt(pos_end-1) != ' '){
                newString = oldString_start + " " + code + oldString_end;
                tv_input.setText(newString);
                tv_input.setSelection(pos_end + 2);
            } else {
                newString = oldString_start + code + oldString_end;
                tv_input.setText(newString);
                tv_input.setSelection(pos_end + 1);
            }

        }
    }

    public void clickSave(View v) {
        Intent returnIntent = new Intent();
        if (tv_input != null) {
            String trans = tv_input.getEditableText().toString();
            returnIntent.putExtra("trans", trans );
            returnIntent.putExtra("color", selectedColor);
            setResult(this.RESULT_OK, returnIntent);

            // tweet
            SharedPreferences sharedpreferences = getSharedPreferences("MAIN", Context.MODE_PRIVATE);
            if (sharedpreferences.getBoolean("autoPost", false)) {
                tweet();
            }

            finish();
        }
    }

    public void clickStar(View v) {
        tv_input.setRawInputType(InputType.TYPE_CLASS_TEXT);
        clickSymbol("star");
    }

    public void clickDollar(View v) {
        tv_input.setRawInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL);
        clickSymbol("dollar");
    }

    public void clickAt(View v) {
        tv_input.setRawInputType(InputType.TYPE_CLASS_TEXT);
        clickSymbol("at");
    }

    public void clickCategory(View v) {
        tv_input.setRawInputType(InputType.TYPE_CLASS_TEXT);
        clickSymbol("category");
    }

    public void clickTwitter(View v) {
        tv_input.setRawInputType(InputType.TYPE_CLASS_TEXT);
        //tweet();
        String trans = tv_input.getEditableText().toString();
        trans = trans + " - #MyMoneyTag";
        TweetComposer.Builder builder = new TweetComposer.Builder(this)
                .text(trans);
        builder.show();
    }

    public void tweet() {
        String trans = tv_input.getEditableText().toString();
        trans = trans + " - #MyMoneyTag";
        TwitterSession session = TwitterCore.getInstance().getSessionManager().getActiveSession();
        if (session != null) {
            new updateTweets(session).execute(trans);
        } else {
            Toast toast = Toast.makeText(this, getString(R.string.twitter_msg_not_login), Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    public void selectColor(View v) {
        int columns = 5;

        LayoutInflater layoutInflater = LayoutInflater.from(this);
        final ColorPickerPalette colorPickerPalette = (ColorPickerPalette) layoutInflater
                .inflate(R.layout.custom_picker, null);
        colorPickerPalette.init(colors.length, columns, new ColorPickerSwatch.OnColorSelectedListener() {
            @Override
            public void onColorSelected(int color) {
                colorPickerPalette.drawPalette(colors, color);
                selectedColor = color;
            }
        });
        colorPickerPalette.drawPalette(colors, selectedColor);

        AlertDialog alert = new AlertDialog.Builder(this, R.style.MyDialogTheme)
                .setTitle(R.string.dialog_title_color)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (ab != null) {
                            ab.setBackgroundDrawable(new ColorDrawable(selectedColor));
                        }

                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setView(colorPickerPalette)
                .create();
        alert.show();
    }

    private class updateTweets extends AsyncTask<String, Void, Boolean> {

        private TwitterSession session;
        public updateTweets(TwitterSession session) {
            this.session = session;
        }

        @Override
        protected Boolean doInBackground(String... params) {

            StatusesService statusesService = Twitter.getApiClient(session).getStatusesService();
            Call<Tweet> tweetsCall = statusesService.update(params[0], null, null, null, null, null, null, null, null);

            Response<Tweet> responses = null;
            try {
                responses = tweetsCall.execute();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return responses != null;

        }

        @Override
        protected void onPostExecute(Boolean result) {

        }

    }


}
