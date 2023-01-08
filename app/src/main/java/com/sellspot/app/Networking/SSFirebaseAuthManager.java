package com.sellspot.app.Networking;

import android.content.Context;

import androidx.annotation.NonNull;

import com.sellspot.app.Constants.SSConstants;
import com.sellspot.app.Handlers.SSFirebaseHandler;
import com.sellspot.app.Models.User;
import com.sellspot.app.Shared.CurrentUser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.SignInMethodQueryResult;

public class SSFirebaseAuthManager {
    public static SSFirebaseAuthManager authHelper = new SSFirebaseAuthManager();

    public void createUser(final User user,Context context, final SSFirebaseHandler handler) {
        SSFirestoreManager.firestoreHelper.checkUserNameExistence(user.getUsername(), new SSFirebaseHandler() {
            @Override
            public void completionHandler(Boolean success, String error) {
                if (success) {
                    SSFirebaseManager.firebaseAuth.createUserWithEmailAndPassword(user.getEmail(), user.getPassword()).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            user.setUserid(authResult.getUser().getUid());
                            SSFirestoreManager.firestoreHelper.addUserInformation(user, new SSFirebaseHandler() {
                                @Override
                                public void completionHandler(Boolean success, String error) {
                                    if (success) {
                                        CurrentUser.shared.setCurrentUser(user);
                                        CurrentUser.shared.saveCurrentUser(context, user);
                                        handler.completionHandler(true, "");
                                    } else {
                                        handler.completionHandler(false, error);
                                    }
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            handler.completionHandler(false, e.getMessage());
                        }
                    });
                } else {
                    handler.completionHandler(false, error);
                }
            }
        });
    }

    public void signInUser(String email, String password, Context context, final SSFirebaseHandler handler) {
        SSFirebaseManager.firebaseAuth.signInWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                SSFirestoreManager.firestoreHelper.getUser(authResult.getUser().getUid(), context,  new SSFirebaseHandler() {
                    @Override
                    public void completionHandler(Boolean success, String error) {
                        if (success) {
                            handler.completionHandler(true, "");
                        } else {
                            handler.completionHandler(false, error);
                        }
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                handler.completionHandler(false, e.getMessage());
            }
        });
    }

    public void sendPasswordResetEmail(String email, final SSFirebaseHandler handler)
    {

        SSFirebaseManager.firebaseAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            handler.completionHandler(true, "");
                        }
                        else{
                            handler.completionHandler(false, "An error occurred");
                        }
                    }
                });
    }

    public void updateUserProfile(Context context, final String email, final String password, final String phone, final String address, final SSFirebaseHandler handler) {
        final FirebaseUser currentUser = SSFirebaseManager.firebaseAuth.getCurrentUser();
        AuthCredential credential = EmailAuthProvider.getCredential(CurrentUser.shared.getCurrentUser().getEmail(), CurrentUser.shared.getCurrentUser().getPassword());
        currentUser.reauthenticate(credential).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                currentUser.updatePassword(password).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        SSFirestoreManager.firestoreHelper.updateUserPassword(password);
                        CurrentUser.shared.setCurrentUserPassword(context, password);
                        CurrentUser.shared.getCurrentUser().setPassword(password);
                        currentUser.updateEmail(email).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                SSFirestoreManager.firestoreHelper.updateUserEmail(email);
                                CurrentUser.shared.setCurrentUserEmail(context, email);
                                CurrentUser.shared.getCurrentUser().setEmail(email);
                                SSFirestoreManager.firestoreHelper.updateUserPhone(phone);
                                CurrentUser.shared.getCurrentUser().setPhonenumber(phone);
                                SSFirestoreManager.firestoreHelper.updateUserAddress(address);
                                CurrentUser.shared.getCurrentUser().setAddress(address);
                                handler.completionHandler(true, "");
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                handler.completionHandler(false, SSConstants.processingError);
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        handler.completionHandler(false, SSConstants.processingError);
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                handler.completionHandler(false, SSConstants.processingError);
            }
        });
    }

    public void signInWithPhoneAuthCredential(PhoneAuthCredential credential, final SSFirebaseHandler handler) {
        SSFirebaseManager.firebaseAuth.signInWithCredential(credential).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                handler.completionHandler(true, "");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                handler.completionHandler(false, e.toString());
            }
        });

    }

    public void checkIfEmailExists(String email, final SSFirebaseHandler handler) {
        SSFirebaseManager.firebaseAuth.fetchSignInMethodsForEmail(email).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
            @Override
            public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
            boolean isNewUser = task.getResult().getSignInMethods().isEmpty();

            if (isNewUser) {
                handler.completionHandler(true, "");
            } else {
                handler.completionHandler(false,"User with email already exists");
            }
            }
        });
    }
}

