package com.sellspot.app.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.util.DisplayMetrics;
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
import com.sellspot.app.Activities.UserImage.SSUserImageView;
import com.sellspot.app.Constants.SSFirestoreConstants;
import com.sellspot.app.Models.Product;
import com.sellspot.app.R;
import com.sellspot.app.Shared.SharedVariables;

import java.util.List;

public class SSProfileAdapter extends BaseAdapter {
    private Activity activity;
    private Context context;
    private List<Product> products;

    public SSProfileAdapter(Activity activity, Context context, List<Product> products) {
        this.activity = activity;
        this.context = context;
        this.products = products;
    }

    @Override
    public int getCount() {
        return products.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.profile_item, null); // inflate the layout

        ProfileViewHolder profileViewHolder = new ProfileViewHolder();
        profileViewHolder.icon = (ImageView) view.findViewById(R.id.icon);
        profileViewHolder.priceLabel = (TextView) view.findViewById(R.id.priceLabel);
        profileViewHolder.mainLayout = (LinearLayout) view.findViewById(R.id.mainLayout);
        profileViewHolder.statusLabel = (TextView) view.findViewById(R.id.profileStatus);

        profileViewHolder.setItemHeight(context);

        if (SharedVariables.shared.localProductImages.containsKey(products.get(i).getProductid()) == true) {
            List<Bitmap> images = SharedVariables.shared.localProductImages.get(products.get(i).getProductid());
            profileViewHolder.icon.setImageBitmap(images.get(0));
        } else {
            if(products.get(i).getImages().size() > 0) {
                Glide.with(context).load(products.get(i).getImages().get(0)).into(profileViewHolder.icon);
            }

        }

        profileViewHolder.icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SSUserImageView.class);
                intent.putExtra("produserid", products.get(i).getUserid());
                intent.putExtra("prodid", products.get(i).getProductid());
                context.startActivity(intent);
                activity.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
            }
        });

        profileViewHolder.priceLabel.setText("Rs " + products.get(i).getPrice());

        String status = products.get(i).getStatus();
        if (status.equals(SSFirestoreConstants.forsale)) {
            profileViewHolder.statusLabel.setVisibility(View.INVISIBLE);
        } else {
            switch (status) {
                case SSFirestoreConstants.placed:
                    profileViewHolder.statusLabel.setText("Order Placed");
                    break;
                case SSFirestoreConstants.inprocess:
                    profileViewHolder.statusLabel.setText("Order In Process");
                    break;
                case SSFirestoreConstants.dispatched:
                    profileViewHolder.statusLabel.setText("Order Dispatched");
                    break;
                case SSFirestoreConstants.soldout:
                case SSFirestoreConstants.delivered:
                    profileViewHolder.statusLabel.setText("Delivered");
                    break;
                default:
                    profileViewHolder.statusLabel.setText("Unavailable");
                    break;
            }
            int color = R.color.productStatusColor;
            profileViewHolder.icon.setForeground(new ColorDrawable(ContextCompat.getColor(context, color)));
            profileViewHolder.statusLabel.setVisibility(View.VISIBLE);
        }

        return view;
    }
}

class ProfileViewHolder {
    public ImageView icon;
    public TextView priceLabel;
    public LinearLayout mainLayout;
    public TextView statusLabel;

    void setItemHeight(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int width = displayMetrics.widthPixels - 40;

        mainLayout.getLayoutParams().width = width / 2;
        mainLayout.getLayoutParams().height = width / 2;

        icon.getLayoutParams().height = (int) ((width / 2) * 0.9);

        mainLayout.requestLayout();
        icon.requestLayout();
    }
}
