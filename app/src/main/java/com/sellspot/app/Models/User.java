package com.sellspot.app.Models;
import java.io.Serializable;


public class User implements Serializable {
    private String address;
    private String email;
    private String firstname;
    private String image;
    private String lastname;
    private String password;
    private String phonenumber;
    private String time;
    private String userid;
    private String username;

    public User() {}

    public User(String address, String email, String firstname, String image, String lastname, String password, String phonenumber, String time, String userid, String username) {
        this.address = address;
        this.email = email;
        this.firstname = firstname;
        this.image = image;
        this.lastname = lastname;
        this.password = password;
        this.phonenumber = phonenumber;
        this.time = time;
        this.userid = userid;
        this.username = username;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
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
}

