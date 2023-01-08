package com.sellspot.app.Handlers;

import com.sellspot.app.Models.CartPerStore;

import java.util.List;

public interface SSCartProductHandler {
    void completionHandler(Boolean success, List<CartPerStore> cartperstoreproducts , String error);
}
