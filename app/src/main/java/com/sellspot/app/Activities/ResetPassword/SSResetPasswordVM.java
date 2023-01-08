package com.sellspot.app.Activities.ResetPassword;

import com.sellspot.app.Handlers.SSFirebaseHandler;
import com.sellspot.app.Networking.SSFirebaseAuthManager;

public class SSResetPasswordVM {

    public SSFirebaseHandler resetPasswordHandler;

    public void resetPassword(final String email) {
        SSFirebaseAuthManager.authHelper.sendPasswordResetEmail(email, resetPasswordHandler);
    }
}
