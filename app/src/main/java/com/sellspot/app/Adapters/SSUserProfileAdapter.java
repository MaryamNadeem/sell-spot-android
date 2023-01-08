package com.sellspot.app.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.sellspot.app.Constants.SSFirestoreConstants;
import com.sellspot.app.Models.Product;
import com.sellspot.app.R;
import com.sellspot.app.Activities.UserImage.SSUserImageView;
import com.sellspot.app.Shared.SharedVariables;

import java.util.List;

public class SSUserProfileAdapter extends BaseAdapter {
    private Activity activity;
    private Context context;
    private List<Product> products;

    public SSUserProfileAdapter(Activity activity, Context context, List<Product> products) {
        this.activity = activity;
        this.context = context;
        this.products = products;
    }

    @Override
    public int getCount() {
        return products.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_profile_item, null); // inflate the layout

        ProfileViewHolder profileViewHolder = new ProfileViewHolder();
        profileViewHolder.icon = (ImageView) convertView.findViewById(R.id.userProfileIcon);
        profileViewHolder.priceLabel = (TextView) convertView.findViewById(R.id.priceLabel);
        profileViewHolder.mainLayout = (LinearLayout) convertView.findViewById(R.id.mainLayout);
        profileViewHolder.statusLabel = (TextView) convertView.findViewById(R.id.userProfileStatus);

        profileViewHolder.setItemHeight(context);

        if (SharedVariables.shared.localProductImages.containsKey(products.get(position).getProductid()) == true) {
            List<Bitmap> images = SharedVariables.shared.localProductImages.get(products.get(position).getProductid());
            profileViewHolder.icon.setImageBitmap(images.get(0));
        } else {
            Glide.with(context).load(products.get(position).getImages().get(0)).into(profileViewHolder.icon);
        }

        profileViewHolder.icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SSUserImageView.class);
                intent.putExtra("produserid", products.get(position).getUserid());
                intent.putExtra("prodid", products.get(position).getProductid());
                context.startActivity(intent);
                activity.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
            }
        });

        profileViewHolder.priceLabel.setText("Rs " + products.get(position).getPrice());

        String status = products.get(position).getStatus();
        if (status.equals(SSFirestoreConstants.forsale)) {
            profileViewHolder.statusLabel.setVisibility(View.INVISIBLE);
        } else {
            switch (status) {
                case SSFirestoreConstants.soldout:
                case SSFirestoreConstants.delivered:
                    profileViewHolder.statusLabel.setText("Sold Out");
                    break;
                default:
                    profileViewHolder.statusLabel.setText("Unavailable");
                    break;
            }
            int color = R.color.productStatusColor;
            profileViewHolder.icon.setForeground(new ColorDrawable(ContextCompat.getColor(context, color)));
            profileViewHolder.statusLabel.setVisibility(View.VISIBLE);
        }

        return convertView;
    }
}
