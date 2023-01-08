package com.sellspot.app.Handlers;

import com.sellspot.app.Models.User;

import java.util.List;

public interface SSUsersHandler {
    void completionHandler(Boolean success, List<User> users, String error);
}
