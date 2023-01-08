package com.sellspot.app.Activities.OrderHistory;

import android.os.Bundle;
import android.view.View;
import com.sellspot.app.R;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import com.sellspot.app.Adapters.SSOrderHistoryAdapter;

public class SSOrderHistoryView extends AppCompatActivity {

    private RecyclerView orderHistoryRecyclerView;
    private SSOrderHistoryAdapter orderHistoryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_history_view);
        initAttributes();
    }

    private void initAttributes() {
        this.getSupportActionBar().hide();
        orderHistoryRecyclerView = (RecyclerView) findViewById(R.id.orderHistoryRecyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        orderHistoryRecyclerView.setLayoutManager(linearLayoutManager);
        orderHistoryRecyclerView.setItemAnimator(new DefaultItemAnimator());
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(orderHistoryRecyclerView.getContext(), linearLayoutManager.getOrientation());
        orderHistoryRecyclerView.addItemDecoration(dividerItemDecoration);
        orderHistoryRecyclerView.setHasFixedSize(true);
        orderHistoryRecyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        orderHistoryAdapter = new SSOrderHistoryAdapter(this);
        orderHistoryRecyclerView.setAdapter(orderHistoryAdapter);
    }
}
