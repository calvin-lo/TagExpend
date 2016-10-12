package com.uoit.calvin.thesis2;

import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.MultiAutoCompleteTextView;

public class FormActivity extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);
    }

    public void clickCamera(View v) {
        dispatchTakePictureIntent();
    }

    public void clickSave(View v) {
        Intent returnIntent = new Intent();
        MultiAutoCompleteTextView input = (MultiAutoCompleteTextView) findViewById(R.id.tagInput);
        String result = input.getText().toString();
        returnIntent.putExtra("result",result);
        setResult(RESULT_OK,returnIntent);
        finish();
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
            viewer.setVisibility(View.VISIBLE);
            viewer.setImageBitmap(imageBitmap);
        }

    }



}
