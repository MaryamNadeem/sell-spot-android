package com.sellspot.app.Activities.UserProfile;

import com.sellspot.app.Handlers.SSFirebaseHandler;
import com.sellspot.app.Handlers.SSFollowersHandler;
import com.sellspot.app.Handlers.SSFollowingsHandler;
import com.sellspot.app.Handlers.SSPostsHandler;
import com.sellspot.app.Handlers.SSUserHandler;
import com.sellspot.app.Networking.SSFirestoreManager;


public class SSUserProfileVM {
    SSUserHandler getUserHandler;
    SSPostsHandler getPostsHandler;
    SSFirebaseHandler statusHandler;
    SSFirebaseHandler followHandler;
    SSFirebaseHandler unfollowHandler;
    SSFollowingsHandler getFollowingsHandler;
    SSFollowersHandler getFollowerHandler;

    public void getUserImage(String userid) {
        SSFirestoreManager.firestoreHelper.getUserProfile(userid, getUserHandler);
    }

    public void setUserProfilePosts(String userid) {
        SSFirestoreManager.firestoreHelper.getUserProducts(userid, getPostsHandler);
    }

    public void getFollowStatus(String userid) {
        SSFirestoreManager.firestoreHelper.getFollowStatus(userid, statusHandler);
    }

    public void followUser(String userid, String username) {
        SSFirestoreManager.firestoreHelper.followUser(userid, username, followHandler);
    }

    public void unFollowUser(String userid) {
        SSFirestoreManager.firestoreHelper.unfollowUser(userid, unfollowHandler);
    }

    public void getFollowings(String userid) {
        SSFirestoreManager.firestoreHelper.getUserFollowings(userid, getFollowingsHandler);
    }

    public void getFollowers(String userid) {
        SSFirestoreManager.firestoreHelper.getUserFollowers(userid, getFollowerHandler);
    }
}
