package com.sellspot.app.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.bumptech.glide.Glide;
import com.sellspot.app.Activities.UserImage.SSUserImageView;
import com.sellspot.app.Activities.UserProfile.SSUserProfileView;
import com.sellspot.app.Constants.SSConstants;
import com.sellspot.app.Handlers.SSFirebaseHandler;
import com.sellspot.app.Handlers.SSPostsHandler;
import com.sellspot.app.Models.CartPerStore;
import com.sellspot.app.Models.GuestUser;
import com.sellspot.app.Models.Product;
import com.sellspot.app.Networking.SSFirestoreManager;
import com.sellspot.app.R;
import com.sellspot.app.Shared.CurrentUser;
import com.sellspot.app.Shared.SharedVariables;

import java.io.IOException;
import java.util.List;

public class SSNewHomeAdapter extends BaseAdapter {
    private Activity activity;
    private Context context;
    public List<Product> products;
    public Integer lastLayoutItem;

    public SSNewHomeAdapter(Context context, List<Product> products, Activity activity) {
        this.context = context;
        this.products = products;
        this.activity = activity;
        lastLayoutItem = 0;
    }

    class NewHomeViewHolder {
        TextView username;
        TextView homeItemPrice;
        ImageView homeItemImage;
        Button addToCartButton;

        NewHomeViewHolder(View v) {
            username = (TextView) v.findViewById(R.id.homeUsername);
            homeItemPrice = (TextView)v.findViewById(R.id.homeItemPrice);
            addToCartButton = (Button)v.findViewById(R.id.addToCartButton);
            homeItemImage = (ImageView)v.findViewById(R.id.homeItemImage);
        }
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

    public boolean checkIfItemInGuestCart(Product prod)
    {
        List<List<CartPerStore>> cartsArray = GuestUser.sharedGuest.getCartperstoreproducts();
        if(cartsArray != null) {
            for (int i = 0; i < cartsArray.size(); i++) {
                for (int j = 0; j < cartsArray.get(i).size(); j++) {
                    if (cartsArray.get(i).get(j).getProductid().equals(prod.getProductid())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        lastLayoutItem = position;
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_row, parent, false);
        final NewHomeViewHolder viewHolder = new NewHomeViewHolder(v);
        viewHolder.homeItemPrice.setText("Price: Rs " + (products.get(position).getPrice()));
        viewHolder.username.setText(products.get(position).getUsername());

        if (SharedVariables.shared.localProductImages.containsKey(products.get(position).getProductid()) == true) {
            List<Bitmap> images = SharedVariables.shared.localProductImages.get(products.get(position).getProductid());
            viewHolder.homeItemImage.setImageBitmap(images.get(0));
        } else {
            if(products.get(position).getImages().size() > 0) {
                String productImage = products.get(position).getImages().get(0);
                if (!productImage.equals("")) {
                    Glide.with(context).load(productImage).into(viewHolder.homeItemImage);

                }
            }
        }
        if(CurrentUser.shared.getIsGuest(context) == false) {
            SSFirestoreManager.firestoreHelper.checkIfItemInCart(products.get(position), new SSFirebaseHandler() {
                @Override
                public void completionHandler(Boolean success, String error) {
                    if (success) {
                        viewHolder.addToCartButton.setText("Item already in Cart");
                        viewHolder.addToCartButton.setEnabled(false);
                    } else {
                        viewHolder.addToCartButton.setText("Add to Cart");
                        viewHolder.addToCartButton.setEnabled(true);
                    }
                }
            });
        }
        else{
            if(checkIfItemInGuestCart(products.get(position)) == true)
            {
                viewHolder.addToCartButton.setText("Item already in Cart");
                viewHolder.addToCartButton.setEnabled(false);
            }
            else{
                viewHolder.addToCartButton.setText("Add to Cart");
                viewHolder.addToCartButton.setEnabled(true);
            }
        }

        viewHolder.homeItemImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Product product = products.get(position);
                Intent intent = new Intent(context, SSUserImageView.class);
                intent.putExtra("produserid", products.get(position).getUserid());
                intent.putExtra("prodid", products.get(position).getProductid());
                context.startActivity(intent);
                activity.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
            }
        });

        viewHolder.addToCartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Product product = products.get(position);
                CartPerStore cps;
                if(product.getImages().size() > 0) {
                    cps = new CartPerStore(product.getUserid(), product.getProductid(), product.getPrice(), product.getSize(), product.getImages().get(0));
                }
                else{
                    cps = new CartPerStore(product.getUserid(), product.getProductid(), product.getPrice(), product.getSize(), "");
                }
                if (CurrentUser.shared.getIsGuest(context) == false) {
                    SSFirestoreManager.firestoreHelper.addProductToCart(cps, new SSFirebaseHandler() {
                        @Override
                        public void completionHandler(Boolean success, String error) {
                            if (success) {
                                SharedVariables.shared.increaseCartCount.completionHandler(true, "");
                                viewHolder.addToCartButton.setText("Item added to Cart");
                                viewHolder.addToCartButton.setEnabled(false);
                            }
                        }
                    });
                }
                else{
                    try {
                        GuestUser.sharedGuest.saveToFile(cps,context);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    viewHolder.addToCartButton.setText("Item added to Cart");
                    viewHolder.addToCartButton.setEnabled(false);
                    SharedVariables.shared.increaseCartCount.completionHandler(true, "");

                }
            }
        });

        viewHolder.username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SSUserProfileView.class);
                intent.putExtra(SSConstants.USERID_KEY,((Product) products.get(position)).getUserid());
                context.startActivity(intent);
            }
        });

        if(position == products.size()-1) {
            SSFirestoreManager.firestoreHelper.getHomeScreenProducts(new SSPostsHandler() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void completionHandler(Boolean success, List<Product> productList, String error) {
                    if (success) {
                        if(CurrentUser.shared.getIsGuest(context) == false) {
                            for (int i = 0; i < productList.size(); i++) {
                                if (!productList.get(i).getUserid().equals(CurrentUser.shared.getCurrentUser().getUserid())) {
                                    products.add(productList.get(i));
                                }
                            }
                        }
                        else {
                                products.addAll(productList);
                        }
                        if(productList.size()== 0) {
                        } else {
                            notifyDataSetChanged();
                        }
                    } else {
                        Toast.makeText(context, error, Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
        return v;
    }
}
