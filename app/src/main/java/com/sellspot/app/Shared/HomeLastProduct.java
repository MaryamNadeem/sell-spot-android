package com.sellspot.app.Shared;

import com.sellspot.app.Models.Order;
import com.google.firebase.firestore.DocumentSnapshot;

public class HomeLastProduct {
    public static final HomeLastProduct shared = new HomeLastProduct();
    private DocumentSnapshot homeLastProduct;

    private DocumentSnapshot adminHomeLastProduct;

    public Order selectedOrder;

    public DocumentSnapshot getHomeLastProduct () {
        return homeLastProduct;
    }

    public void setHomeLastProduct(DocumentSnapshot homeLastProduct) {
        this.homeLastProduct = homeLastProduct;
    }

    public DocumentSnapshot getAdminHomeLastProduct() {
        return adminHomeLastProduct;
    }

    public void setAdminHomeLastProduct(DocumentSnapshot adminHomeLastProduct) {
        this.adminHomeLastProduct = adminHomeLastProduct;
    }
}
