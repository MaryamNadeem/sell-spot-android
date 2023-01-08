package com.sellspot.app.Activities.UploadImage;

import android.graphics.Bitmap;

import com.sellspot.app.Handlers.SSFirebaseHandler;
import com.sellspot.app.Networking.SSFirebaseStorageManager;
import com.sellspot.app.Shared.CurrentUser;

public class SSUploadImageVM {
    SSFirebaseHandler pictureSuccess;

    public void setUserProfileImage(Bitmap bitmap) {
        String storageRef = CurrentUser.shared.getCurrentUser().getUserid()+"/profilePicture.jpg";
        SSFirebaseStorageManager.uploadImage(storageRef,bitmap, pictureSuccess);
    }
}
