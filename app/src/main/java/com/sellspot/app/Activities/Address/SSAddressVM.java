package com.sellspot.app.Activities.Address;

import com.sellspot.app.Handlers.SSFirebaseHandler;
import com.sellspot.app.Networking.SSFirestoreManager;
import com.sellspot.app.Shared.CurrentUser;

public class SSAddressVM {

    SSFirebaseHandler updateHandler;

    public void updateUserAddress(String address) {
        SSFirestoreManager.firestoreHelper.updateUserAddress(address);
        CurrentUser.shared.getCurrentUser().setAddress(address);
        updateHandler.completionHandler(true, "");
    }
}
