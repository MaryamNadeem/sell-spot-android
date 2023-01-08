package com.sellspot.app.AdminDirectory.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.sellspot.app.AdminDirectory.Adapters.OrdersAdapter;
import com.sellspot.app.Constants.SSFirestoreConstants;
import com.sellspot.app.Models.Order;
import com.sellspot.app.Networking.SSFirebaseManager;
import com.sellspot.app.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class Orders extends AppCompatActivity {

    private RecyclerView ordersRecyclerView;
    private OrdersAdapter ordersAdapter;
    private Context context;

    String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);
        initUIAttributes();
        fetchData();
    }

    void initUIAttributes() {
        this.getSupportActionBar().hide();
        ordersRecyclerView = findViewById(R.id.ordersRecyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        ordersRecyclerView.setLayoutManager(linearLayoutManager);
        ordersRecyclerView.setItemAnimator(new DefaultItemAnimator());
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(ordersRecyclerView.getContext(), linearLayoutManager.getOrientation());
        ordersRecyclerView.addItemDecoration(dividerItemDecoration);
        ordersRecyclerView.setHasFixedSize(true);
        ordersRecyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        context = this;
        type = getIntent().getStringExtra("order type");
        final SwipeRefreshLayout pullToRefresh =(SwipeRefreshLayout) findViewById(R.id.orders_swipe_container);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchData();
                pullToRefresh.setRefreshing(false);
            }
        });
    }

    void fetchData() {
        SSFirebaseManager.firestoreDatabase.collection("orders")
                .whereEqualTo(SSFirestoreConstants.status, type)
                .orderBy("time", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<Order> orders = queryDocumentSnapshots.toObjects(Order.class);
                        ordersAdapter = new OrdersAdapter(context,(Activity)context,orders,type);
                        ordersRecyclerView.setAdapter(ordersAdapter);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
    }
}