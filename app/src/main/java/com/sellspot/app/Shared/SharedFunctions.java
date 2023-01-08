package com.sellspot.app.Shared;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import com.sellspot.app.Activities.Camera.SSCameraView;
import com.sellspot.app.Constants.SSConstants;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SharedFunctions {

    public static String encodeImage(Bitmap bitmap) {
        ByteArrayOutputStream blob = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, blob);
        byte[] bitmapdata = blob.toByteArray();
        String image = Base64.encodeToString(bitmapdata, Base64.DEFAULT);
        return image;
    }

    public static Bitmap decodeImage(byte[] outImage) {
        ByteArrayInputStream imageStream = new ByteArrayInputStream(outImage);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        Bitmap userImage = BitmapFactory.decodeStream(imageStream);
        userImage.compress(Bitmap.CompressFormat.JPEG,100,stream);
        return userImage;
    }

    public static Bitmap decodeImagefromString(String image) {
        byte outImage[] = Base64.decode(image, Base64.DEFAULT);
        ByteArrayInputStream imageStream = new ByteArrayInputStream(outImage);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        Bitmap userImage = BitmapFactory.decodeStream(imageStream);
        userImage.compress(Bitmap.CompressFormat.JPEG,100,stream);
        return userImage;
    }

    public static boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            Log.d("Camera status", "this device has a camera");
            return true;
        } else {
            Log.d("Camera status", "no camera on this device");
            return false;
        }
    }

    public static void onCameraClick(Context context, int checkActivity) {
        if (!checkCameraHardware(context)) {
            return;
        }
        Intent intent = new Intent(context, SSCameraView.class);
        intent.putExtra(SSConstants.IMAGE_KEY, checkActivity);
        context.startActivity(intent);
    }

    public static String getCurrentTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(System.currentTimeMillis());
        return formatter.format(date);
    }
}
