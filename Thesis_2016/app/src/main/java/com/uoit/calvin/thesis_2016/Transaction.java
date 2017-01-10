package com.uoit.calvin.thesis_2016;

import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Transaction implements Comparable<Transaction>{

    private static DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.CANADA);
    private List<Tag> tagList;
    private long id;
    private String timestamp;
    private String tagStr;
    private float amount;

    private MyDate myDate;
    private Date date;

    private static final String STAR_ICON = "\u2606";
    private static final String LOCATION_ICON = "\u27B4";
    private static final String DOLLAR_ICON = "\u00A4";


    Transaction() {
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void setAmout(float amount) {
        this.amount = amount;
    }

    public float getAmount() {
        return this.amount;
    }

    public void setTags(List<Tag> tagList) {
        tagStr = new Helper().convertTagListToString(tagList);
        Log.i("MYTAGSTR", tagStr);
        this.tagList = tagList;
    }

    public List<Tag> getStarTagsList() {
        List<Tag> result = new ArrayList<>();

        for (Tag t : this.tagList) {
            Log.i("MYTEST", t.getType());
            Log.i("MYTEST", STAR_ICON);
            if (t.getType().equals(STAR_ICON)) {
                result.add(t);
            }
        }
        return result;
    }

    public List<Tag> getLocationTagsList() {
        List<Tag> result = new ArrayList<>();

        for (Tag t : this.tagList) {
            if (t.getType().equals(LOCATION_ICON)) {
                result.add(t);
            }
        }
        return result;
    }

    public List<Tag> getTagsList() {
        return this.tagList;
    }

    public String getTagsStr() {
        return this.tagStr;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
        myDate = new Helper().timeToMyDate(timestamp);
        date = new Helper().timeToDate(timestamp);
    }

    public String getTimestamp() {
        return timestamp;
    }

    public MyDate getMyDate() {
        return this.myDate;
    }

    public void setMyDate(MyDate myDate) {
        this.myDate = myDate;
    }

    public Date getDate() {
        return this.date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public String toString() {
        String s = getTimestamp() + " ";
        for (Tag tag : tagList) {
            s = s + tag.toString() + " ";
        }
        s = s + "$"  + amount;
        return s;
    }

    @Override
    public int compareTo(Transaction t) {
        return getDate().compareTo(t.getDate());
    }
}
