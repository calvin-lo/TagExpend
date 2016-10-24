package com.uoit.calvin.thesis2;

public class Tag {

    private long id;
    private String tag;

    public void setId(long id) {

        this.id = id;
    }

    public long getId() {
        return id;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    @Override
    public String toString() {
        return tag;
    }



}
