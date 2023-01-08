package com.sellspot.app.Handlers;
import com.sellspot.app.Models.User;

public interface SSUserHandler {
    void completionHandler(Boolean success, User user, String error);
}
