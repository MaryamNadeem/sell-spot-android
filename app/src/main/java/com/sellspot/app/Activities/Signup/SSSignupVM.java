package com.sellspot.app.Activities.Signup;

import com.sellspot.app.Handlers.SSFirebaseHandler;
import com.sellspot.app.Networking.SSFirebaseAuthManager;
import com.sellspot.app.Networking.SSFirestoreManager;

public class SSSignupVM {
    SSFirebaseHandler usernameHandler;
    SSFirebaseHandler emailHandler;

    public void checkUsername(String username)
    {
        SSFirestoreManager.firestoreHelper.checkUserNameExistence(username,usernameHandler);
    }

    public void checkEmail(String email)
    {
        SSFirebaseAuthManager.authHelper.checkIfEmailExists(email,emailHandler);
    }

}
