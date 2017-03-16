package com.uoit.calvin.thesis_2016;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

class Helper implements ImageListener{

    private String GENERAL_ICON;
    private String LOCATION_ICON;
    private String DOLLAR_ICON;
    private String CATEGORY_ICON;
    private String format;
    private Context context;

    Helper(Context context) {
        this.context = context;
        GENERAL_ICON = context.getResources().getString(R.string.generalIcon);
        LOCATION_ICON = context.getResources().getString(R.string.locationIcon);
        DOLLAR_ICON = context.getResources().getString(R.string.dollarIcon);
        CATEGORY_ICON = context.getResources().getString(R.string.categoryIcon);
        format = "(?=" + GENERAL_ICON + "|" + LOCATION_ICON + "|"+ "\\" + DOLLAR_ICON + "|" + CATEGORY_ICON + ")";

    }

    List<Tag> parseTag(String message) {
        List<Tag> tags = new ArrayList<>();
        message = message.replace(" ", "");
        String parsedTags[] =  message.split(format);
        float amount = 0;

        for (int i = 1; i < parsedTags.length; i++) {
            if (parsedTags[i].substring(0,1).equals(DOLLAR_ICON)) {
                amount = amount + Float.parseFloat(parsedTags[i].substring(1, parsedTags[i].length()));
            }
        }

        for (int i = 1; i < parsedTags.length; i++) {
            if (parsedTags[i].substring(0,1).equals(GENERAL_ICON) && parsedTags[i].length()>1) {
                tags.add(new Tag(parsedTags[i].substring(1,parsedTags[i].length()), GENERAL_ICON, amount));
            }
            if (parsedTags[i].substring(0,1).equals(LOCATION_ICON) && parsedTags[i].length()>1 ) {
                tags.add(new Tag(parsedTags[i].substring(1,parsedTags[i].length()), LOCATION_ICON, amount));
            }
            if (parsedTags[i].substring(0,1).equals(CATEGORY_ICON) && parsedTags[i].length()>1) {
                tags.add(new Tag(parsedTags[i].substring(1, parsedTags[i].length()), CATEGORY_ICON, amount));
            }

        }

        return tags;
    }

    List<Tag> parseTag(String message, String username) {
        List<Tag> tags = new ArrayList<>();
        message = message.replace(" ", "");
        String parsedTags[] =  message.split(format);
        float amount = 0;

        for (int i = 1; i < parsedTags.length; i++) {
            if (parsedTags[i].substring(0,1).equals(DOLLAR_ICON)) {
                amount = amount + Float.parseFloat(parsedTags[i].substring(1, parsedTags[i].length()));
            }
        }

        UserDBHelper userDBHelper = new UserDBHelper(context);
        User user = userDBHelper.getUserNyUsername(username);

        for (int i = 1; i < parsedTags.length; i++) {
            if (parsedTags[i].substring(0,1).equals(GENERAL_ICON)  && parsedTags[i].length()>1 ) {
                Tag t = new Tag(parsedTags[i].substring(1,parsedTags[i].length()), GENERAL_ICON, amount);
                //t.setUser(user);
                tags.add(t);
            }
            if (parsedTags[i].substring(0,1).equals(LOCATION_ICON) && parsedTags[i].length()>1) {
                Tag t = new Tag(parsedTags[i].substring(1,parsedTags[i].length()), LOCATION_ICON, amount);
                //t.setUser(user);
                tags.add(t);
            }
            if (parsedTags[i].substring(0,1).equals(CATEGORY_ICON) && parsedTags[i].length()>1) {
                Tag t = new Tag(parsedTags[i].substring(1, parsedTags[i].length()), CATEGORY_ICON, amount);
                //t.setUser(user);
                tags.add(t);
            }

        }

        return tags;
    }

    String parseGeneral(String message) {
        String general = "";
        String parsedTags[] = message.split(format);
        for (int i = 1; i < parsedTags.length; i++) {
            if (parsedTags[i].substring(0,1).equals(GENERAL_ICON)) {
                general = general + GENERAL_ICON + parsedTags[i].substring(1, parsedTags[i].length());
            }
        }
        return general;
    }

    String parseLocation(String message) {
        String location = "";
        String parsedTags[] = message.split(format);
        for (int i = 1; i < parsedTags.length; i++) {
            if (parsedTags[i].substring(0,1).equals(LOCATION_ICON)) {
                location = location + LOCATION_ICON + parsedTags[i].substring(1, parsedTags[i].length());
            }
        }
        return location;
    }

    String parseCategory(String message) {
        String category = "";
        String parsedTags[] = message.split(format);
        for (int i = 1; i < parsedTags.length; i++) {
            if (parsedTags[i].substring(0,1).equals(CATEGORY_ICON)) {
                category = category + CATEGORY_ICON + parsedTags[i].substring(1, parsedTags[i].length());
            }
        }
        return category;
    }

