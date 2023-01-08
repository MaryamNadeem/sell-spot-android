package com.sellspot.app.Activities.EditProduct;

import com.sellspot.app.Handlers.SSFirebaseHandler;
import com.sellspot.app.Models.Product;
import com.sellspot.app.Networking.SSFirestoreManager;

public class SSEditProductVM {

    public SSFirebaseHandler updateItemHandler;

    public void updateProduct(Product product) {
        SSFirestoreManager.firestoreHelper.updateProduct(product, updateItemHandler);
    }
}
