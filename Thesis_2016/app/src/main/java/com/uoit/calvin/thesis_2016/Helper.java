package com.uoit.calvin.thesis_2016;

import android.text.Html;

import java.util.ArrayList;
import java.util.List;

class Helper {

    List<Tag> parseTag(String data) {
        List<Tag> tags = new ArrayList<>();
        String parsedTags[] = data.split("(?=#|\\@|\\$)");
        float amount = 0;

        for (int i = 1; i < parsedTags.length; i++) {
            if (parsedTags[i].charAt(0) == '$') {
                amount = amount + Float.parseFloat(parsedTags[i].substring(1,parsedTags[i].length()));
            }
        }

        for (int i = 1; i < parsedTags.length; i++) {
            if (parsedTags[i].charAt(0) != '$') {
                tags.add(new Tag(parsedTags[i].substring(1,parsedTags[i].length()), String.valueOf(parsedTags[i].charAt(0)), amount));
            }
        }
        return tags;
    }

    List<String> tagListToString(List<Tag> tagsList) {
        List<String> tagStrList = new ArrayList<>();
        for (Tag t : tagsList) {
            tagStrList.add(t.toString());
        }
        return tagStrList;
    }

    public String HTMLReplaceNewLine(String input) {
        String result = input.replace("<p dir=\"ltr\">", "");
        result = result.replace("</p>","");
        return result;
    }

    public String HTMLParsing(String input) {
        String result = input;

        // replace hashtag
        result = result.replace("<img src=\"hashtag.png\">", "#");

        // replace at
        result = result.replace("<img src=\"at.png\">", "@");

        // replace dollar
        result = result.replace("<img src=\"dollar.png\">", "$");

        return  result;
    }

}
