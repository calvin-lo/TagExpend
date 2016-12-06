package com.uoit.calvin.thesis_2016;

import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Transaction {

    private static DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.CANADA);
    private List<Tag> tagList;
    private long id;
    private String timestamp;
    private String tagStr;
    private float amount;



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

    public List<Tag> getTagsList() {
        return this.tagList;
    }

    public String getTagsStr() {
        return this.tagStr;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getTimestamp() {
        return timestamp;
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
}
