package com.sellspot.app.Activities.Following;

import com.sellspot.app.Handlers.SSFollowingsHandler;
import com.sellspot.app.Networking.SSFirestoreManager;

public class SSFollowingVM {
    SSFollowingsHandler getFollowingsHandler;

    public void getFollowings(String userid) {
        SSFirestoreManager.firestoreHelper.getUserFollowings(userid, getFollowingsHandler);
    }

}