    public float getAmount(String message) {
        String parsedTags[] =  message.split(format);
        float amount = 0;

        for (int i = 1; i < parsedTags.length; i++) {
            if (parsedTags[i].substring(0,1).equals(DOLLAR_ICON)) {
                amount = amount + Float.parseFloat(parsedTags[i].substring(1, parsedTags[i].length()));
            }
        }

        return amount;
    }

    List<String> tagListToString(List<Tag> tagsList) {
        List<String> tagStrList = new ArrayList<>();
        for (Tag t : tagsList) {
            tagStrList.add(t.toString());
        }
        return tagStrList;
    }

    String getCurrentTime() {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.CANADA);
        Date date = new Date();
        return dateFormat.format(date);
    }

    long parseDate(String text)
    {
        long time = 0;
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.CANADA);
        try {
            time = dateFormat.parse(text).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return time;
    }

    int getDay(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.DAY_OF_MONTH);
    }

    int getMonth(Date date) {

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.MONTH) + 1;
    }

    int getYear(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.YEAR);
    }

    Date timeToDate(String timestamp) {
        DateFormat format =  new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.CANADA);
        Date date = new Date();
        try {
            date = format.parse(timestamp);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return date;
    }

    int parseMonthtoInt(String month) {
        switch (month) {
            case ("January"):
                return 1;
            case ("February"):
                return 2;
            case ("March"):
                return 3;
            case ("April"):
                return 4;
            case ("May"):
                return 5;
            case ("June"):
                return 6;
            case ("July"):
                return 7;
            case ("August"):
                return 8;
            case ("September"):
                return 9;
            case ("October"):
                return 10;
            case ("November"):
                return 11;
            case ("December"):
                return 12;
            case ("All"):
                return -1;
            default:
                return -1;
        }
    }

    public void setUser(String username) {
        SharedPreferences sharedpreferences;
        sharedpreferences = this.context.getSharedPreferences("USER", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString("username", username);
        editor.apply();
    }

    void setSelectedPosition(int posID) {
        SharedPreferences sharedpreferences;
        sharedpreferences = this.context.getSharedPreferences("USER", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putInt("selectedPosition", posID);
        editor.apply();
    }

    void setDefaultUser(String defaultUsername) {
        SharedPreferences sharedpreferences;
        sharedpreferences = this.context.getSharedPreferences("USER", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString("defaultUsername", defaultUsername);
        editor.apply();
    }

    public void setAutoPost(boolean checked) {
        SharedPreferences sharedpreferences;
        sharedpreferences = this.context.getSharedPreferences("MAIN", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putBoolean("autoPost", checked);
        editor.apply();
    }

    public int[] getMaterialColor() {
        int my_colors[] = {ContextCompat.getColor(context, R.color.red),
                ContextCompat.getColor(context, R.color.pink),
                ContextCompat.getColor(context, R.color.purple),
                ContextCompat.getColor(context, R.color.deep_purple),
                ContextCompat.getColor(context, R.color.indigo),
                ContextCompat.getColor(context, R.color.green),
                ContextCompat.getColor(context, R.color.teal),
                ContextCompat.getColor(context, R.color.cyan),
                ContextCompat.getColor(context, R.color.light_blue),
                ContextCompat.getColor(context, R.color.blue),
                ContextCompat.getColor(context, R.color.light_green),
                ContextCompat.getColor(context, R.color.lime),
                ContextCompat.getColor(context, R.color.yellow),
                ContextCompat.getColor(context, R.color.amber),
                ContextCompat.getColor(context, R.color.orange),
                ContextCompat.getColor(context, R.color.black),
                ContextCompat.getColor(context, R.color.blue_grey),
                ContextCompat.getColor(context, R.color.grey),
                ContextCompat.getColor(context, R.color.brown),
                ContextCompat.getColor(context, R.color.deep_orange),
                };

        return my_colors;
    }

    public int[] getFlatColor() {
        int my_colors[] = {ContextCompat.getColor(context, R.color.turquoise),
                ContextCompat.getColor(context, R.color.emerald),
                ContextCompat.getColor(context, R.color.peter_river),
                ContextCompat.getColor(context, R.color.amethyst),
                ContextCompat.getColor(context, R.color.wet_asphalt),
                ContextCompat.getColor(context, R.color.midnight_blue),
                ContextCompat.getColor(context, R.color.wisteria),
                ContextCompat.getColor(context, R.color.belize_hole),
                ContextCompat.getColor(context, R.color.nephritis),
                ContextCompat.getColor(context, R.color.green_sea),
                ContextCompat.getColor(context, R.color.sun_flower),
                ContextCompat.getColor(context, R.color.carrot),
                ContextCompat.getColor(context, R.color.alizarin),
                ContextCompat.getColor(context, R.color.clouds),
                ContextCompat.getColor(context, R.color.concrete),
                ContextCompat.getColor(context, R.color.asbestos),
                ContextCompat.getColor(context, R.color.silver),
                ContextCompat.getColor(context, R.color.pomegranate),
                ContextCompat.getColor(context, R.color.pumpkin),
                ContextCompat.getColor(context, R.color.orange2)};
        return my_colors;
    }

    public Bitmap resizeProfileSize(Drawable image) {
        Bitmap b = ((BitmapDrawable)image).getBitmap();
        Bitmap bitmapResized = Bitmap.createScaledBitmap(b, 120, 120, false);
        return bitmapResized;
    }

    public void storeImage(Bitmap image, User user) {
        File pictureFile = getOutputMediaFile(user);
        if (pictureFile == null) {
            return;
        }
        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            image.compress(Bitmap.CompressFormat.PNG, 90, fos);
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /** Create a File for saving an image or video */
    private  File getOutputMediaFile(User user){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory()
                + "/Android/data/"
                + context.getApplicationContext().getPackageName()
                + "/ProfileImage");

        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                return null;
            }
        }
        // Create a media file name
        File mediaFile;
        String mImageName="PI_"+ user.getUsername() +".jpg";
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + mImageName);
        return mediaFile;
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {

        private ImageListener listener;
        private User user;
        public DownloadImageTask(ImageListener listener, User user) {
            this.listener = listener;
            this.user = user;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            //Log.i("Testing", result.getByteCount()+"");
            if (listener != null) {
                listener.imageCompleted(Bitmap.createScaledBitmap(result, 120, 120, false), user);
            }
            //userBitmap = Bitmap.createScaledBitmap(result, 120, 120, false);
        }
    }

    public void runDownloadImageTask(User user) {
        if (user.getProfileImage() == -1) {
            new DownloadImageTask(this, user).execute(user.getProfileImageUrl());
        } else {
            storeImage(new Helper(context).resizeProfileSize(ContextCompat.getDrawable(context, R.drawable.ic_label_outline_white_big)), user);
        }
    }

    @Override
    public void imageCompleted(Bitmap b, User user) {
        storeImage(b, user);
    }

    public Bitmap loadImageFromStorage(User user) {

        File mediaStorageDir = new File(Environment.getExternalStorageDirectory()
                + "/Android/data/"
                + context.getApplicationContext().getPackageName()
                + "/ProfileImage");

        File mediaFile;
        String mImageName="PI_"+ user.getUsername() +".jpg";
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + mImageName);
        Bitmap b = null;
        try {
            b = BitmapFactory.decodeStream(new FileInputStream(mediaFile));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return b;

    }

    public void deleteTrans(long id, String username) {
        TransactionDBHelper transDB = new TransactionDBHelper(context.getApplicationContext());
        SharedPreferences sharedpreferences = context.getSharedPreferences("USER", Context.MODE_PRIVATE);
        User user = new User(context,
                sharedpreferences.getString("defaultUsername", context.getResources().getString(R.string.default_user)),
                context.getResources().getString(R.string.default_user));

        Helper helper = new Helper(context);
        List<Tag> tagList = helper.parseTag(transDB.getTransByID(id, username).getMessage(), username);
        transDB.deleteTransactions(id);


        // update tag cloud
        TagDBHelper tagDB = new TagDBHelper(context.getApplicationContext());
        for (Tag t : tagList) {
            t.setUser(user);
            tagDB.updateTag(t);
        }

        tagDB.close();
    }

    public void displayTransList(View v, Activity activity) {

        TransactionDBHelper transDB = new TransactionDBHelper(context);
        SharedPreferences sharedpreferences = context.getSharedPreferences("USER", Context.MODE_PRIVATE);
        String username = sharedpreferences.getString("username", null);
        ArrayList<Transaction> transList = new ArrayList<>(transDB.getAllData(username));
        Collections.sort(transList);
        Collections.reverse(transList);
        if (transList.size() > 0) {
            SampleExpandableListAdapter listAdapter;
            ExpandableListView expListView = (ExpandableListView) v.findViewById(R.id.transactionList);
            listAdapter = new SampleExpandableListAdapter(context, activity, transList, username);

            expListView.setAdapter(listAdapter);
            expListView.setGroupIndicator(new ColorDrawable(ContextCompat.getColor(context, R.color.tw__transparent)));
        }

        transDB.close();
    }

    public void setTwitterConnected(boolean checked) {
        SharedPreferences sharedpreferences;
        sharedpreferences = this.context.getSharedPreferences("MAIN", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putBoolean("twitterConnected", checked);
        editor.apply();
    }


}
