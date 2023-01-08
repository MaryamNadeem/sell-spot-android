package com.sellspot.app.AdminDirectory.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sellspot.app.AdminDirectory.Activities.AdminReviewProducts;
import com.sellspot.app.AdminDirectory.Activities.AdminUsers;
import com.sellspot.app.AdminDirectory.Activities.Orders;
import com.sellspot.app.Constants.SSFirestoreConstants;
import com.sellspot.app.R;

import java.util.List;

public class MainAdminViewAdapter extends RecyclerView.Adapter<MainAdminViewAdapter.mainAdminViewHolder>  {

    private Context context;
    private Activity activity;
    private List<String> data;

    public class mainAdminViewHolder extends RecyclerView.ViewHolder {
        TextView categoryName;
        public mainAdminViewHolder(View itemView) {
            super(itemView);
            categoryName = (TextView) itemView.findViewById(R.id.categoryName);
        }
    }

    public MainAdminViewAdapter(Context context, Activity activity, List<String> data) {
        this.context = context;
        this.activity = activity;
        this.data = data;
    }


    @NonNull
    @Override
    public mainAdminViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_admin_row, parent, false);
        mainAdminViewHolder holder = new mainAdminViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final mainAdminViewHolder holder, final int position) {
        holder.categoryName.setText(data.get(position));
        holder.categoryName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, Orders.class);
                switch (position) {
                    case 0:
                        intent.putExtra("order type", SSFirestoreConstants.placed);
                        context.startActivity(intent);
                        activity.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
                        break;
                    case 1:
                        intent.putExtra("order type",SSFirestoreConstants.inprocess);
                        context.startActivity(intent);
                        activity.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
                        break;
                    case 2:
                        intent.putExtra("order type",SSFirestoreConstants.dispatched);
                        context.startActivity(intent);
                        activity.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
                        break;
                    case 3:
                        intent.putExtra("order type",SSFirestoreConstants.delivered);
                        context.startActivity(intent);
                        activity.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
                        break;
                    case 4:
                        intent.putExtra("order type",SSFirestoreConstants.cancelledOrReturned);
                        context.startActivity(intent);
                        activity.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
                        break;
                    case 5:
                        Intent intent1 = new Intent(context, AdminUsers.class);
                        context.startActivity(intent1);
                        activity.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
                        break;
                    case 6:
                        Intent intent2 = new Intent(context, AdminReviewProducts.class);
                        context.startActivity(intent2);
                        activity.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
                        break;
                    default:
                        break;
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}
