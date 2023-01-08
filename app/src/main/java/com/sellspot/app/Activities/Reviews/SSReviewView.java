package com.sellspot.app.Activities.Reviews;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.sellspot.app.Handlers.SSReviewsHandler;
import com.sellspot.app.Models.Review;
import com.sellspot.app.R;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.DividerItemDecoration;
import com.sellspot.app.Adapters.SSReviewsAdapter;
import com.sellspot.app.Constants.SSConstants;

import java.util.List;

public class SSReviewView extends AppCompatActivity {

    private RecyclerView reviewRecyclerView;
    private SSReviewsAdapter reviewsAdapter;
    private Context context;

    private String userReviews; //iss user k reviews get karnay hain tou iska firebase listener lagay ga
    private SSReviewsVM viewModel = new SSReviewsVM();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reviews_view);
        initUI();
        initialiseVM();
        viewModel.getReviews(userReviews);
    }

    private void initUI() {
        this.getSupportActionBar().hide();
        reviewRecyclerView = (RecyclerView) findViewById(R.id.reviewRecyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        reviewRecyclerView.setLayoutManager(linearLayoutManager);
        reviewRecyclerView.setItemAnimator(new DefaultItemAnimator());
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(reviewRecyclerView.getContext(), linearLayoutManager.getOrientation());
        reviewRecyclerView.addItemDecoration(dividerItemDecoration);
        reviewRecyclerView.setHasFixedSize(true);
        reviewRecyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        userReviews = getIntent().getExtras().getString(SSConstants.REVIEW_KEY);
        context= this;
    }

    private void initialiseVM() {
        viewModel.getReviewsHandler = new SSReviewsHandler() {
            @Override
            public void completionHandler(Boolean success, List<Review> reviews, String error) {
                if(success) {
                    reviewsAdapter = new SSReviewsAdapter(context, reviews);
                    reviewRecyclerView.setAdapter(reviewsAdapter);
                }else{
                    Toast.makeText(context, error, Toast.LENGTH_LONG).show();
                }

            }
        };

    }
}
