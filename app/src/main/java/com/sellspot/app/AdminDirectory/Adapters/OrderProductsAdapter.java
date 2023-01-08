package com.sellspot.app.AdminDirectory.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.sellspot.app.Handlers.SSPostHandler;
import com.sellspot.app.Models.Order;
import com.sellspot.app.Models.Product;
import com.sellspot.app.Networking.SSFirestoreManager;
import com.sellspot.app.R;

public class OrderProductsAdapter extends RecyclerView.Adapter<OrderProductsAdapter.OrdersProductsViewHolder> {
    private Context context;
    private Activity activity;
    private Order data;

    public OrderProductsAdapter(Context context, Activity activity, Order data) {
        this.context = context;
        this.activity = activity;
        this.data = data;
    }

    @NonNull
    @Override
    public OrdersProductsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_products_row, parent, false);
        OrdersProductsViewHolder holder = new OrdersProductsViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final OrdersProductsViewHolder holder, int position) {
        holder.orderProductNumber.setText(Integer.toString(position+1));
        holder.orderProductId.setText(data.getProductId().get(position));
        SSFirestoreManager.firestoreHelper.getProductById(data.getPlacedFrom(), data.getProductId().get(position), new SSPostHandler() {
            @Override
            public void completionHandler(Boolean success, Product products, String error) {
                if(success) {
                    holder.orderProductSize.setText(products.getSize());
                    holder.orderProductPrice.setText("RS "+products.getPrice());
                    holder.orderProductMaterial.setText(products.getMaterial());
                    holder.orderProductDescription.setText(products.getDescription());
                    Glide.with(context).load(products.getImages().get(0)).into(holder.orderProductImage);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.getProductId().size();
    }

    public class OrdersProductsViewHolder extends RecyclerView.ViewHolder {
        ImageView orderProductImage;
        TextView orderProductPrice;
        TextView orderProductSize;
        TextView orderProductMaterial;
        TextView orderProductDescription;
        TextView orderProductNumber;
        TextView orderProductId;
        public OrdersProductsViewHolder(View itemView) {
            super(itemView);
            orderProductId = (TextView)itemView.findViewById(R.id.orderProductId);
            orderProductImage = (ImageView)itemView.findViewById(R.id.orderProductImage);
            orderProductPrice = (TextView)itemView.findViewById(R.id.orderProductPrice);
            orderProductSize = (TextView)itemView.findViewById(R.id.orderProductSize);
            orderProductMaterial = (TextView)itemView.findViewById(R.id.orderProductMaterial);
            orderProductDescription = (TextView)itemView.findViewById(R.id.orderProductDescription);
            orderProductNumber = (TextView)itemView.findViewById(R.id.orderProductNumber);
        }
    }
}
