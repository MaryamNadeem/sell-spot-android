package com.sellspot.app.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sellspot.app.R;

import java.util.ArrayList;
import java.util.List;

public class BuyerSellerInfoAdapter  extends RecyclerView.Adapter<BuyerSellerInfoAdapter.buyerSellerViewHolder>  {
    private Context context;
    private Activity activity;
    private List<String> data;

    public class buyerSellerViewHolder extends RecyclerView.ViewHolder {
        TextView stepLabel;
        TextView infoLabel;
        public buyerSellerViewHolder(View itemView) {
            super(itemView);
            stepLabel = (TextView) itemView.findViewById(R.id.stepLabel);
            infoLabel = (TextView) itemView.findViewById(R.id.infoLabel);
        }
    }

    public BuyerSellerInfoAdapter(Context context, Activity activity, Integer type) {
        data = new ArrayList<>();

        if (type == 1) {
            data.add("Browse through the products on your Home and select your style.");
            data.add("Simply Add to cart and complete the checkout process from the Cart.");
            data.add("Receive your product at your doorstep.");
            data.add("Once an order is placed on Sell Spot the buyer will be contacted to confirm the order.\n" +
                    "Once we receive the order at our headquarters we will ship the order to the buyer.\n" +
                    "Upon receiving the order the buyer will hand the cash payment to the rider.\n" +
                    "If the buyer wants to make an online payment we will only ship the order after receiving the payment.\n" +
                    "The buyer will also pay the delivery charges in both cases.");
        } else {
            data.add("Take or upload a photo of your product.");
            data.add("Fill all the required information and add it.");
            data.add("Once an order is placed the seller will be contacted by a representative of Sell Spot, on this call the seller will confirm with Sell Spot whether they want the order picked up from their place or would like to ship us the order themselves.\n" +
                    "In both cases the seller will pay the shipment fee.\n" +
                    "We will provide all the necessary information to the seller for a successful delivery to our headquarters.\n" +
                    "The seller will receive the payment (with 10% commission cut) once the order is successfully received by the buyer.\n" +
                    "The seller can choose his/her desired method of receiving payments from the following options.\n" +
                    "- Online bank transfer\n" +
                    "- Jazz cash\n" +
                    "- Easy Paisa");
        }

        this.context =  context;
        this.activity = activity;
    }


    @NonNull
    @Override
    public buyerSellerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.buyer_seller_row, parent, false);
        buyerSellerViewHolder holder = new buyerSellerViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final buyerSellerViewHolder holder, final int position) {
        if(position != data.size()-1) {
            holder.stepLabel.setText("Step " + (position + 1));
            holder.stepLabel.setVisibility(View.VISIBLE);
        } else {
            holder.stepLabel.setVisibility(View.GONE);
        }
        holder.infoLabel.setText(data.get(position));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}
