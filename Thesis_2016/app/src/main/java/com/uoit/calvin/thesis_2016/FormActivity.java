package com.uoit.calvin.thesis_2016;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.colorpicker.ColorPickerDialog;
import com.android.colorpicker.ColorPickerPalette;
import com.android.colorpicker.ColorPickerSwatch;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;

import java.util.concurrent.ThreadLocalRandom;

import io.fabric.sdk.android.Fabric;

public class FormActivity extends AppCompatActivity{

    final int RESULT_OK = 1;
    Helper helper;
    EditText input;

    int selectedColor;
    int colors[];
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
        colors = helper.getColorArray();
        int randomNum = ThreadLocalRandom.current().nextInt(0, colors.length);
        selectedColor = colors[randomNum];
        Button colorButton = (Button) findViewById(R.id.colorButton);
        if (colorButton != null) {
            colorButton.setBackgroundColor(selectedColor);
        }
    }

    public void clickSave(View v) {
        Intent returnIntent = new Intent();
        if (input != null) {
            String trans = input.getEditableText().toString();
            returnIntent.putExtra("trans", trans );
            returnIntent.putExtra("color", selectedColor);
            setResult(this.RESULT_OK, returnIntent);
            finish();
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
                .setTitle(R.string.color_title)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Button colorButton = (Button) findViewById(R.id.colorButton);
                        if (colorButton != null) {
                            colorButton.setBackgroundColor(selectedColor);
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


}
