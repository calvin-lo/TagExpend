package com.uoit.calvin.thesis_2016;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ListAdapter;
import android.widget.Toast;

import com.twitter.sdk.android.core.models.Tweet;

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
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;

class Helper implements ImageListener{

    private String GENERAL_ICON;
    private String LOCATION_ICON;
    private String DOLLAR_ICON;
    private String CATEGORY_ICON;
    private String format;
    private Context context;

    Helper(Context context) {
        this.context = context;
        GENERAL_ICON = context.getResources().getString(R.string.icon_general);
        LOCATION_ICON = context.getResources().getString(R.string.icon_location);
        DOLLAR_ICON = context.getResources().getString(R.string.icon_dollar);
        CATEGORY_ICON = context.getResources().getString(R.string.icon_category);
        format = "(?=" + GENERAL_ICON + "|" + LOCATION_ICON + "|"+ "\\" + DOLLAR_ICON + "|" + CATEGORY_ICON + ")";

    }

    List<Tag> parseTag(String message) {
        List<Tag> tags = new ArrayList<>();
        message = message.replace(" ", "");
        String parsedTags[] =  message.split(format);
        float amount = 0;

        for (String s : parsedTags) {
            if (s.trim().length() > 0) {
                if (s.substring(0, 1).equals(DOLLAR_ICON)) {
                    amount = amount + Float.parseFloat(s.substring(1, s.length()));
                }
            }
        }
        for (String s : parsedTags) {
            if (s.trim().length() > 0) {
                if (s.substring(0, 1).equals(GENERAL_ICON) && s.length() > 1) {
                    tags.add(new Tag(s.substring(1,s.length()), GENERAL_ICON, amount));
                }
                if (s.substring(0, 1).equals(LOCATION_ICON) && s.length() > 1) {
                    tags.add(new Tag(s.substring(1, s.length()), LOCATION_ICON, amount));
                }
                if (s.substring(0, 1).equals(CATEGORY_ICON) && s.length() > 1) {
                    tags.add(new Tag(s.substring(1, s.length()), CATEGORY_ICON, amount));
                }
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
        DateFormat dateFormat = new SimpleDateFormat(context.getString(R.string.pattern_date_app), Locale.CANADA);
        Date date = new Date();
        return dateFormat.format(date);
    }

    long parseDate(String text) {
        long time = 0;
        DateFormat dateFormat = new SimpleDateFormat(context.getString(R.string.pattern_date_app), Locale.CANADA);
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
        DateFormat format =  new SimpleDateFormat(context.getString(R.string.pattern_date_app), Locale.CANADA);
        Date date = new Date();
        try {
            date = format.parse(timestamp);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return date;
    }

    int parseMonthToInt(String month) {
        month = month.toLowerCase();
        switch (month) {
            case ("january"):
                return 1;
            case ("february"):
                return 2;
            case ("march"):
                return 3;
            case ("april"):
                return 4;
            case ("may"):
                return 5;
            case ("june"):
                return 6;
            case ("july"):
                return 7;
            case ("august"):
                return 8;
            case ("september"):
                return 9;
            case ("october"):
                return 10;
            case ("november"):
                return 11;
            case ("december"):
                return 12;
            case ("jan"):
                return 1;
            case ("feb"):
                return 2;
            case ("mar"):
                return 3;
            case ("apr"):
                return 4;
            case ("jun"):
                return 6;
            case ("jul"):
                return 7;
            case ("aug"):
                return 8;
            case ("sept"):
                return 9;
            case ("sep"):
                return 9;
            case ("oct"):
                return 10;
            case ("nov"):
                return 11;
            case ("dec"):
                return 12;
            case ("All"):
                return -1;
            default:
                return -1;
        }
    }

    String parseMonthToString(int month) {
        switch (month) {
            case 1:
                return "Jan";
            case 2:
                return "Feb";
            case 3:
                return "Mar";
            case 4:
                return "Apr";
            case 5:
                return "May";
            case 6:
                return "Jun";
            case 7:
                return "Jul";
            case 8:
                return "Aug";
            case 9:
                return "Sep";
            case 10:
                return "Oct";
            case 11:
                return "Nov";
            case 12:
                return "Dec";
            default:
                return "Jan";


        }
    }

    /** Set SharedPreferences **/
    public void setUser(String username) {
        SharedPreferences sharedpreferences;
        sharedpreferences = this.context.getSharedPreferences(context.getString(R.string.shared_pref_name_user), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(context.getString(R.string.shared_pref_arg_username), username);
        editor.apply();
    }

    void setSelectedPosition(int posID) {
        SharedPreferences sharedpreferences;
        sharedpreferences = this.context.getSharedPreferences(context.getString(R.string.shared_pref_name_user), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putInt(context.getString(R.string.shared_pref_arg_selected_pos), posID);
        editor.apply();
    }

    void setDefaultDisplayName(String defaultUsername) {
        SharedPreferences sharedpreferences;
        sharedpreferences = this.context.getSharedPreferences(context.getString(R.string.shared_pref_name_user), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(context.getString(R.string.shared_pref_arg_default_display_name), defaultUsername);
        editor.apply();
    }

    void setAutoPost(boolean checked) {
        SharedPreferences sharedpreferences;
        sharedpreferences = this.context.getSharedPreferences(context.getString(R.string.shared_pref_name_main), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putBoolean(context.getString(R.string.shared_pref_arg_auto_post), checked);
        editor.apply();
    }

    void setPerfencesYear(int year) {
        SharedPreferences sharedpreferences;
        sharedpreferences = this.context.getSharedPreferences(context.getString(R.string.shared_pref_name_main), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putInt(context.getString(R.string.shared_pref_arg_year), year);
        editor.apply();
    }

    void setPerfencesMonth(int month) {
        SharedPreferences sharedpreferences;
        sharedpreferences = this.context.getSharedPreferences(context.getString(R.string.shared_pref_name_main), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putInt(context.getString(R.string.shared_pref_arg_month), month);
        editor.apply();
    }




    /** Color **/
    int[] getMaterialColor() {
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


    /** Image Process **/
    private Bitmap resizeProfileSize(Drawable image) {
        Bitmap b = ((BitmapDrawable)image).getBitmap();
        return Bitmap.createScaledBitmap(b, 120, 120, false);
    }

    public Drawable resizeDrawble(Drawable image, int x, int y) {
        Bitmap b = ((BitmapDrawable)image).getBitmap();
        Bitmap bitmapResized = Bitmap.createScaledBitmap(b, x, y, false);
        return new BitmapDrawable(context.getResources(), bitmapResized);
    }

    private void storeImage(Bitmap image, User user) {
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

    private  File getOutputMediaFile(User user){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory()
                + context.getString(R.string.helper_image_path_start)
                + context.getApplicationContext().getPackageName()
                + context.getString(R.string.helper_image_path_end));

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
        String mImageName = context.getString(R.string.helper_image_name_start)
                            + user.getUsername()
                            + context.getString(R.string.helper_image_name_end);
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + mImageName);
        return mediaFile;
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {

        private ImageListener listener;
        private User user;
        DownloadImageTask(ImageListener listener, User user) {
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

    void runDownloadImageTask(User user) {
        if (user.getProfileImage() == -1) {
            new DownloadImageTask(this, user).execute(user.getProfileImageUrl());
        } else {
            storeImage(new Helper(context).resizeProfileSize(ContextCompat.getDrawable(context, R.drawable.ic_account_box_white_24dp)), user);
        }
    }

    @Override
    public void imageCompleted(Bitmap b, User user) {
        storeImage(b, user);
    }

    Bitmap loadImageFromStorage(User user) {

        File mediaStorageDir = new File(Environment.getExternalStorageDirectory()
                + context.getString(R.string.helper_image_path_start)
                + context.getApplicationContext().getPackageName()
                + context.getString(R.string.helper_image_path_end));

        File mediaFile;
        String mImageName = context.getString(R.string.helper_image_name_start)
                            + user.getUsername()
                            + context.getString(R.string.helper_image_name_end);
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + mImageName);
        Bitmap b = null;
        try {
            b = BitmapFactory.decodeStream(new FileInputStream(mediaFile));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return b;

    }



    /** Transaction **/
    void deleteTrans(long id, String username) {
        TransactionDBHelper transDB = new TransactionDBHelper(context.getApplicationContext());
        SharedPreferences sharedpreferences = context.getSharedPreferences(context.getString(R.string.shared_pref_name_user), Context.MODE_PRIVATE);
        User user = new User(context,
                sharedpreferences.getString(context.getString(R.string.shared_pref_arg_default_display_name), context.getResources().getString(R.string.user_default)),
                context.getResources().getString(R.string.user_default));

        Helper helper = new Helper(context);
        List<Tag> tagList = helper.parseTag(transDB.getTransByID(id, username).getMessage());
        transDB.deleteTransactions(id);


        // update tag cloud
        TagDBHelper tagDB = new TagDBHelper(context.getApplicationContext());
        for (Tag t : tagList) {
            t.setUser(user);
            tagDB.updateTag(t);
        }

        transDB.close();
        tagDB.close();
    }

    void displayTransList(View v, Activity activity, int year, int month) {

        TransactionDBHelper transDB = new TransactionDBHelper(context);
        SharedPreferences sharedpreferences = context.getSharedPreferences(context.getString(R.string.shared_pref_name_user), Context.MODE_PRIVATE);
        String username = sharedpreferences.getString(context.getString(R.string.shared_pref_arg_username), null);
        ArrayList<Transaction> transList = new ArrayList<>(transDB.getTransByTime(year, month, username));
        Collections.sort(transList);
        Collections.reverse(transList);
        ExpandableListView lv_transactions = (ExpandableListView) v.findViewById(R.id.transactionList);
        if (transList.size() > 0) {
            MainExpandableListAdapter listAdapter;
            listAdapter = new MainExpandableListAdapter(context, activity, transList, username);

            lv_transactions.setAdapter(listAdapter);
            lv_transactions.setGroupIndicator(new ColorDrawable(ContextCompat.getColor(context, R.color.tw__transparent)));
        }

        transDB.close();
    }

    void setTwitterConnected(boolean checked) {
        SharedPreferences sharedpreferences;
        sharedpreferences = this.context.getSharedPreferences(context.getString(R.string.shared_pref_name_main), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putBoolean(context.getString(R.string.shared_pref_arg_twitter_connected), checked);
        editor.apply();
    }

    public void pullTweetsData(List<Tweet> tweets, boolean clear) {

        if (tweets != null) {
            if (tweets.size() > 0) {


                Collections.sort(tweets, new Comparator<Tweet>() {
                    @Override
                    public int compare(Tweet t1, Tweet t2) {
                        return t1.getId()<t2.getId()?-1:
                                t1.getId()>t2.getId()?1:0;
                    }
                });


                TransactionDBHelper transactionDBHelper = new TransactionDBHelper(context);
                TagDBHelper tagDBHelper = new TagDBHelper(context);
                UserDBHelper userDBHelper = new UserDBHelper(context);
                if (clear) {
                    transactionDBHelper.clearUser(tweets.get(0).user.screenName);
                    tagDBHelper.clearUser(tweets.get(0).user.screenName);
                }


                int color[] = this.getMaterialColor();
                int randomNum = ThreadLocalRandom.current().nextInt(0, color.length);
                int selectedColor = color[randomNum];
                for (Tweet t : tweets) {

                    String message = t.text.replace(context.getString(R.string.twitter_tail), "");

                    User user = new User(context, t.user.name, t.user.screenName);
                    user.setProfileImageUrl(t.user.profileImageUrl);
                    user.setProfileImage(-1);
                    user.setSinceID(t.getId());

                    userDBHelper.addUser(user);

                    Transaction trans = new Transaction(context);
                    trans.setMessage(message);
                    trans.setTags(this.parseTag(message));
                    trans.setGeneral(this.parseGeneral(message));
                    trans.setLocation(this.parseLocation(message));
                    trans.setCategory(this.parseCategory(message));
                    String pattern = context.getString(R.string.pattern_date_twitter);
                    SimpleDateFormat format = new SimpleDateFormat(pattern, Locale.CANADA);
                    String time = this.getCurrentTime();
                    trans.setColor(selectedColor);
                    try {
                        Date date = format.parse(t.createdAt);
                        DateFormat dateFormat = new SimpleDateFormat(context.getString(R.string.pattern_date_app), Locale.CANADA);
                        time = dateFormat.format(date);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    trans.setTimestamp(time);
                    trans.setUser(user);
                    trans.setAmount(this.getAmount(message));
                    transactionDBHelper.addTransactions(trans);

                    // add the tag to tag cloud
                    for (Tag tag : trans.getTagsList()) {
                        tag.setUser(user);
                        tag.setColor(ContextCompat.getColor(context, R.color.accent));
                        tagDBHelper.addTag(tag);
                    }

                    Toast.makeText(context, context.getString(R.string.follow_msg_pull_success), Toast.LENGTH_SHORT).show();
                }

                transactionDBHelper.close();
                tagDBHelper.close();
                userDBHelper.close();
            }
        }
    }

    public int lighter(int color, float factor) {
        int red = (int) ((Color.red(color) * (1 - factor) / 255 + factor) * 255);
        int green = (int) ((Color.green(color) * (1 - factor) / 255 + factor) * 255);
        int blue = (int) ((Color.blue(color) * (1 - factor) / 255 + factor) * 255);
        return Color.argb(Color.alpha(color), red, green, blue);
    }

    public void unfollow(String username) {
        UserDBHelper userDBHelper = new UserDBHelper(context);
        TransactionDBHelper transactionDBHelper = new TransactionDBHelper(context);
        TagDBHelper tagDBHelper = new TagDBHelper(context);
        if (!(username.equals(context.getString(R.string.user_default))) || !(username.equals(context.getString(R.string.icon_all)))) {
            userDBHelper.deleteTUser(username);
            transactionDBHelper.clearUser(username);
            tagDBHelper.clearUser(username);
        }
        Intent intent = new Intent(context,MainActivity.class);
        context.startActivity(intent);
        userDBHelper.close();
    }

}
