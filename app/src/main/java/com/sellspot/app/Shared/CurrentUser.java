package com.sellspot.app.Shared;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.sellspot.app.Models.User;

public class CurrentUser extends Application  {
    public static final CurrentUser shared = new CurrentUser();
    private User currentUser;
    private boolean isGuest = false;

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    public void setGuest(Context context, boolean guest) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("isGuest", guest);
        editor.commit();
    }

    public String getCurrentUserEmail(Context context) {
        final SharedPreferences mSharedPreference = PreferenceManager.getDefaultSharedPreferences(context);
        final String email = (mSharedPreference.getString("email",null));
        return email;
    }

    public void setCurrentUserEmail(Context context, String email) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("email", email);
        editor.commit();
    }

    public String getCurrentUserPassword(Context context) {
        final SharedPreferences mSharedPreference = PreferenceManager.getDefaultSharedPreferences(context);
        final String password = (mSharedPreference.getString("password",null));
        return password;
    }

    public boolean getIsGuest(Context context) {
        final SharedPreferences mSharedPreference = PreferenceManager.getDefaultSharedPreferences(context);
        Boolean isGuest = mSharedPreference.getBoolean("isGuest",false);
        return isGuest;
    }

    public void setCurrentUserPassword(Context context, String password) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("password", password);
        editor.commit();
    }

    public void removeCurrentUserEmail(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove("email");
        editor.commit();
    }

    public void removeCurrentUserPassword(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove("password");
        editor.commit();
    }

    public void saveCurrentUser(Context context, User u) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(u);
        editor.putString("CurrentUser", json);
        editor.commit();
    }

    public User getCUser(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        Gson gson = new Gson();
        String json = prefs.getString("CurrentUser", "");
        User obj = gson.fromJson(json, User.class);
        return obj;
    }
}
