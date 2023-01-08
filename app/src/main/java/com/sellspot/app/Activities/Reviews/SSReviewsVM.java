package com.sellspot.app.Activities.Reviews;
import com.sellspot.app.Handlers.SSReviewsHandler;
import com.sellspot.app.Networking.SSFirestoreManager;

public class SSReviewsVM {
    SSReviewsHandler getReviewsHandler;

    public void getReviews(String userid) {
        SSFirestoreManager.firestoreHelper.getUserReviews(userid, getReviewsHandler);
    }
}
