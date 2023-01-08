package com.sellspot.app.Shared;

import android.graphics.Bitmap;

import com.sellspot.app.Handlers.SSCompletionHandler;
import com.sellspot.app.Models.CartPerStore;
import com.sellspot.app.Models.Product;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public class SharedVariables {
    public static final SharedVariables shared = new SharedVariables();

    private Bitmap imageProfileOrProduct;

    public Product sharedProduct;

    public List<Bitmap> productImages = new ArrayList<>();
    public SSCompletionHandler cartProgressHandler;

    public Hashtable<String,List<Bitmap>> localProductImages= new Hashtable<String,List<Bitmap>>();

    public SSCompletionHandler increaseCartCount;
    public SSCompletionHandler decreaseCartCount;
    public SSCompletionHandler updateCartCount;

    public SSCompletionHandler changeTab;

    private List<CartPerStore> cartItems;

    public Bitmap getImage() {
        return imageProfileOrProduct;
    }

    public List<CartPerStore> getCartItems()  {
        return cartItems;
    }

    public void setCartItems(List<CartPerStore> cartItems) {
        this.cartItems = cartItems;
    }

    public void setImage(Bitmap imageProfileOrProduct) {
        this.imageProfileOrProduct = imageProfileOrProduct;
    }
}
