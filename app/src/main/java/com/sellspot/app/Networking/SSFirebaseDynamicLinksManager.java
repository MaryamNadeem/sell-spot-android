package com.sellspot.app.Networking;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
import com.google.firebase.dynamiclinks.ShortDynamicLink;
import com.sellspot.app.Constants.SSConstants;
import com.sellspot.app.Constants.SSFirestoreConstants;
import com.sellspot.app.Handlers.SSCompletionHandler;

public class SSFirebaseDynamicLinksManager {
    public static SSFirebaseDynamicLinksManager shared = new SSFirebaseDynamicLinksManager();

    public Uri receivedLink;
    public Boolean isLinkReceived = false;

    private SSFirebaseDynamicLinksManager() {}

    public void handleDynamicLinks(Intent intent, SSCompletionHandler completionHandler) {
        FirebaseDynamicLinks.getInstance().getDynamicLink(intent).addOnSuccessListener(new OnSuccessListener<PendingDynamicLinkData>() {
            @Override
            public void onSuccess(@NonNull PendingDynamicLinkData pendingDynamicLinkData) {
                Log.i("SplashVC", "Coming in dynamic link success");
                if (pendingDynamicLinkData != null) {
                    receivedLink = pendingDynamicLinkData.getLink();
                }
                if (receivedLink != null) {
                    Log.i("SplashVC", "Here's the deep link URL:\n" +
                            receivedLink.toString());
                    String productID = receivedLink.getQueryParameter(SSFirestoreConstants.productid);
                    Log.i("SplashVC", productID);
                    String userID = receivedLink.getQueryParameter(SSFirestoreConstants.userid);
                    Log.i("SplashVC", userID);
                    isLinkReceived = true;
                    completionHandler.completionHandler(true, "");
                } else {
                    completionHandler.completionHandler(false, "");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                completionHandler.completionHandler(false, "");
                Log.e("SplashVC", "Couldn't receive Dynamic Link Data");
            }
        });
    }

    public void createDynamicLink(String productID, String userID, String title, String description, Uri imageURL, SSCompletionHandler completionHandler) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(SSFirestoreConstants.dynamicLinkScheme)
                .authority(SSFirestoreConstants.dynamicLinkHost)
                .appendPath(SSFirestoreConstants.dynamicLinkPath)
                .appendQueryParameter(SSFirestoreConstants.productid, productID)
                .appendQueryParameter(SSFirestoreConstants.userid, userID);
        String url = builder.build().toString();

        Task<ShortDynamicLink> dynamicLink = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLink(Uri.parse(url))
                .setDomainUriPrefix(SSFirestoreConstants.dynamicLinkURL)
                .setAndroidParameters(
                        new DynamicLink.AndroidParameters.Builder(SSFirestoreConstants.PACKAGE_NAME)
                                .build())
                .setIosParameters(
                        new DynamicLink.IosParameters.Builder(SSConstants.iOSBundleID)
                                .setAppStoreId(SSConstants.iOSAppStoreID)
                                .build())
                .setSocialMetaTagParameters(
                        new DynamicLink.SocialMetaTagParameters.Builder()
                                .setTitle(title)
                                .setDescription(description)
                                .setImageUrl(imageURL)
                                .build())
                .buildShortDynamicLink();

        dynamicLink.addOnSuccessListener(new OnSuccessListener<ShortDynamicLink>() {
            @Override
            public void onSuccess(@NonNull ShortDynamicLink shortDynamicLink) {
                Uri shortLink = shortDynamicLink.getShortLink();
                completionHandler.completionHandler(true, shortLink.toString());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                completionHandler.completionHandler(false, e.getLocalizedMessage());
            }
        });
    }
    
    public void removeDynamicLinkData() {
        isLinkReceived = false;
        receivedLink = null;
    }
}
