package com.sellspot.app.Networking;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.sellspot.app.Handlers.SSFirebaseHandler;
import com.sellspot.app.Models.Product;
import com.sellspot.app.Shared.CurrentUser;
import com.sellspot.app.Shared.SharedFunctions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SSFirebaseStorageManager {
    public static FirebaseStorage storage = FirebaseStorage.getInstance();

    public static void downloadImage(String ImageUrl, final ImageView imageView)
    {
        StorageReference httpsReference = storage.getReferenceFromUrl(ImageUrl);
        final long ONE_MEGABYTE = 5*1024 * 1024;
        httpsReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                imageView.setImageBitmap(SharedFunctions.decodeImage(bytes));

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                Log.e("Image Download Error","Image download failure");

            }
        });


    }

    public static void uploadImage(String storageReference, Bitmap bitmap, final SSFirebaseHandler handler) {
        StorageReference storageRef = storage.getReference();
        final StorageReference imageRef = storageRef.child(storageReference);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        UploadTask uploadTask = imageRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                handler.completionHandler(false, "Image Upload failure");
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
            imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                   @Override
                   public void onSuccess(Uri uri) {
               CurrentUser.shared.getCurrentUser().setImage(uri.toString());
               SSFirestoreManager.firestoreHelper.setUserProfileImage(uri.toString());
               handler.completionHandler(true, "");
                   }
           });
            }
        });

    }

    public static void uploadProductImage(final String productID, final Product p, final List<Bitmap> images, final SSFirebaseHandler handler) {
        final List<String> urls = new ArrayList<>();
        HashMap<Integer, String> urlHash = new HashMap<Integer, String>();
        final int[] uploadCount = {0};


        for(int i = 0; i< images.size();i++) {
            final int pos = i;
            String storageReference = CurrentUser.shared.getCurrentUser().getUserid()+"/products/"+productID+i+".jpg";
            StorageReference storageRef = storage.getReference();
            final StorageReference imageRef = storageRef.child(storageReference);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Bitmap bitmap = images.get(i);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos);
            byte[] data = baos.toByteArray();
            UploadTask uploadTask = imageRef.putBytes(data);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    handler.completionHandler(false, "Image Upload failure");
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            urlHash.put(pos, uri.toString());
                            if (pos == 0) {
                                SSFirestoreManager.firestoreHelper.createAndSaveDynamicLink(productID, p, uri);
                            }
                            uploadCount[0]++;
                            if( uploadCount[0] == images.size()) {
                                for(int j = 0; j<images.size();j++)
                                {
                                    urls.add(urlHash.get(j));
                                }
                                SSFirestoreManager.firestoreHelper.setProductImage(urls, productID);
                                handler.completionHandler(true, "");
                            }
                        }
                    });
                }
            });
        }
    }
}
