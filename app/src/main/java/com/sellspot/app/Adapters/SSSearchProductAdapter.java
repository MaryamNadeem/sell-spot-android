package com.sellspot.app.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.sellspot.app.Activities.UserImage.SSUserImageView;
import com.sellspot.app.Models.Product;
import com.sellspot.app.R;
import com.sellspot.app.Shared.SharedVariables;

import java.util.List;

public class SSSearchProductAdapter extends BaseAdapter {
    private Activity activity;
    private Context context;
    public List<Product> products;

    public SSSearchProductAdapter(Activity activity, Context context, List<Product> products) {
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

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_item_row, null);

        SearchProductViewHolder searchProductViewHolder = new SearchProductViewHolder();
        searchProductViewHolder.searchItemImage = (ImageView) convertView.findViewById(R.id.searchItemImage);

        searchProductViewHolder.setItemHeight(context);
        if (SharedVariables.shared.localProductImages.containsKey(products.get(position).getProductid()) == true) {
            List<Bitmap> images = SharedVariables.shared.localProductImages.get(products.get(position).getProductid());
            searchProductViewHolder.searchItemImage.setImageBitmap(images.get(0));
        } else {
            if(products.get(position).getImages().size()>0) {
                Glide.with(context).load(products.get(position).getImages().get(0)).into(searchProductViewHolder.searchItemImage);
            }
        }

        searchProductViewHolder.searchItemImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SSUserImageView.class);
                intent.putExtra("produserid", products.get(position).getUserid());
                intent.putExtra("prodid", products.get(position).getProductid());
                context.startActivity(intent);
                activity.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
            }
        });

        return convertView;
    }
}

class SearchProductViewHolder {
    public ImageView searchItemImage;
    void setItemHeight(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int width = displayMetrics.widthPixels;
        searchItemImage.getLayoutParams().height = (int) ((width / 3));
        searchItemImage.requestLayout();
    }
}