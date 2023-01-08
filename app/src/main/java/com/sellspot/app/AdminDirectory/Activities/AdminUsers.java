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

import com.google.firebase.firestore.Query;
import com.sellspot.app.AdminDirectory.Adapters.AdminUsersAdapter;
import com.sellspot.app.Constants.SSFirestoreConstants;
import com.sellspot.app.Models.User;
import com.sellspot.app.Networking.SSFirebaseManager;
import com.sellspot.app.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class AdminUsers extends AppCompatActivity {

    private RecyclerView usersRecyclerView;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_users);
        initUIAttributes();
        fetchData();
    }

    void initUIAttributes() {
        this.getSupportActionBar().hide();
        usersRecyclerView = findViewById(R.id.usersRecyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        usersRecyclerView.setLayoutManager(linearLayoutManager);
        usersRecyclerView.setItemAnimator(new DefaultItemAnimator());
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(usersRecyclerView.getContext(), linearLayoutManager.getOrientation());
        usersRecyclerView.addItemDecoration(dividerItemDecoration);
        usersRecyclerView.setHasFixedSize(true);
        usersRecyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        context = this;

        final SwipeRefreshLayout pullToRefresh =(SwipeRefreshLayout) findViewById(R.id.admin_users_swipe_container);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchData();
                pullToRefresh.setRefreshing(false);
            }
        });
    }

    void fetchData() {
        SSFirebaseManager.firestoreDatabase.collection(SSFirestoreConstants.users).orderBy(SSFirestoreConstants.time, Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<User> users = queryDocumentSnapshots.toObjects(User.class);
                        AdminUsersAdapter adminUsersAdapter = new AdminUsersAdapter(context,(Activity)context, users);
                        usersRecyclerView.setAdapter(adminUsersAdapter);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }
}