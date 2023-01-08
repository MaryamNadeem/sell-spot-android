package com.sellspot.app.Handlers;

import com.sellspot.app.Models.Product;

public interface SSPostHandler {
    void completionHandler(Boolean success, Product products, String error);

}
