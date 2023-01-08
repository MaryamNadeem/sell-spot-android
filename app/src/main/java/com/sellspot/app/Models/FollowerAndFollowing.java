package com.sellspot.app.Models;

import java.io.Serializable;

public class FollowerAndFollowing implements Serializable {
    private String userid;
    private String username;

    public FollowerAndFollowing() {}

    public FollowerAndFollowing(String userid, String username) {
        this.userid = userid;
        this.username = username;
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
