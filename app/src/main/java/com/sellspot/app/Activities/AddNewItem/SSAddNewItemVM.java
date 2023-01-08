package com.sellspot.app.Activities.AddNewItem;

import com.sellspot.app.Handlers.SSFirebaseHandler;
import com.sellspot.app.Models.Product;
import com.sellspot.app.Networking.SSFirestoreManager;

public class SSAddNewItemVM {
    public SSFirebaseHandler addItemHandler;

    public void addNewItem(Product product) {
        SSFirestoreManager.firestoreHelper.addNewProduct(product, addItemHandler);
    }
}
