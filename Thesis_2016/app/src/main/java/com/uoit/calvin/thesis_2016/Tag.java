package com.uoit.calvin.thesis_2016;

import android.content.Context;
import android.content.res.Resources;

class Tag {

    private String name;
    private String type;
    private float amount;
    private static final String STAR_SIGN = "&#9734;";
    private static final String LOCATION_SIGN = "&#10164;";
    private static final String CURRENCY_SIGN = "&#164;";
    private static final String STAR_ICON = "\u2606";
    private static final String AT_ICON = "\u27B4";
    private static final String DOLLAR_ICON = "\u00A4";

    Tag(String name, String type, float amount) {
        setName(name);
        setType(type);
        setAmount(amount);
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name.substring(0,name.length());
        this.name = this.name.replace(" ", "");
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType(){
        return this.type;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public float getAmount() {
        return this.amount;
    }

    @Override
    public String toString() {
        String s;
        switch (this.getType()) {
            case STAR_SIGN:
                s = STAR_ICON + this.getName();
                break;
            case LOCATION_SIGN:
                s = AT_ICON + this.getName();
                break;
            case CURRENCY_SIGN:
                s = DOLLAR_ICON + this.getName();
                break;
            default:
                s = this.getType() + this.getName();
        }
        return s;
    }
}
