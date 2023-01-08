package com.sellspot.app.Activities.Search;

import com.sellspot.app.Handlers.SSPostsHandler;
import com.sellspot.app.Handlers.SSUsersHandler;
import com.sellspot.app.Networking.SSFirestoreManager;

public class SSSearchVM {
    SSUsersHandler usersHandler;
    SSPostsHandler productsMaterialHandler;
    SSPostsHandler productsSizeHandler;
    SSPostsHandler productsTagHandler;


    public void search(String searchString) {
        SSFirestoreManager.firestoreHelper.searchUser(searchString,usersHandler);
    }

    public void searchProductByMaterial(String searchString) {
        SSFirestoreManager.firestoreHelper.searchProductByMaterial(searchString, productsMaterialHandler);
    }

    public void searchProductBySize(String searchString) {
        SSFirestoreManager.firestoreHelper.searchProductBySize(searchString, productsSizeHandler);
    }

    public void searchProductByTags(String searchString) {
        SSFirestoreManager.firestoreHelper.searchProductByTags(searchString, productsTagHandler);
    }
}
