package com.sellspot.app.Activities.UserImage;

import com.sellspot.app.Handlers.SSFirebaseHandler;
import com.sellspot.app.Models.CartPerStore;
import com.sellspot.app.Models.Product;
import com.sellspot.app.Networking.SSFirestoreManager;

public class SSUserImageVM {
    public SSFirebaseHandler addToCartHandler;
    public SSFirebaseHandler itemInCartHandler;
    public SSFirebaseHandler deleteHandler;

    public void addProductToCart(CartPerStore cps)
    {
        SSFirestoreManager.firestoreHelper.addProductToCart(cps, addToCartHandler);
    }
    public void checkIfItemInCart(Product product)
    {
        SSFirestoreManager.firestoreHelper.checkIfItemInCart(product, itemInCartHandler);
    }
    public void deleteProduct(String productId)
    {
        SSFirestoreManager.firestoreHelper.deleteProduct(productId, deleteHandler);
    }
}
