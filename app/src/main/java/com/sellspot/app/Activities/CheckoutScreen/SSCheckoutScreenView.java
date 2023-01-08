package com.sellspot.app.Activities.CheckoutScreen;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.sellspot.app.Adapters.SSCheckoutScreenAdapter;
import com.sellspot.app.Models.CartPerStore;
import com.sellspot.app.R;
import com.sellspot.app.Shared.SharedVariables;

import java.util.List;

public class SSCheckoutScreenView extends AppCompatActivity {

    List<CartPerStore> productsArray;
    RecyclerView checkoutInfoRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout_screen_view);
        initUIAttributes();
    }

    private void initUIAttributes() {
        this.getSupportActionBar().hide();
        productsArray = SharedVariables.shared.getCartItems();
        checkoutInfoRecyclerView = findViewById(R.id.checkoutInfoRecyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        checkoutInfoRecyclerView.setLayoutManager(linearLayoutManager);
        checkoutInfoRecyclerView.setItemAnimator(new DefaultItemAnimator());
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(checkoutInfoRecyclerView.getContext(), linearLayoutManager.getOrientation());
        checkoutInfoRecyclerView.addItemDecoration(dividerItemDecoration);
        checkoutInfoRecyclerView.setHasFixedSize(true);
        checkoutInfoRecyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        SSCheckoutScreenAdapter mainAdminViewAdapter = new SSCheckoutScreenAdapter(this, (Activity) this, productsArray);
        checkoutInfoRecyclerView.setAdapter(mainAdminViewAdapter);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
    }
}