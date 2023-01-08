package com.sellspot.app.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.sellspot.app.Activities.UserProfile.SSUserProfileView;
import com.sellspot.app.Constants.SSConstants;
import com.sellspot.app.Handlers.SSUserHandler;
import com.sellspot.app.Models.FollowerAndFollowing;
import com.sellspot.app.Models.User;
import com.sellspot.app.Networking.SSFirestoreManager;
import com.sellspot.app.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class SSFollowingAdapter extends RecyclerView.Adapter<SSFollowingAdapter.followingViewHolder> {
    private Context context;
    private Activity activity;
    private List<FollowerAndFollowing> following;


    public class followingViewHolder extends RecyclerView.ViewHolder {
        TextView followingName;
        CircleImageView followingImage;

        public followingViewHolder(View itemView) {
            super(itemView);
            followingName = (TextView) itemView.findViewById(R.id.followingName);
            followingImage = (CircleImageView) itemView.findViewById(R.id.followingImage);
        }
    }

    public SSFollowingAdapter(Context context, Activity activity, List<FollowerAndFollowing> following) {
        this.context = context;
        this.activity = activity;
        this.following = following;
    }

    @NonNull
    @Override
    public followingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.following_row, parent, false);
        followingViewHolder holder = new followingViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final followingViewHolder holder, final int position) {
        holder.followingName.setText(following.get(position).getUsername());
        SSFirestoreManager.firestoreHelper.getUserProfile(following.get(position).getUserid(), new SSUserHandler() {
            @Override
            public void completionHandler(Boolean success, User user, String error) {
                if(user.getImage() != null && user.getImage() != "")
                {
                    Glide.with(context).load(user.getImage()).into(holder.followingImage);
                }
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, SSUserProfileView.class);
                intent.putExtra(SSConstants.USERID_KEY,following.get(position).getUserid());
                context.startActivity(intent);
                activity.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
            }
        });
    }

    @Override
    public int getItemCount() {
        return following.size();
    }
}
