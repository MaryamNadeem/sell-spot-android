package com.sellspot.app.Activities.Following;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.sellspot.app.Models.FollowerAndFollowing;
import com.sellspot.app.R;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.sellspot.app.Adapters.SSFollowingAdapter;
import com.sellspot.app.Constants.SSConstants;
import com.sellspot.app.Handlers.SSFollowingsHandler;

import java.util.List;

public class SSFollowingView extends AppCompatActivity {

    private RecyclerView followingRecyclerView;
    private SSFollowingAdapter followingAdapter;
    private Context context;

    private String userFollowings; //iss user k followings get karnay hain tou iska firebase listener lagay ga
    private SSFollowingVM viewModel = new SSFollowingVM();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.following_view);
        initUI();
        initializeVM();
        viewModel.getFollowings(userFollowings);
    }

    private void initUI() {
        this.getSupportActionBar().hide();
        followingRecyclerView = (RecyclerView) findViewById(R.id.followingRecyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        followingRecyclerView.setLayoutManager(linearLayoutManager);
        followingRecyclerView.setItemAnimator(new DefaultItemAnimator());
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(followingRecyclerView.getContext(), linearLayoutManager.getOrientation());
        followingRecyclerView.addItemDecoration(dividerItemDecoration);
        followingRecyclerView.setHasFixedSize(true);
        followingRecyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        userFollowings = getIntent().getExtras().getString(SSConstants.FOLLOWING_KEY);
        context = this;
        final SwipeRefreshLayout pullToRefresh =(SwipeRefreshLayout) findViewById(R.id.following_swipe_container);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                viewModel.getFollowings(userFollowings);
                pullToRefresh.setRefreshing(false);
            }
        });
    }

    private void initializeVM() {
        viewModel.getFollowingsHandler = new SSFollowingsHandler() {
            @Override
            public void completionHandler(Boolean success, List<FollowerAndFollowing> followings, String error) {
            if(success) {
                followingAdapter = new SSFollowingAdapter(context,(Activity)context,followings);
                followingRecyclerView.setAdapter(followingAdapter);
            }
            else {
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
