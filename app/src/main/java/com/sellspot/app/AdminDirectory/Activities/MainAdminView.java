package com.sellspot.app.AdminDirectory.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import com.sellspot.app.Activities.Welcome.SSWelcomeView;
import com.sellspot.app.AdminDirectory.Adapters.MainAdminViewAdapter;
import com.sellspot.app.R;
import com.sellspot.app.Shared.CurrentUser;
import com.sellspot.app.Shared.HomeLastProduct;

import java.util.ArrayList;
import java.util.List;

public class MainAdminView extends AppCompatActivity {

    List<String> dataArray = new ArrayList<>();

    private RecyclerView dataRecyclerView;
    private MainAdminViewAdapter mainAdminViewAdapter;
    private Context context;

    boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_admin_view);
        initUIAttributes();
    }

    void initUIAttributes() {
        this.getSupportActionBar().hide();
        dataRecyclerView = findViewById(R.id.dataRecyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        dataRecyclerView.setLayoutManager(linearLayoutManager);
        dataRecyclerView.setItemAnimator(new DefaultItemAnimator());
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(dataRecyclerView.getContext(), linearLayoutManager.getOrientation());
        dataRecyclerView.addItemDecoration(dividerItemDecoration);
        dataRecyclerView.setHasFixedSize(true);
        dataRecyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        context = this;
        dataArray.add("Go To Placed Orders");
        dataArray.add("Go To In Process Orders");
        dataArray.add("Go To Dispatched Orders");
        dataArray.add("Go To Delivered Orders");
        dataArray.add("Go To Cancelled/Returned Orders");
        dataArray.add("View Users");
        dataArray.add("Review Products");
        mainAdminViewAdapter = new MainAdminViewAdapter(context,(Activity)context,dataArray);
        dataRecyclerView.setAdapter(mainAdminViewAdapter);
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            finishAffinity();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }

    public void logOutPressed(View view) {
        CurrentUser.shared.removeCurrentUserEmail(context);
        CurrentUser.shared.removeCurrentUserPassword(context);
        HomeLastProduct.shared.setAdminHomeLastProduct(null);
        Intent intent = new Intent(this, SSWelcomeView.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
    }
}