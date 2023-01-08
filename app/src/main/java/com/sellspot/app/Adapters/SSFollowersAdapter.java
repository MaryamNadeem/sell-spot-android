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

public class SSFollowersAdapter extends RecyclerView.Adapter<SSFollowersAdapter.followerViewHolder> {
    private Context context;
    private Activity activity;
    private List<FollowerAndFollowing> followers;

    public class followerViewHolder extends RecyclerView.ViewHolder {
        TextView followerName;
        CircleImageView followerImage;

        public followerViewHolder(View itemView) {
            super(itemView);
            followerName = (TextView) itemView.findViewById(R.id.followerName);
            followerImage = (CircleImageView) itemView.findViewById(R.id.followerImage);
        }
    }

    public SSFollowersAdapter(Context context, Activity activity, List<FollowerAndFollowing> followers) {
        this.context = context;
        this.activity = activity;
        this.followers = followers;
    }


    @NonNull
    @Override
    public followerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.followers_row, parent, false);
        followerViewHolder holder = new followerViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final followerViewHolder holder,final int position) {
        holder.followerName.setText(followers.get(position).getUsername());
        SSFirestoreManager.firestoreHelper.getUserProfile(followers.get(position).getUserid(), new SSUserHandler() {
            @Override
            public void completionHandler(Boolean success, User user, String error) {
                if(user.getImage() != null && user.getImage() != "")
                {
                    Glide.with(context).load(user.getImage()).into(holder.followerImage);
                }
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, SSUserProfileView.class);
                intent.putExtra(SSConstants.USERID_KEY,followers.get(position).getUserid());
                context.startActivity(intent);
                activity.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
            }
        });
    }

    @Override
    public int getItemCount() {
        return followers.size();
    }
}
