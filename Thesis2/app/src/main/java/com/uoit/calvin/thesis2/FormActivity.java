package com.uoit.calvin.thesis2;

import android.content.Intent;
import android.graphics.Bitmap;
import android.inputmethodservice.KeyboardView;
import android.provider.MediaStore;;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class FormActivity extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    final int RESULT_OK = 1;


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

        //String[] tagsList = new TagDBHelper(FormActivity.this.getApplicationContext()).getTagsStringList();
        //ArrayAdapter<String> adapter = new ArrayAdapter<>(FormActivity.this, android.R.layout.simple_list_item_1, tagsList);
        final MultiAutoCompleteTextView textView = (MultiAutoCompleteTextView) findViewById(R.id.tagInput);

        if (textView != null) {
            //textView.setAdapter(adapter);
            textView.setThreshold(0);
            textView.setTokenizer(new SpaceTokenizer());
        }

    }

    public void clickCamera(View v) {
        dispatchTakePictureIntent();
    }

    public void clickSave(View v) {
        Intent returnIntent = new Intent();
        MultiAutoCompleteTextView input = (MultiAutoCompleteTextView) findViewById(R.id.tagInput);
        if (input != null) {
            String trans = input.getText().toString();
            if (trans.matches("(([#@$])(.*))+")) {
                returnIntent.putExtra("trans", trans );
                setResult(this.RESULT_OK, returnIntent);
                finish();
            } else {
                Toast toast = Toast.makeText(this, getResources().getString(R.string.errorFormat), Toast.LENGTH_LONG);
                toast.show();
            }
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            ImageView viewer = (ImageView) findViewById(R.id.formImage);
            if (viewer != null) {
                viewer.setVisibility(View.VISIBLE);
                viewer.setImageBitmap(imageBitmap);
            }
        }

    }



}
