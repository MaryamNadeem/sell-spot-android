package com.sellspot.app.Models;
import java.io.Serializable;


public class CartPerStore implements Serializable {
    private String userid;
    private String productid;
    private int price;
    private String size;
    private String image;

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getProductid() {
        return productid;
    }

    public void setProductid(String productid) {
        this.productid = productid;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public CartPerStore() {}

    public CartPerStore(String userid, String productid, int price, String size, String image) {
        this.userid = userid;
        this.productid = productid;
        this.price = price;
        this.size = size;
        this.image = image;
    }
}
