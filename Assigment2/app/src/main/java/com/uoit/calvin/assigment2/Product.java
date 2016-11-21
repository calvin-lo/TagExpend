package com.uoit.calvin.assigment2;

public class Product {

    private int productID;
    private String name;
    private String description;
    private float price;

    public Product() {}

    public Product(int productID, String name, String description, float price) {
        setProductID(productID);
        setDescription(description);
        setName(name);
        setPrice(price);
    }


    public int getProductID() {
        return productID;
    }

    public void setProductID(int productID) {
        this.productID = productID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }
}
