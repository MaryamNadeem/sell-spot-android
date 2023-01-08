package com.sellspot.app.Activities.Login;

import android.content.Context;

import com.sellspot.app.Handlers.SSFirebaseHandler;
import com.sellspot.app.Networking.SSFirebaseAuthManager;
import com.sellspot.app.Networking.SSFirebaseManager;
import com.sellspot.app.Networking.SSFirestoreManager;

public class SSLoginVM {
    public SSFirebaseHandler loginHandler;
    public SSFirebaseHandler uploadGuestCartHandler;

    public void loginUser(final String email, final String password, Context context) {
        SSFirebaseAuthManager.authHelper.signInUser(email, password, context, loginHandler);
    }
    public void updatePassword(final String password)
    {
        SSFirestoreManager.firestoreHelper.updateUserPassword(password);
    }
    public void uploadGuestUserCart()
    {
        System.out.println("coming in upload");
        SSFirestoreManager.firestoreHelper.uploadGuestUserCart(uploadGuestCartHandler);
    }
}
