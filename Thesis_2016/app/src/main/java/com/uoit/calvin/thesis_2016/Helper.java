package com.uoit.calvin.thesis_2016;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

class Helper {

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
        String parsedTags[] =  message.split(format);
        float amount = 0;

        for (int i = 1; i < parsedTags.length; i++) {
            if (parsedTags[i].substring(0,1).equals(DOLLAR_ICON)) {
                amount = amount + Float.parseFloat(parsedTags[i].substring(1, parsedTags[i].length()));
            }
        }

        for (int i = 1; i < parsedTags.length; i++) {
            if (parsedTags[i].substring(0,1).equals(GENERAL_ICON)) {
                tags.add(new Tag(parsedTags[i].substring(1,parsedTags[i].length()), GENERAL_ICON, amount));
            }
            if (parsedTags[i].substring(0,1).equals(LOCATION_ICON)) {
                tags.add(new Tag(parsedTags[i].substring(1,parsedTags[i].length()), LOCATION_ICON, amount));
            }
            if (parsedTags[i].substring(0,1).equals(CATEGORY_ICON)) {
                tags.add(new Tag(parsedTags[i].substring(1, parsedTags[i].length()), CATEGORY_ICON, amount));
            }

        }

        return tags;
    }

    List<Tag> parseTag(String message, String user) {
        List<Tag> tags = new ArrayList<>();
        String parsedTags[] =  message.split(format);
        float amount = 0;

        for (int i = 1; i < parsedTags.length; i++) {
            if (parsedTags[i].substring(0,1).equals(DOLLAR_ICON)) {
                amount = amount + Float.parseFloat(parsedTags[i].substring(1, parsedTags[i].length()));
            }
        }

        for (int i = 1; i < parsedTags.length; i++) {
            if (parsedTags[i].substring(0,1).equals(GENERAL_ICON)) {
                Tag t = new Tag(parsedTags[i].substring(1,parsedTags[i].length()), GENERAL_ICON, amount);
                t.setUser(user);
                tags.add(t);
            }
            if (parsedTags[i].substring(0,1).equals(LOCATION_ICON)) {
                Tag t = new Tag(parsedTags[i].substring(1,parsedTags[i].length()), LOCATION_ICON, amount);
                t.setUser(user);
                tags.add(t);
            }
            if (parsedTags[i].substring(0,1).equals(CATEGORY_ICON)) {
                Tag t = new Tag(parsedTags[i].substring(1, parsedTags[i].length()), CATEGORY_ICON, amount);
                t.setUser(user);
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

    public String getCurrentTime() {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.CANADA);
        Date date = new Date();
        return dateFormat.format(date);
    }

    public long parseDate(String text)
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

    public int getDay(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.DAY_OF_MONTH);
    }

    public int getMonth(Date date) {

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.MONTH) + 1;
    }

    public int getYear(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.YEAR);
    }

    public Date timeToDate(String timestamp) {
        DateFormat format =  new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.CANADA);
        Date date = new Date();
        try {
            date = format.parse(timestamp);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return date;
    }

    public int parseMonthtoInt(String month) {
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

    public void setUser(String user) {
        SharedPreferences sharedpreferences;
        sharedpreferences = this.context.getSharedPreferences("USER", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString("user", user);
        editor.apply();
    }

    public void setSelectedPosition(int posID) {
        SharedPreferences sharedpreferences;
        sharedpreferences = this.context.getSharedPreferences("USER", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putInt("selectedPosition", posID);
        editor.apply();
    }

    public void setDefaultUser(String user) {
        SharedPreferences sharedpreferences;
        sharedpreferences = this.context.getSharedPreferences("USER", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString("defaultUser", user);
        editor.apply();
    }

    public int[] getColorArray() {
        int my_colors[] = {ContextCompat.getColor(context, R.color.red),
                ContextCompat.getColor(context, R.color.pink),
                ContextCompat.getColor(context, R.color.purple),
                ContextCompat.getColor(context, R.color.deep_purple),
                ContextCompat.getColor(context, R.color.indigo),
                ContextCompat.getColor(context, R.color.blue),
                ContextCompat.getColor(context, R.color.light_blue),
                ContextCompat.getColor(context, R.color.cyan),
                ContextCompat.getColor(context, R.color.teal),
                ContextCompat.getColor(context, R.color.green),
                ContextCompat.getColor(context, R.color.light_green),
                ContextCompat.getColor(context, R.color.lime),
                ContextCompat.getColor(context, R.color.yellow),
                ContextCompat.getColor(context, R.color.amber),
                ContextCompat.getColor(context, R.color.orange),
                ContextCompat.getColor(context, R.color.deep_orange),
                ContextCompat.getColor(context, R.color.brown),
                ContextCompat.getColor(context, R.color.grey),
                ContextCompat.getColor(context, R.color.blue_grey)};

        return my_colors;
    }
}
