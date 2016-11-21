package com.uoit.calvin.thesis_2016;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Transaction {

    private static DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.CANADA);
    private List<Tag> tagsList;
    private Date date;
    private long id;

    Transaction(List<Tag> tagsList) {
        setDate();
        setTags(tagsList);
    }

    public void setId(long id) {
        this.id = id;
    }
    public long getId() {
        return id;
    }

    private void setTags(List<Tag> tagsList) {
        this.tagsList = tagsList;
    }

    private void setDate() {
        date = new Date();
    }

    private String getDate() {
        return dateFormat.format(date);
    }

    @Override
    public String toString() {
        String s = getDate() + " ";
        for (Tag tag : tagsList) {
            s = s + tag.toString() + " ";
        }
        if (tagsList.size() >= 1) {
            s = s + "$"  + tagsList.get(0).getAmount();
        }
        return s;
    }
}
