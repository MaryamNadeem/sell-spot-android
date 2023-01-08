package com.sellspot.app.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.sellspot.app.Models.CartPerStore;
import com.sellspot.app.R;
import com.sellspot.app.Shared.SharedVariables;

import java.util.List;

public class SSCheckoutScreenAdapter extends RecyclerView.Adapter {
    Context context;
    Activity activity;
    List<CartPerStore> productsArray;

    public SSCheckoutScreenAdapter(Context context, Activity activity, List<CartPerStore> productsArray) {
        this.context = context;
        this.activity = activity;
        this.productsArray = productsArray;
    }

    class ItemInfoViewHolder extends RecyclerView.ViewHolder {
        TextView price;
        TextView size;
        ImageView infoImage;
        public ItemInfoViewHolder(View itemView){
            super(itemView);
            price = (TextView) itemView.findViewById(R.id.infoPrice);
            size = (TextView) itemView.findViewById(R.id.infoSize);
            infoImage = (ImageView) itemView.findViewById(R.id.infoImage);
        }
    }

    class TotalPriceViewHolder extends RecyclerView.ViewHolder {
        TextView totalPrice;
        Button continueShoppingButton;
        public TotalPriceViewHolder(View itemView) {
            super(itemView);
            totalPrice = (TextView) itemView.findViewById(R.id.infoTotalPrice);
            continueShoppingButton = (Button) itemView.findViewById(R.id.continueShoppingButton);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == 0) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.checkout_info_row, parent, false);
            ItemInfoViewHolder holder = new ItemInfoViewHolder(v);
            return holder;
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.total_price_row, parent, false);
            TotalPriceViewHolder holder = new TotalPriceViewHolder(v);
            return holder;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == 0) {
            CartPerStore product = productsArray.get(position);
            ItemInfoViewHolder viewHolder = (ItemInfoViewHolder) holder;
            viewHolder.price.setText("RS " + product.getPrice());
            viewHolder.size.setText(product.getSize());
            Glide.with(context).load(product.getImage()).into(viewHolder.infoImage);

        } else {
            TotalPriceViewHolder viewHolder = (TotalPriceViewHolder) holder;
            int total = 0;
            for(int i = 0; i <productsArray.size();i++) {
                total += productsArray.get(i).getPrice();
            }
            viewHolder.totalPrice.setText("Rs "+Integer.toString(total));
            viewHolder.continueShoppingButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    activity.finish();
                    SharedVariables.shared.changeTab.completionHandler(true,"");
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return productsArray.size()+1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == productsArray.size()) {
            return 1;
        } else {
            return 0;
        }
    }
}
