package com.sellspot.app.AdminDirectory.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.GridView;
import android.widget.TextView;

import com.sellspot.app.AdminDirectory.Adapters.AdminReviewProductsAdapter;
import com.sellspot.app.Constants.SSFirestoreConstants;
import com.sellspot.app.Models.Product;
import com.sellspot.app.Networking.SSFirebaseManager;
import com.sellspot.app.R;
import com.sellspot.app.Shared.HomeLastProduct;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class AdminReviewProducts extends AppCompatActivity {

    private GridView homeGridView;
    private TextView noProductsLabel;
    private AdminReviewProductsAdapter homeAdapter;
    private SwipeRefreshLayout homerefresh;
    private FloatingActionButton upButton;

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_review_products);
        initUIAttributes();
        setHomeUI();
    }

    public void initUIAttributes() {
        this.getSupportActionBar().hide();
        homerefresh = (SwipeRefreshLayout) findViewById(R.id.adminreviewrefresh);
        noProductsLabel = (TextView) findViewById(R.id.adminreviewnoProductsLabel);
        homeGridView = (GridView) findViewById(R.id.adminreviewgridview);
        upButton = (FloatingActionButton) findViewById(R.id.adminreviewupButton);
        context = this;
        HomeLastProduct.shared.setAdminHomeLastProduct(null);
    }

    public void setHomeUI() {
        homeAdapter = new AdminReviewProductsAdapter(this, new ArrayList<Product>(), (Activity) this);
        homeGridView.setAdapter(homeAdapter);
        upButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                homeGridView.smoothScrollToPosition(0);
            }
        });
        populateHome();
        homerefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                populateHome();
            }
        });

        homeGridView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if(firstVisibleItem > 0) {
                    upButton.setVisibility(View.VISIBLE);
                } else {
                    upButton.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    void populateHome() {
        HomeLastProduct.shared.setAdminHomeLastProduct(null);
        homeAdapter.products.clear();

        Query collectionRef;
        collectionRef = SSFirebaseManager.firestoreDatabase.collectionGroup(SSFirestoreConstants.userproducts)
                .orderBy(SSFirestoreConstants.time, Query.Direction.DESCENDING)
                .limit(16);

        collectionRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (queryDocumentSnapshots.size() > 0) {
                    HomeLastProduct.shared.setAdminHomeLastProduct(queryDocumentSnapshots.getDocuments()
                            .get(queryDocumentSnapshots.size() - 1));
                }
                List<Product> products = queryDocumentSnapshots.toObjects(Product.class);
                homerefresh.setRefreshing(false);
                if (products.size() > 0) {
                    if(products.size() <= 0) {
                        noProductsLabel.setVisibility(View.VISIBLE);
                        homeGridView.setVisibility(View.INVISIBLE);
                    }
                    else {
                        noProductsLabel.setVisibility(View.INVISIBLE);
                        homeGridView.setVisibility(View.VISIBLE);
                        homeAdapter.products = products;
                        homeAdapter.notifyDataSetChanged();
                    }

                } else {
                    noProductsLabel.setVisibility(View.VISIBLE);
                    homeGridView.setVisibility(View.INVISIBLE);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });
    }
}