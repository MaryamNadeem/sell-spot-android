package com.sellspot.app.Activities.Followers;

import com.sellspot.app.Handlers.SSFollowersHandler;
import com.sellspot.app.Networking.SSFirestoreManager;


public class SSFollowersVM {
    SSFollowersHandler getFollowerHandler;

    public void getFollowers(String userid) {
        SSFirestoreManager.firestoreHelper.getUserFollowers(userid, getFollowerHandler);
    }
}

