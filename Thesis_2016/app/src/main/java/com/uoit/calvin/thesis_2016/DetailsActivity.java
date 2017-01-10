package com.uoit.calvin.thesis_2016;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Interpolator;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DetailsActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener{


    private Transaction transaction;
    private String time;
    private String date;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        Toolbar myChildToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myChildToolbar);
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }

        TextView dateTV = (TextView) findViewById(R.id.dateTextView);
        TextView timeTV = (TextView) findViewById(R.id.timeTextView);
        TextView amountTV = (TextView) findViewById(R.id.amountTextView);

        // set default clickable to false
        if (dateTV != null) {
            dateTV.setClickable(false);
        }
        if (timeTV != null) {
            timeTV.setClickable(false);
        }
        if (amountTV != null) {
            amountTV.setClickable(false);
        }

        long id = getIntent().getLongExtra("ID", 0);
        TransactionDBHelper transactionDBHelper = new TransactionDBHelper(this);

        transaction = transactionDBHelper.getTransByID(id);

        // Amount
        if (amountTV != null) {
            String s = Float.toString(transaction.getAmount());
            amountTV.setText(s);
        }


        // Date
        Date d = transaction.getDate();

        SimpleDateFormat sdf = new SimpleDateFormat("EEE, MMM d, yyyy", Locale.CANADA);
        date = sdf.format(d);
        if (dateTV != null) {
            dateTV.setText(date);
        }

        SimpleDateFormat sdf2 = new SimpleDateFormat("h:mm a", Locale.CANADA);
        time = sdf2.format(d);
        if (timeTV != null) {
            timeTV.setText(time);
        }


        // Tags
        ArrayAdapter starAdapter = new ArrayAdapter<>(this, R.layout.list_item_center, transaction.getStarTagsList());
        ArrayAdapter locationAdapter = new ArrayAdapter<>(this, R.layout.list_item_center, transaction.getLocationTagsList());

        ListView starLV = (ListView) findViewById(R.id.starListView);
        ListView locationLV = (ListView) findViewById(R.id.locationListView);

        if (starLV != null) {
            starLV.setAdapter(starAdapter);
        }
        registerForContextMenu(starLV);

        if (locationLV != null) {
            locationLV.setAdapter(locationAdapter);
        }
        registerForContextMenu(locationLV);


    }

    public void save(View v) {

        String timestamp = date + " " + time;
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, MMM d, yyyy h:mm a", Locale.CANADA);
        Date d = null;
        try {
            d = sdf.parse(timestamp);
        }catch(Exception ex){
            ex.printStackTrace();
        }

        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.CANADA);
        timestamp = formatter.format(d);
        transaction.setTimestamp(timestamp);

        TransactionDBHelper transactionDBHelper = new TransactionDBHelper(this);
        transactionDBHelper.updateTransaction(transaction);

        FloatingActionButton saveButton = (FloatingActionButton) findViewById(R.id.saveButton);
        if (saveButton != null) {
            saveButton.setVisibility(View.GONE);
        }
        FloatingActionButton editButton = (FloatingActionButton) findViewById(R.id.editButton);
        if (editButton != null) {
            editButton.setVisibility(View.VISIBLE);
        }
        TextView dateTV = (TextView) findViewById(R.id.dateTextView);
        if (dateTV != null) {
            dateTV.setClickable(false);
        }
        TextView timeTV = (TextView) findViewById(R.id.timeTextView);
        if (timeTV != null) {
            timeTV.setClickable(false);
        }
        TextView amountTV = (TextView) findViewById(R.id.amountTextView);
        if (amountTV != null) {
            amountTV.setClickable(false);
        }
    }

    public void edit(View v) {
        FloatingActionButton saveButton = (FloatingActionButton) findViewById(R.id.saveButton);
        if (saveButton != null) {
            saveButton.setVisibility(View.VISIBLE);
        }
        FloatingActionButton editButton = (FloatingActionButton) findViewById(R.id.editButton);
        if (editButton != null) {
            editButton.setVisibility(View.GONE);
        }
        TextView dateTV = (TextView) findViewById(R.id.dateTextView);
        if (dateTV != null) {
            dateTV.setClickable(true);
        }
        TextView timeTV = (TextView) findViewById(R.id.timeTextView);
        if (timeTV != null) {
            timeTV.setClickable(true);
        }
        TextView amountTV = (TextView) findViewById(R.id.amountTextView);
        if (amountTV != null) {
            amountTV.setClickable(true);
        }
    }

    public void addTag(View v) {
        LayoutInflater layoutInflater = (LayoutInflater)getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = layoutInflater.inflate(R.layout.popup_addtag, null);
        final PopupWindow popupWindow = new PopupWindow(popupView, ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT);
        RelativeLayout mRelativeLayout = (RelativeLayout) findViewById(R.id.activity_details);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.showAtLocation(mRelativeLayout, Gravity.CENTER,0,0);
    }


    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");

    }

    public void showTimePickerDialog(View v) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getSupportFragmentManager(), "timePicker");

    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        // Do something with the time chosen by the user

        String newTime = hourOfDay + ":" + minute;
        Date d = new Date();
        SimpleDateFormat format = new SimpleDateFormat("hh:mm", Locale.CANADA);
        try {
            d = format.parse(newTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        SimpleDateFormat sdf = new SimpleDateFormat("h:mm a", Locale.CANADA);
        time = sdf.format(d);

        TextView timeTV = (TextView) findViewById(R.id.timeTextView);
        if (timeTV != null) {
            timeTV.setText(time);
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        // Do something with the date chosen by the user

        String newDate =   day + "/" + (month + 1) + "/" + year;
        Date d = new Date();
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.CANADA);
        try {
            d = format.parse(newDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        SimpleDateFormat sdf = new SimpleDateFormat("EEE, MMM d, yyyy", Locale.CANADA);
        date = sdf.format(d);
        TextView dateTV = (TextView) findViewById(R.id.dateTextView);
        if (dateTV != null) {
            dateTV.setText(date);
        }

    }

    public static class DatePickerFragment extends DialogFragment {

        private Activity mActivity;
        private DatePickerDialog.OnDateSetListener mListener;

        @Override
        public void onAttach(Context context) {
            super.onAttach(context);

            Activity activity= null;

            if (context instanceof Activity){
                activity=(Activity) context;
            }

            mActivity = activity;

            // This error will remind you to implement an OnTimeSetListener
            //   in your Activity if you forget
            try {
                mListener = (DatePickerDialog.OnDateSetListener) activity;
            } catch (ClassCastException e) {
                throw new ClassCastException(activity.toString() + " must implement OnTimeSetListener");
            }
        }

        @Override
        @NonNull
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(mActivity, mListener, year, month, day);
        }

    }

    public static class TimePickerFragment extends DialogFragment  {

        private Activity mActivity;
        private TimePickerDialog.OnTimeSetListener mListener;

        @Override
        public void onAttach(Context context) {
            super.onAttach(context);

            Activity activity= null;

            if (context instanceof Activity){
                activity=(Activity) context;
            }

            mActivity = activity;

            // This error will remind you to implement an OnTimeSetListener
            //   in your Activity if you forget
            try {
                mListener = (TimePickerDialog.OnTimeSetListener) activity;
            } catch (ClassCastException e) {
                throw new ClassCastException(activity.toString() + " must implement OnTimeSetListener");
            }
        }

        @Override
        @NonNull
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(mActivity, mListener, hour, minute,
                    DateFormat.is24HourFormat(mActivity));
        }

    }
}
