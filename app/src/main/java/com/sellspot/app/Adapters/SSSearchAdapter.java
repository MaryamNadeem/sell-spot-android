package com.sellspot.app.Adapters;

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
import com.sellspot.app.Models.User;
import com.sellspot.app.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class  SSSearchAdapter extends RecyclerView.Adapter<SSSearchAdapter.searchUserHolder> {
    private Context context;
    public List<User> objects;

    public class searchUserHolder extends RecyclerView.ViewHolder {
        TextView searchUsername;
        CircleImageView searchUserImage;

        public searchUserHolder(View itemView) {
            super(itemView);
            searchUsername = (TextView) itemView.findViewById(R.id.searchUsername);
            searchUserImage = (CircleImageView) itemView.findViewById(R.id.searchUserImage);
        }
    }

    public SSSearchAdapter(Context context, List<User> objects) {
        this.context = context;
        this.objects = objects;
    }

    @NonNull
    @Override
    public searchUserHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v1 = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_user_row, parent, false);
        searchUserHolder holder1 = new searchUserHolder(v1);
        return holder1;
    }

    @Override
    public void onBindViewHolder(@NonNull searchUserHolder holder, final int position) {
        holder.searchUsername.setText((objects.get(position)).getUsername());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, SSUserProfileView.class);
                intent.putExtra(SSConstants.USERID_KEY,((User) objects.get(position)).getUserid());
                context.startActivity(intent);

            }
        });
        holder.searchUserImage.setImageResource(R.mipmap.shape);
        if((objects.get(position)).getImage() != null && (objects.get(position)).getImage() !="") {
            Glide.with(context).load(objects.get(position).getImage()).into(holder.searchUserImage);
        }
    }

    @Override
    public int getItemCount() {
        return objects.size();
    }
}
