package com.sellspot.app.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sellspot.app.Activities.AboutUs.SSAboutView;
import com.sellspot.app.Activities.BuyerSellerInfo.SSBuyerSellerInfoVC;
import com.sellspot.app.R;
import com.sellspot.app.Shared.CurrentUser;

import java.util.ArrayList;
import java.util.List;

public class SSAboutUsInfoAdapter extends RecyclerView.Adapter<SSAboutUsInfoAdapter.aboutUsViewHolder>  {

    private Context context;
    private Activity activity;
    private List<String> data;
    private List<Integer> images;

    public class aboutUsViewHolder extends RecyclerView.ViewHolder {
        TextView iconLabel;
        ImageView iconImageView;
        public aboutUsViewHolder(View itemView) {
            super(itemView);
            iconLabel = (TextView) itemView.findViewById(R.id.iconLabel);
            iconImageView = (ImageView) itemView.findViewById(R.id.iconImageView);
        }
    }

    public SSAboutUsInfoAdapter(Context context, Activity activity) {
        data = new ArrayList<>();
        data.add("About Us");
        data.add("Contact Us");
        data.add("Sell Products through Sell Spot");
        data.add("Buy Products through Sell Spot");

        images = new ArrayList<>();
        images.add(R.mipmap.infoicon);
        images.add(R.mipmap.contactus);
        images.add(R.mipmap.seller);
        images.add(R.mipmap.buyer);

        this.context =  context;
        this.activity = activity;
    }


    @NonNull
    @Override
    public aboutUsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.about_us_info_row, parent, false);
        aboutUsViewHolder holder = new aboutUsViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final aboutUsViewHolder holder, final int position) {
        holder.iconLabel.setText(data.get(position));
        holder.iconImageView.setImageResource(images.get(position));
        holder.iconLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (position) {
                    case 0:
                        Intent i = new Intent(context, SSAboutView.class);
                        context.startActivity(i);
                        break;
                    case 1:
                        Intent intent=new Intent(Intent.ACTION_SEND);
                        String[] recipients={"sellspotofficial@gmail.com"};
                        intent.putExtra(Intent.EXTRA_EMAIL, recipients);
                        if (CurrentUser.shared.getCurrentUser() != null) {
                            intent.putExtra(Intent.EXTRA_SUBJECT, "Contact Us - " + CurrentUser.shared.getCurrentUser().getUsername());
                        } else {
                            intent.putExtra(Intent.EXTRA_SUBJECT, "Contact Us");
                        }
                        intent.setType("text/html");
                        intent.setPackage("com.google.android.gm");
                        context.startActivity(Intent.createChooser(intent, "Send mail"));
                        break;
                    case 2:
                        Intent i1 = new Intent(context, SSBuyerSellerInfoVC.class);
                        i1.putExtra("usertype", 2);
                        context.startActivity(i1);
                        break;
                    case 3:
                        Intent i2 = new Intent(context, SSBuyerSellerInfoVC.class);
                        i2.putExtra("usertype", 1);
                        context.startActivity(i2);
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
