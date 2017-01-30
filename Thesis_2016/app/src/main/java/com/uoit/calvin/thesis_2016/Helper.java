package com.uoit.calvin.thesis_2016;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

class Helper {

    private static final String STAR_ICON = "\u2606";
    private static final String LOCATION_ICON = "\u27B4";
    private static final String DOLLAR_ICON = "\u00A4";
    private static final String CATEGORY_ICON = "\u00A7";
    private static final String format = "(?=" + STAR_ICON + "|" + LOCATION_ICON + "|"+ CATEGORY_ICON + "|" + DOLLAR_ICON + ")";

    Helper() {}

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
            if (parsedTags[i].substring(0,1).equals(STAR_ICON)) {
                tags.add(new Tag(parsedTags[i].substring(1,parsedTags[i].length()), STAR_ICON, amount));
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

    String parseGeneral(String message) {
        String general = "";
        String parsedTags[] = message.split(format);
        for (int i = 1; i < parsedTags.length; i++) {
            if (parsedTags[i].substring(0,1).equals(STAR_ICON)) {
                general = general + STAR_ICON + parsedTags[i].substring(1, parsedTags[i].length());
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
}
