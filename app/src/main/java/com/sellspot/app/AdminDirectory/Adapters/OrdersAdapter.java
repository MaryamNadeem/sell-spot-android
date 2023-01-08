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

import com.sellspot.app.AdminDirectory.Activities.OrderInfo;
import com.sellspot.app.Constants.SSFirestoreConstants;
import com.sellspot.app.Handlers.SSUserHandler;
import com.sellspot.app.Models.Order;
import com.sellspot.app.Models.User;
import com.sellspot.app.Networking.SSFirestoreManager;
import com.sellspot.app.R;
import com.sellspot.app.Shared.HomeLastProduct;

import java.util.List;

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.OrdersViewHolder> {
    private Context context;
    private Activity activity;
    private List<Order> data;
    private String type;

    public class OrdersViewHolder extends RecyclerView.ViewHolder {
        TextView orderId;
        TextView placedBy;
        TextView placedFrom;
        LinearLayout mainll;
        public OrdersViewHolder(View itemView) {
            super(itemView);
            orderId = (TextView) itemView.findViewById(R.id.orderId);
            placedBy = (TextView) itemView.findViewById(R.id.placedBy);
            placedFrom = (TextView) itemView.findViewById(R.id.placedFrom);
            mainll = (LinearLayout) itemView.findViewById(R.id.mainll);
        }
    }

    public OrdersAdapter(Context context, Activity activity, List<Order> data, String type) {
        this.context = context;
        this.activity = activity;
        this.data = data;
        this.type =  type;
    }

    @NonNull
    @Override
    public OrdersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.orders_row, parent, false);
        OrdersViewHolder holder = new OrdersViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final OrdersViewHolder holder, final int position) {
        String text = (data.get(position).getOrderId());
        holder.orderId.setText(text);
        SSFirestoreManager.firestoreHelper.getUserProfile(data.get(position).getPlacedBy(), new SSUserHandler() {
            @Override
            public void completionHandler(Boolean success, User user, String error) {
                if (success) {
                    holder.placedBy.setText(user.getUsername());
                }
            }
        });

        SSFirestoreManager.firestoreHelper.getUserProfile(data.get(position).getPlacedFrom(), new SSUserHandler() {
            @Override
            public void completionHandler(Boolean success, User user, String error) {
                if (success) {
                    holder.placedFrom.setText(user.getUsername());
                }
            }
        });
        holder.mainll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, OrderInfo.class);
                HomeLastProduct.shared.selectedOrder = data.get(position);
                switch (type) {
                    case SSFirestoreConstants
                            .placed:
                        intent.putExtra("order type",SSFirestoreConstants.placed);
                        break;
                    case SSFirestoreConstants
                            .inprocess:
                        intent.putExtra("order type",SSFirestoreConstants
                                .inprocess);
                        break;
                    case SSFirestoreConstants
                            .dispatched:
                        intent.putExtra("order type",SSFirestoreConstants
                                .dispatched);
                        break;
                    case SSFirestoreConstants
                            .delivered:
                        intent.putExtra("order type",SSFirestoreConstants
                                .delivered);
                        break;
                    case SSFirestoreConstants.cancelledOrReturned:
                        intent.putExtra("order type",SSFirestoreConstants
                                .cancelledOrReturned);
                        break;
                    default:
                        break;
                }
                context.startActivity(intent);
                activity.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}
