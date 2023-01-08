package com.sellspot.app.Adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ablanco.zoomy.Zoomy;
import com.bumptech.glide.Glide;
import com.sellspot.app.Models.Product;
import com.sellspot.app.R;
import com.sellspot.app.Shared.SharedVariables;

import java.util.List;

public class ImagePagerAdapter extends RecyclerView.Adapter<ImagePagerAdapter.ViewHolder> {

    public List<Bitmap> images;
    private Context context;
    private Activity activity;

    public Product product;

    public List<String> imagesurls;

    int check = 0;

    public Boolean isAvailable = false;

    public ImagePagerAdapter(List<Bitmap> images, Context context, Activity activity) {
        this.images = images;
        this.context = context;
        this.activity = activity;
        this.isAvailable = false;
        check = 0;
    }

    public ImagePagerAdapter(List<Bitmap> images, Context context, Activity activity, Boolean isAvailable) {
        this.images = images;
        this.context = context;
        this.activity = activity;
        this.isAvailable = isAvailable;
        check = 1;
    }

    public ImagePagerAdapter(Context context, List<String> imagesurls, Activity activity) {
        this.context = context;
        this.imagesurls = imagesurls;
        this.activity = activity;
        this.isAvailable = false;
        check = 1;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pager_image,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (check == 0) {
            holder.pagerItemImage.setImageBitmap(images.get(position));
        } else {
            if (isAvailable) {
                holder.pagerItemImage.setImageBitmap(images.get(position));
            } else {
                Glide.with(context).load(imagesurls.get(position)).into(holder.pagerItemImage);
            }
        }
        Zoomy.Builder builder = new Zoomy.Builder(activity).target(holder.pagerItemImage);
        builder.register();
    }

    @Override
    public int getItemCount() {
        if (check == 0) {
            return images.size();
        } else {
            if (isAvailable == true) {
                return images.size();
            } else {
                return imagesurls.size();
            }
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView pagerItemImage;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            pagerItemImage = (ImageView) itemView.findViewById(R.id.pagerItemImage);
            DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
            int width = displayMetrics.widthPixels;
            pagerItemImage.setMinimumHeight(width);
        }
    }
}
