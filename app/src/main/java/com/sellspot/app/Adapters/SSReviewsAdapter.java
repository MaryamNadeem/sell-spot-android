package com.sellspot.app.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sellspot.app.Models.Review;
import com.sellspot.app.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class SSReviewsAdapter extends RecyclerView.Adapter<SSReviewsAdapter.reviewsViewHolder> {
    private Context context;
    private List<Review> reviews;

    public class reviewsViewHolder extends RecyclerView.ViewHolder {
        TextView reviewerName;
        TextView reviewText;
        CircleImageView reviewerImage;

        public reviewsViewHolder(View itemView) {
            super(itemView);
            reviewerName = (TextView) itemView.findViewById(R.id.reviewerName);
            reviewText = (TextView) itemView.findViewById(R.id.reviewText);
            reviewerImage = (CircleImageView) itemView.findViewById(R.id.reviewerImage);
        }
    }

    public SSReviewsAdapter(Context context, List<Review> reviews) {
        this.context = context;
        this.reviews = reviews;
    }

    @NonNull
    @Override
    public reviewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.review_row, parent, false);
        reviewsViewHolder holder = new reviewsViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull reviewsViewHolder holder, int position) {
        holder.reviewerName.setText(reviews.get(position).getReviewername());
        holder.reviewText.setText(reviews.get(position).getReview());

    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }
}
