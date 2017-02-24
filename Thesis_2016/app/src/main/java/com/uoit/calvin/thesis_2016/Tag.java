package com.uoit.calvin.thesis_2016;

class Tag {

    private String name;
    private String type;
    private float amount;
    private String user;

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

    public void setUser(String user) {
        this.user = user;
    }

    public String getUser() {
        return this.user;
    }

    @Override
    public String toString() {
        return this.getType() + this.getName();
    }
}
