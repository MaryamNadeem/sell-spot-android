package com.sellspot.app.Activities.Search;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.sellspot.app.Adapters.SSSearchProductAdapter;
import com.sellspot.app.Constants.SSConstants;
import com.sellspot.app.Handlers.SSPostsHandler;
import com.sellspot.app.Handlers.SSUsersHandler;
import com.sellspot.app.Models.Product;
import com.sellspot.app.Models.User;
import com.sellspot.app.R;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.sellspot.app.Adapters.SSSearchAdapter;

public class SSSearchView  < E > extends AppCompatActivity {

    private GridView searchGridView;
    private EditText searchField;
    private ImageButton searchButton;
    private String searchString;
    private RecyclerView userSearchRV;
    private SSSearchVM viewModel= new SSSearchVM();
    private Context context;
    private List<User> objects = new ArrayList<>();
    private SSSearchProductAdapter productAdapter;
    private SSSearchAdapter searchAdapter;
    private List<Product> searchProducts = new ArrayList<>();
    private ProgressBar searchProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_view);
        initUIAttributes();
        initializeVM();
        if ( getIntent().getExtras() != null ) {
            if(getIntent().getExtras().getBoolean(SSConstants.viewTags)) {
                objects.clear();
                searchField.setVisibility(View.GONE);
                searchButton.setVisibility(View.GONE);
                String tag = getIntent().getExtras().getString(SSConstants.tagString);
                viewModel.searchProductByTags(tag.replace("#", ""));
            }
        }
    }

    private void initUIAttributes() {
        this.getSupportActionBar().hide();
        context = this;
        searchProgress = (ProgressBar) findViewById(R.id.searchProgress);
        userSearchRV = (RecyclerView) findViewById(R.id.userSearchRV);
        searchButton = (ImageButton) findViewById(R.id.searchButton);
        searchGridView = findViewById(R.id.searchGridView);
        searchField = (EditText) findViewById(R.id.searchText);
        productAdapter = new SSSearchProductAdapter((Activity)context, context, searchProducts);
        searchGridView.setAdapter(productAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL,false);
        userSearchRV.setLayoutManager(linearLayoutManager);
        userSearchRV.setItemAnimator(new DefaultItemAnimator());
        userSearchRV.setHasFixedSize(true);
        userSearchRV.setOverScrollMode(View.OVER_SCROLL_NEVER);

        searchAdapter = new SSSearchAdapter(context, objects);
        userSearchRV.setAdapter(searchAdapter);

        searchField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    performSearch();
                    return true;
                }
                return false;
            }
        });
    }
    public void onSearchClick(View v) {
        performSearch();
    }

    void performSearch() {
        searchString = searchField.getText().toString();
        if(!searchString.isEmpty()) {
            searchProgress.setVisibility(View.VISIBLE);
            searchProgress.animate();
            objects.clear();
            searchProducts.clear();
            viewModel.searchProductByMaterial(searchString);
        }
    }

    public void initializeVM(){
        viewModel.usersHandler = new SSUsersHandler() {
            @Override
            public void completionHandler(Boolean success, List<User> users, String error) {
               if(success) {
                   objects.addAll(users);
                   Set<User> set = new HashSet<User>();
                   for(int i = 0; i < objects.size(); i++){
                       set.add(objects.get(i));
                   }
                   searchAdapter.objects = new ArrayList<User>(set);
                   searchAdapter.notifyDataSetChanged();
                   searchProgress.setIndeterminate(true);
                   searchProgress.setVisibility(View.INVISIBLE);
               } else {
                   searchProgress.setIndeterminate(true);
                   searchProgress.setVisibility(View.INVISIBLE);
               }
            }
        };

        viewModel.productsMaterialHandler = new SSPostsHandler() {
            @Override
            public void completionHandler(Boolean success, List<Product> products, String error) {
                if(success) {
                    searchProducts.addAll(products);
                    Set<Product> set = new HashSet<Product>();
                    for(int i = 0; i < searchProducts.size(); i++) {
                        set.add(searchProducts.get(i));
                    }
                    productAdapter.products = new ArrayList<Product>(set);
                    productAdapter.notifyDataSetChanged();
                    viewModel.searchProductBySize(searchString);
                } else {
                    searchProgress.setIndeterminate(true);
                    searchProgress.setVisibility(View.INVISIBLE);
                }
            }
        };

        viewModel.productsSizeHandler = new SSPostsHandler() {
            @Override
            public void completionHandler(Boolean success, List<Product> products, String error) {
                if(success) {
                    searchProducts.addAll(products);
                    Set<Product> set = new HashSet<Product>();
                    for(int i = 0; i < searchProducts.size(); i++) {
                        set.add(searchProducts.get(i));
                    }
                    productAdapter.products = new ArrayList<Product>(set);
                    productAdapter.notifyDataSetChanged();
                    viewModel.searchProductByTags(searchString);
                } else {
                    searchProgress.setIndeterminate(true);
                    searchProgress.setVisibility(View.INVISIBLE);
                }
            }
        };

        viewModel.productsTagHandler = new SSPostsHandler() {
            @Override
            public void completionHandler(Boolean success, List<Product> products, String error) {
                if(success) {
                    searchProducts.addAll(products);
                    Set<Product> set = new HashSet<Product>();
                    for(int i = 0; i < searchProducts.size(); i++) {
                        set.add(searchProducts.get(i));
                    }
                    productAdapter.products = new ArrayList<Product>(set);
                    productAdapter.notifyDataSetChanged();
                    viewModel.search(searchString);
                } else {
                    searchProgress.setIndeterminate(true);
                    searchProgress.setVisibility(View.INVISIBLE);
                }
            }
        };
    }
}
