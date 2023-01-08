package com.sellspot.app.Activities.Camera;

import android.graphics.Bitmap;

import com.sellspot.app.Handlers.SSFirebaseHandler;
import com.sellspot.app.Networking.SSFirebaseStorageManager;
import com.sellspot.app.Shared.CurrentUser;
import com.sellspot.app.Shared.SharedVariables;

public class SSCameraVM {
    SSFirebaseHandler pictureSuccess;
    public void setUserProfileImage(Bitmap bitmap) {
        String storageRef = CurrentUser.shared.getCurrentUser().getUserid()+"/profilePicture.jpg";
        SSFirebaseStorageManager.uploadImage(storageRef,bitmap, pictureSuccess);
    }

    public void addImageFromCamera(Bitmap image) {
        SharedVariables.shared.productImages.add(image);
    }

    public void updateImageFromCamera(Bitmap image, int index) {
        SharedVariables.shared.productImages.set(index, image);
    }
}
