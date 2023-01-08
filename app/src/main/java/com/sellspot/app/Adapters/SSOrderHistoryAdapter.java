package com.sellspot.app.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sellspot.app.R;

public class SSOrderHistoryAdapter extends RecyclerView.Adapter<SSOrderHistoryAdapter.orderHistoryViewHolder> {

    private Context context;

    public class orderHistoryViewHolder extends RecyclerView.ViewHolder {
        TextView orderHistoryUsername;
        TextView orderHistoryPrice;
        TextView goToProfile;
        ImageView orderHistoryImage;

        public orderHistoryViewHolder(View itemView) {
            super(itemView);
            orderHistoryUsername = (TextView) itemView.findViewById(R.id.orderHistoryUsername);
            orderHistoryPrice = (TextView) itemView.findViewById(R.id.orderHistoryPrice);
            goToProfile = (TextView) itemView.findViewById(R.id.goToProfile);
            orderHistoryImage = (ImageView) itemView.findViewById(R.id.orderHistoryImage);
        }
    }

    public SSOrderHistoryAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public orderHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_history_row, parent, false);
        orderHistoryViewHolder holder = new orderHistoryViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull orderHistoryViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 10;
    }
}
