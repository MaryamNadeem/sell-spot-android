package com.sellspot.app.Activities.Settings;

import android.content.Context;
import android.graphics.Bitmap;

import com.sellspot.app.Handlers.SSFirebaseHandler;
import com.sellspot.app.Networking.SSFirebaseAuthManager;
import com.sellspot.app.Networking.SSFirebaseStorageManager;
import com.sellspot.app.Shared.CurrentUser;

public class SSSettingsVM {
    SSFirebaseHandler pictureSuccess;
    SSFirebaseHandler updateHandler;

    public void update(Context context, String email, String password, String phone, String address) {
        SSFirebaseAuthManager.authHelper.updateUserProfile(context, email, password, phone, address, updateHandler);
    }

    public void setUserProfileImage(Bitmap bitmap) {
        String storageRef = CurrentUser.shared.getCurrentUser().getUserid()+"/profilePicture.jpg";
        SSFirebaseStorageManager.uploadImage(storageRef,bitmap, pictureSuccess);
    }
}
