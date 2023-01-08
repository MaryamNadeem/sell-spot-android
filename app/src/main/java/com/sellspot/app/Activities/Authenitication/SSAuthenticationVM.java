package com.sellspot.app.Activities.Authenitication;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.NonNull;

import com.sellspot.app.Handlers.SSCompletionHandler;
import com.sellspot.app.Handlers.SSFirebaseHandler;
import com.sellspot.app.Models.User;
import com.sellspot.app.Networking.SSFirebaseAuthManager;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.sellspot.app.Networking.SSFirestoreManager;
import java.util.concurrent.TimeUnit;

public class SSAuthenticationVM {
    private String mVerificationId;
    SSFirebaseHandler signupHandler;
    SSCompletionHandler successHandler;
    SSCompletionHandler failureHandler;
    SSCompletionHandler autoDetectHandler;
    SSFirebaseHandler verificationHandler;
    public SSFirebaseHandler uploadGuestCartHandler;

    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    public SSAuthenticationVM() {
        initAuthenticationHandlers();
    }

    public void createNewUser(User user, Context context) {
        SSFirebaseAuthManager.authHelper.createUser(user, context, signupHandler);
    }

    public void verifyPhoneNumber(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);
        SSFirebaseAuthManager.authHelper.signInWithPhoneAuthCredential(credential,verificationHandler);
    }

    private void initAuthenticationHandlers() {
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                if(credential.getSmsCode() != null) {
                    autoDetectHandler.completionHandler(true,credential.getSmsCode().toString());
                    SSFirebaseAuthManager.authHelper.signInWithPhoneAuthCredential(credential,verificationHandler);
                }
                else{
                    autoDetectHandler.completionHandler(true,"");
                    verificationHandler.completionHandler(true, "");
                }
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    failureHandler.completionHandler(false, e.toString());
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    failureHandler.completionHandler(false, e.toString());
                } else {
                    failureHandler.completionHandler(false, e.toString());
                }
            }

            @Override
            public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken token) {
                mVerificationId = verificationId;
                mResendToken = token;
                successHandler.completionHandler(true, "");
            }
        };
    }

    public void sendVerificationCode(String number, Activity context) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(number, 60, TimeUnit.SECONDS, context, mCallbacks);
    }

    public void resendVerificationCode(String number, Activity context) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(number, 60, TimeUnit.SECONDS, context, mCallbacks, mResendToken);
    }
    public void uploadGuestUserCart()
    {
        SSFirestoreManager.firestoreHelper.uploadGuestUserCart(uploadGuestCartHandler);
    }
}
