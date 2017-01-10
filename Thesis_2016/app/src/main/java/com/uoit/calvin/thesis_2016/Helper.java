package com.uoit.calvin.thesis_2016;

import android.text.Html;
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
    private static final String STAR_SIGN = "&#9734;";
    private static final String LOCATION_SIGN = "&#10164;";
    private static final String CURRENCY_SIGN = "&#164;";
    private static final String STAR_ICON = "☆";
    private static final String AT_ICON = "➴";
    private static final String DOLLAR_ICON = "¤";


    public boolean isStar(String code) {
        return code.equals(STAR_SIGN) || code.equals(STAR_ICON);
    }

    public boolean isAt(String code) {
        return code.equals(LOCATION_SIGN) || code.equals(AT_ICON);
    }

    public boolean isDollar(String code) {
        return code.equals(CURRENCY_SIGN) || code.equals(DOLLAR_ICON);
    }


    List<Tag> parseTag(String data) {
        List<Tag> tags = new ArrayList<>();
        String parsedTags[] =  data.split("(?=&#9734;|&#10164;|&#164;)");
        float amount = 0;

        for (int i = 1; i < parsedTags.length; i++) {
            if (parsedTags[i].substring(0,6).equals(CURRENCY_SIGN)) {
                amount = amount + Float.parseFloat(parsedTags[i].substring(6,parsedTags[i].length()));
            }
        }

        for (int i = 1; i < parsedTags.length; i++) {
            if (parsedTags[i].substring(0,7).equals(STAR_SIGN)) {
                tags.add(new Tag(parsedTags[i].substring(7,parsedTags[i].length()).replace("\n",""), STAR_ICON, amount));
            }
            if (parsedTags[i].substring(0,8).equals(LOCATION_SIGN)) {
                tags.add(new Tag(parsedTags[i].substring(8,parsedTags[i].length()).replace("\n",""), AT_ICON, amount));
            }
        }

        return tags;
    }

    List<Tag> parseTagUnicode(String data) {
        List<Tag> tags = new ArrayList<>();
        String parsedTags[] =  data.split("(?=☆|➴|¤)");
        float amount = 0;


        for (int i = 1; i < parsedTags.length; i++) {
            if (parsedTags[i].substring(0,1).equals(DOLLAR_ICON)) {
                amount = amount + Float.parseFloat(parsedTags[i].substring(1,parsedTags[i].length()));
            }
        }

        for (int i = 1; i < parsedTags.length; i++) {
            if (parsedTags[i].substring(0,1).equals(STAR_ICON)){
                tags.add(new Tag(parsedTags[i].substring(1,parsedTags[i].length()), STAR_ICON, amount));
            }
            if (parsedTags[i].substring(0,1).equals(AT_ICON)) {
                tags.add(new Tag(parsedTags[i].substring(1,parsedTags[i].length()), AT_ICON, amount));
            }
        }
        return tags;
    }

    public float getAmount(String trans) {
        String parsedTags[] =  trans.split("(?=&#9734;|&#10164;|&#164;)");
        float amount = 0;

        for (int i = 1; i < parsedTags.length; i++) {
            if (parsedTags[i].substring(0,6).equals(CURRENCY_SIGN)) {
                amount = amount + Float.parseFloat(parsedTags[i].substring(6,parsedTags[i].length()));
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

    String symbolToDatabaseCode(String data) {
        String s;
        switch (data.substring(0,1)) {
            case STAR_ICON:
                s = STAR_SIGN + data.substring(1,data.length());
                break;
            case AT_ICON:
                s = LOCATION_SIGN + data.substring(1,data.length());
                break;
            case DOLLAR_ICON:
                s = CURRENCY_SIGN + data.substring(1,data.length());
                break;
            default:
                s = data;
        }
        return s;
    }

    String databaseCodeToSymbol (String data) {
        String s;
        switch (data.substring(0,1)) {
            case STAR_ICON:
                s = STAR_SIGN + data.substring(1,data.length());
                break;
            case AT_ICON:
                s = LOCATION_SIGN + data.substring(1,data.length());
                break;
            case DOLLAR_ICON:
                s = CURRENCY_SIGN + data.substring(1,data.length());
                break;
            default:
                s = data;
        }
        return s;
    }


    public String HTMLReplaceNewLine(String input) {
        String result = input.replace("<p dir=\"ltr\">", "");
        result = result.replace("</p>","");
        result = result.replace("<u>","");
        result = result.replace("</u>","");
        return result;
    }

    public String HTMLParsing(String input) {
        String result = input;


        // replace hashtag
        result = result.replace("&#11618;", "#");

        // replace at
        result = result.replace("<img src=\"at.png\">", "@");

        // replace dollar
        result = result.replace("<img src=\"dollar.png\">", "$");

        return  result;
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

    public MyDate timeToMyDate(String timestamp) {
        DateFormat format =  new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.CANADA);
        Date date = new Date();
        try {
            date = format.parse(timestamp);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        MyDate myDate = new MyDate();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        myDate.setYear(cal.get(Calendar.YEAR));
        myDate.setMonth(cal.get(Calendar.MONTH)+1);
        myDate.setDay(cal.get(Calendar.DAY_OF_MONTH));

        return myDate;
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




    public String convertTagListToString(List<Tag> tagList) {
        String tagStr = "";
        for (int i = 0; i < tagList.size(); i++) {
            if ( (i+1) != tagList.size()) {
                tagStr = tagStr + tagList.get(i).toString();
            } else {
                tagStr = tagStr + tagList.get(i);
            }
        }
        return  tagStr;
    }

    public String getEmijoByUnicode(int unicode){
        return new String(Character.toChars(unicode));
    }
}
