package com.uoit.calvin.thesis_2016;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;
import io.fabric.sdk.android.Fabric;

public class FormActivity extends AppCompatActivity{

    final int RESULT_OK = 1;
    Helper helper;
    EditText input;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);
        Toolbar myChildToolbar = (Toolbar) findViewById(R.id.formToolbar);
        setSupportActionBar(myChildToolbar);
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }

        TwitterAuthConfig authConfig =  new TwitterAuthConfig("consumerKey", "consumerSecret");
        Fabric.with(this, new TwitterCore(authConfig), new TweetComposer());

        helper = new Helper(this);
        input = (EditText) findViewById(R.id.tagInput);
    }

    public void clickSave(View v) {
        Intent returnIntent = new Intent();
        if (input != null) {
            String trans = input.getEditableText().toString();
            boolean format = true;
            /*if (trans.length() >= 6) {
                format = trans.substring(0,6).equals(CURRENCY_SIGN);
            }
            if (trans.length() >= 7 && !format) {
                format = trans.substring(0,7).equals(STAR_SIGN);
            }
            if (trans.length() >= 8 && !format) {
                format = trans.substring(0,8).equals(LOCATION_SIGN);
            }*/

            if (format) {
                returnIntent.putExtra("trans", trans );
                setResult(this.RESULT_OK, returnIntent);
                finish();
            } else {
                Toast toast = Toast.makeText(this, getResources().getString(R.string.errorFormat), Toast.LENGTH_LONG);
                toast.show();
            }
        }
    }

    public void clickStar(View v) {
        input.setRawInputType(InputType.TYPE_CLASS_TEXT);
        clickSymbol("star");
    }

    public void clickDollar(View v) {
        input.setRawInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL);
        clickSymbol("dollar");
    }

    public void clickAt(View v) {
        input.setRawInputType(InputType.TYPE_CLASS_TEXT);
        clickSymbol("at");
    }

    public void clickCategory(View v) {
        input.setRawInputType(InputType.TYPE_CLASS_TEXT);
        clickSymbol("category");
    }

    public void clickTwitter(View v) {
        input.setRawInputType(InputType.TYPE_CLASS_TEXT);
        String trans = input.getEditableText().toString();
        trans = trans + " - #MyMoneyTag";
        TweetComposer.Builder builder = new TweetComposer.Builder(this)
                .text(trans);
        builder.show();
    }

    public void clickSymbol(String type) {
        if (input != null) {
            String code = "";
            switch (type) {
                case "star":
                    code = getResources().getString(R.string.generalIcon);
                    break;
                case "at":
                    code = getResources().getString(R.string.locationIcon);
                    break;
                case "dollar":
                    code = getResources().getString(R.string.dollarIcon);
                    break;
                case "category":
                    code = getResources().getString(R.string.categoryIcon);
                    break;
                default:
                    code = getResources().getString(R.string.generalIcon);
                    break;

            }


            String oldString = input.getText().toString();
            String newString = oldString + code;
            input.setText(newString);

            input.setSelection(newString.length());
        }
    }




}
