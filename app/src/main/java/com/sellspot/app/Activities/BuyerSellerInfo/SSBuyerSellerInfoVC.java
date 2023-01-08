package com.sellspot.app.Activities.BuyerSellerInfo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.sellspot.app.Adapters.BuyerSellerInfoAdapter;
import com.sellspot.app.R;

public class SSBuyerSellerInfoVC extends AppCompatActivity {

    RecyclerView buyersellerRecyclerView;
    TextView headingLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buyer_seller_info_v_c);

        this.getSupportActionBar().hide();

        headingLabel = (TextView) findViewById(R.id.headingLabell);

        Integer type = getIntent().getIntExtra("usertype",0);

        if (type == 1) {
            headingLabel.setText("Buy Products through Sell Spot");
        } else {
            headingLabel.setText("Sell Products through Sell Spot");
        }

        buyersellerRecyclerView = (RecyclerView) findViewById(R.id.buyersellerRecyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        buyersellerRecyclerView.setLayoutManager(linearLayoutManager);
        buyersellerRecyclerView.setItemAnimator(new DefaultItemAnimator());
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(buyersellerRecyclerView.getContext(), linearLayoutManager.getOrientation());
        buyersellerRecyclerView.addItemDecoration(dividerItemDecoration);
        buyersellerRecyclerView.setHasFixedSize(true);
        buyersellerRecyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        BuyerSellerInfoAdapter adapter = new BuyerSellerInfoAdapter(this, (Activity) this, type);
        buyersellerRecyclerView.setAdapter(adapter);
    }
}