package com.sellspot.app.Models;
import java.io.Serializable;
import java.util.List;

public class Product implements Serializable {
    private String description;
    private List<String> images;
    private String material;
    private int price;
    private String productid;
    private String size;
    private String status;
    private List<String> tags;
    private String time;
    private String userid;
    private String username;
    private String shareablelink;

    public Product() {}

    public Product(String description, List<String> images, String material, int price, String productid, String size, String status, List<String> tags, String time, String userid, String username, String shareablelink) {
        this.description = description;
        this.images = images;
        this.material = material;
        this.price = price;
        this.productid = productid;
        this.size = size;
        this.status = status;
        this.tags = tags;
        this.time = time;
        this.userid = userid;
        this.username = username;
        this.shareablelink = shareablelink;
    }

    public Product(Product p) {
        this(p.description, p.images,p.material,p.price,p.productid,p.size,p.status,p.tags,p.time,p.userid,p.username,p.shareablelink);
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getProductid() {
        return productid;
    }

    public void setProductid(String productid) {
        this.productid = productid;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getShareablelink() {
        return shareablelink;
    }

    public void setShareablelink(String shareablelink) {
        this.shareablelink = shareablelink;
    }
}