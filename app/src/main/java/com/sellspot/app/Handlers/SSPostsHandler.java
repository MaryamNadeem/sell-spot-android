package com.sellspot.app.Handlers;

import com.sellspot.app.Models.Product;

import java.util.List;

public interface SSPostsHandler {
    void completionHandler(Boolean success, List<Product> products, String error);
}
