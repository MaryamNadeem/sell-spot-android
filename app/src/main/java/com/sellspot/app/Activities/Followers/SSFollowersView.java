package com.sellspot.app.Activities.Followers;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.sellspot.app.Models.FollowerAndFollowing;
import com.sellspot.app.R;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.sellspot.app.Adapters.SSFollowersAdapter;
import com.sellspot.app.Constants.SSConstants;
import com.sellspot.app.Handlers.SSFollowersHandler;

import android.widget.Toast;

import java.util.List;

public class SSFollowersView extends AppCompatActivity {

    private RecyclerView followersRecyclerView;
    private SSFollowersAdapter followersAdapter;
    private Context context;

    private String userFollowers; //iss user k followers get karnay hain tou iska firebase listener lagay ga
    private SSFollowersVM viewModel = new SSFollowersVM();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.followers_view);
        initUIAttributes();
        initialiseVM();
        viewModel.getFollowers(userFollowers);
    }

    private void initUIAttributes() {
        this.getSupportActionBar().hide();
        followersRecyclerView = (RecyclerView) findViewById(R.id.followersRecyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        followersRecyclerView.setLayoutManager(linearLayoutManager);
        followersRecyclerView.setItemAnimator(new DefaultItemAnimator());
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(followersRecyclerView.getContext(), linearLayoutManager.getOrientation());
        followersRecyclerView.addItemDecoration(dividerItemDecoration);
        followersRecyclerView.setHasFixedSize(true);
        followersRecyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        userFollowers = getIntent().getExtras().getString(SSConstants.FOLLOWER_KEY);
        context = this;
        final SwipeRefreshLayout pullToRefresh =(SwipeRefreshLayout) findViewById(R.id.follower_swipe_container);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                viewModel.getFollowers(userFollowers);
                pullToRefresh.setRefreshing(false);
            }
        });

    }

    private void initialiseVM() {
        viewModel.getFollowerHandler = new SSFollowersHandler() {
            @Override
            public void completionHandler(Boolean success, List<FollowerAndFollowing> followers, String error) {
            if(success) {
                followersAdapter = new SSFollowersAdapter(context,(Activity)context,followers);
                followersRecyclerView.setAdapter(followersAdapter);
            } else {
                Toast.makeText(context, error, Toast.LENGTH_LONG).show();
            }

            }
        };
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
    }
}
