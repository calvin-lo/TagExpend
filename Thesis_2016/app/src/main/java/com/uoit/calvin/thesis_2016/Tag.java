package com.uoit.calvin.thesis_2016;

class Tag {

    private String title;
    private String type;
    private float amount;
    private User user;

    Tag(String name, String type, float amount) {
        setTitle(name);
        setType(type);
        setAmount(amount);
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String name) {
        this.title = name.substring(0,name.length());
        this.title = this.title.replace(" ", "");
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

    public void setUser(User user) {
        this.user = user;
    }

    public User getUser(){return this.user;}

    @Override
    public String toString() {
        return this.getType() + this.getTitle();
    }


}
