package com.sellspot.app.AdminDirectory.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sellspot.app.AdminDirectory.Activities.UserInfo;
import com.sellspot.app.Models.User;
import com.sellspot.app.R;

import java.util.List;

public class AdminUsersAdapter extends RecyclerView.Adapter<AdminUsersAdapter.AdminUsersViewHolder> {

    private Context context;
    private Activity activity;
    private List<User> data;

    public AdminUsersAdapter(Context context, Activity activity, List<User> data) {
        this.context = context;
        this.activity = activity;
        this.data = data;
    }

    @NonNull
    @Override
    public AdminUsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_users_row, parent, false);
        AdminUsersViewHolder holder = new AdminUsersViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull AdminUsersViewHolder holder, int position) {
        final User u = data.get(position);
        holder.userid.setText(u.getUserid());
        holder.username.setText(u.getUsername());
        holder.userid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToInfo(u.getUserid());
            }
        });

        holder.username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToInfo(u.getUserid());
            }
        });

        holder.mainuserll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToInfo(u.getUserid());
            }
        });
    }

    private void goToInfo(String userid) {
        Intent intent1 = new Intent(context, UserInfo.class);
        intent1.putExtra("useridforinfo",userid);
        context.startActivity(intent1);
        activity.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class AdminUsersViewHolder extends RecyclerView.ViewHolder {
        TextView userid;
        TextView username;
        LinearLayout mainuserll;
        public AdminUsersViewHolder(View itemView) {
            super(itemView);
            userid = (TextView) itemView.findViewById(R.id.userid);
            username = (TextView) itemView.findViewById(R.id.username);
            mainuserll = (LinearLayout) itemView.findViewById(R.id.mainuserll);
        }
    }
}
