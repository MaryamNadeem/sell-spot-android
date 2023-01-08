package com.sellspot.app.AdminDirectory.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.sellspot.app.AdminDirectory.Adapters.OrderProductsAdapter;
import com.sellspot.app.R;
import com.sellspot.app.Shared.HomeLastProduct;

public class OrderProducts extends AppCompatActivity {

    RecyclerView productsRecyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_products);
        initUIAttributes();
    }

    void initUIAttributes() {
        this.getSupportActionBar().hide();
        productsRecyclerView = (RecyclerView) findViewById(R.id.productsRecyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        productsRecyclerView.setLayoutManager(linearLayoutManager);
        productsRecyclerView.setItemAnimator(new DefaultItemAnimator());
        productsRecyclerView.setHasFixedSize(true);
        productsRecyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        OrderProductsAdapter adapter = new OrderProductsAdapter(this, (Activity)this, HomeLastProduct.shared.selectedOrder);
        productsRecyclerView.setAdapter(adapter);
    }
}