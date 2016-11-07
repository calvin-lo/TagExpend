package com.uoit.calvin.thesis2;

import java.util.ArrayList;
import java.util.List;

class Helper {

    List<Tag> parseTag(String data) {
        List<Tag> tags = new ArrayList<>();
        String parsedTags[] = data.split("(?=#|\\@|\\$)");
        for (int i = 1; i < parsedTags.length; i++) {
            tags.add(new Tag(parsedTags[i]));
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

}
