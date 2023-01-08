package com.sellspot.app.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.sellspot.app.Handlers.SSCompletionHandler;
import com.sellspot.app.Handlers.SSFirebaseHandler;
import com.sellspot.app.Handlers.SSPostHandler;
import com.sellspot.app.Models.GuestUser;
import com.sellspot.app.Models.Product;
import com.sellspot.app.Networking.SSFirestoreManager;

import com.sellspot.app.Activities.UserImage.SSUserImageView;
import com.sellspot.app.Models.CartPerStore;
import com.sellspot.app.R;
import com.sellspot.app.Shared.CurrentUser;
import com.sellspot.app.Shared.SharedVariables;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SSCartItemAdapter extends RecyclerView.Adapter<SSCartItemAdapter.cartItemViewHolder> {
    private Context context;
    private SSCompletionHandler completionHandler;
    private SSCompletionHandler removeProductHandler;
    private List<CartPerStore> cps;

    public SSCartItemAdapter(Context context, List<CartPerStore> cps, SSCompletionHandler completionHandler, SSCompletionHandler removeProductHandler) {
        this.context = context;
        this.cps = cps;
        this.completionHandler = completionHandler;
        this.removeProductHandler = removeProductHandler;
    }

    public class cartItemViewHolder extends RecyclerView.ViewHolder {
        ImageView itemImage;
        TextView itemPrice;
        ImageView itemDelete;


        public cartItemViewHolder(View itemView) {
            super(itemView);
            itemImage = (ImageView) itemView.findViewById(R.id.cartItemItemImage);
            itemPrice = (TextView) itemView.findViewById(R.id.itemPrice);
            itemDelete = (ImageView) itemView.findViewById(R.id.itemDelete);
        }
    }

    @NonNull
    @Override
    public cartItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_row_recyclerview_item, parent, false);
        cartItemViewHolder holder = new cartItemViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull cartItemViewHolder holder, final int position) {
        if (SharedVariables.shared.localProductImages.containsKey(cps.get(position).getProductid()) == true) {
            List<Bitmap> images = SharedVariables.shared.localProductImages.get(cps.get(position).getProductid());
            holder.itemImage.setImageBitmap(images.get(0));
        } else {
            Glide.with(context).load(cps.get(position).getImage()).into(holder.itemImage);

        }
        holder.itemPrice.setText("Rs " + Integer.toString(cps.get(position).getPrice()));
        holder.itemImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                SSFirestoreManager.firestoreHelper.getProductById(cps.get(position).getUserid(), cps.get(position).getProductid(), new SSPostHandler() {
                    @Override
                    public void completionHandler(Boolean success, Product product, String error) {
                        if(success)
                        {
                            Intent intent = new Intent(context, SSUserImageView.class);
                            intent.putExtra("produserid", cps.get(position).getUserid());
                            intent.putExtra("prodid", cps.get(position).getProductid());
                            context.startActivity(intent);
                        }
                        else{
                            Toast.makeText(context, "Unable to open product page", Toast.LENGTH_LONG).show();
                        }
                    }
                });

            }
       });
        holder.itemDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(CurrentUser.shared.getIsGuest(context) == false)
                {
                List<String> products = new ArrayList<String>();
                products.add(cps.get(position).getProductid());
                SSFirestoreManager.firestoreHelper.crossCartProductsObject(cps.get(position).getUserid(), products, new SSFirebaseHandler() {
                    @Override
                    public void completionHandler(Boolean success, String error) {
                        if (success) {
                            cps.remove(position);
                            removeProductHandler.completionHandler(true, "");
                            notifyDataSetChanged();
                            if (cps.size() == 0) {
                                completionHandler.completionHandler(true, "");
                            }
                            SharedVariables.shared.decreaseCartCount.completionHandler(true, "");
                        } else {
                        }
                    }
                });
            }
            else{
                boolean flag = false;
                List<List<CartPerStore>> cartsArray = GuestUser.sharedGuest.getCartperstoreproducts();
                if(cartsArray != null) {
                    for (int i = 0; i < cartsArray.size(); i++) {
                        for (int j = 0; j < cartsArray.get(i).size(); j++) {
                            if(flag == false) {
                                if (cartsArray.get(i).get(j).getProductid().equals(cps.get(position).getProductid())) {
                                    cartsArray.get(i).remove(j);
                                    removeProductHandler.completionHandler(true, "");
                                    flag = true;
                                    notifyDataSetChanged();
                                    break;
                                }
                            }
                        }
                    }
                    if (cps.size() == 0) {
                        completionHandler.completionHandler(true, "");
                    }
                    else{
                        try {
                            GuestUser.sharedGuest.setCartperstoreproducts(cartsArray, context);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    SharedVariables.shared.decreaseCartCount.completionHandler(true, "");

                }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return cps.size();
    }
}

// for future reference

//    Hex Opacity Values
//
//        100% — FF
//        95% — F2
//        90% — E6
//        85% — D9
//        80% — CC
//        75% — BF
//        70% — B3
//        65% — A6
//        60% — 99
//        55% — 8C
//        50% — 80
//        45% — 73
//        40% — 66
//        35% — 59
//        30% — 4D
//        25% — 40
//        20% — 33
//        15% — 26
//        10% — 1A
//        5% — 0D
//        0% — 00