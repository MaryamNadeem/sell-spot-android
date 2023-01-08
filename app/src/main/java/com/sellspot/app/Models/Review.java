package com.sellspot.app.Models;
import java.io.Serializable;


public class Review implements Serializable {
    private String review;
    private String userid;
    private String reviewerid;
    private String time;
    private String reviewername;

    public Review() {
    }

    public Review(String review, String userid, String reviewerid, String time, String reviewername) {
        this.review = review;
        this.userid = userid;
        this.reviewerid = reviewerid;
        this.time = time;
        this.reviewername = reviewername;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getReviewerid() {
        return reviewerid;
    }

    public void setReviewerid(String reviewerid) {
        this.reviewerid = reviewerid;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getReviewername() {
        return reviewername;
    }

    public void setReviewername(String reviewername) {
        this.reviewername = reviewername;
    }
}
