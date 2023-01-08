package com.sellspot.app.AdminDirectory.Adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.ablanco.zoomy.Zoomy;
import com.bumptech.glide.Glide;
import com.sellspot.app.Activities.UserImage.SSUserImageView;
import com.sellspot.app.AdminDirectory.Activities.AdminViewProduct;
import com.sellspot.app.Constants.SSFirestoreConstants;
import com.sellspot.app.Models.Product;
import com.sellspot.app.Networking.SSFirebaseManager;
import com.sellspot.app.R;
import com.sellspot.app.Shared.HomeLastProduct;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


import java.util.List;

public class AdminReviewProductsAdapter extends BaseAdapter {
    private Activity activity;
    private Context context;
    public List<Product> products;
    public Integer lastLayoutItem;

    public AdminReviewProductsAdapter(Context context, List<Product> products, Activity activity) {
        this.context = context;
        this.products = products;
        this.activity = activity;
        lastLayoutItem = 0;
    }

    class ReviewViewHolder {
        TextView username;
        TextView homeItemPrice;
        ImageView homeItemImage;
        Button deleteProductButton;

        ReviewViewHolder(View v) {
            username = (TextView) v.findViewById(R.id.reviewUsername);
            homeItemPrice = (TextView)v.findViewById(R.id.reviewItemPrice);
            deleteProductButton = (Button)v.findViewById(R.id.deleteproductButton);
            homeItemImage = (ImageView)v.findViewById(R.id.reviewItemImage);
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

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        lastLayoutItem = position;
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_review_row, parent, false);
        final ReviewViewHolder viewHolder = new ReviewViewHolder(v);
        viewHolder.homeItemPrice.setText("RS " + products.get(position).getPrice() + "\nStatus: " + products.get(position).getStatus());
        viewHolder.username.setText(products.get(position).getUsername());
        if(products.get(position).getImages().size()>0) {
            String productImage = products.get(position).getImages().get(0);
            if (!productImage.equals("")) {
                Glide.with(context).load(productImage).into(viewHolder.homeItemImage);
            }
        }

        viewHolder.homeItemImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("AdminReviewProductsAdap","Clicked on item");
                Product product = products.get(position);
                Intent intent = new Intent(context, AdminViewProduct.class);
                intent.putExtra("produserid", product.getUserid());
                intent.putExtra("prodid", product.getProductid());
                context.startActivity(intent);
                activity.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
            }
        });

        viewHolder.deleteProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(context)
                        .setTitle("Are you sure you want to delete the product?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                String doc= SSFirestoreConstants.products+"/"+products.get(position).getUserid()+"/"
                                        +SSFirestoreConstants.userproducts+"/"+products.get(position).getProductid();
                                DocumentReference docRef = SSFirebaseManager.firestoreDatabase.document(doc);
                                docRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Query collectionRef = SSFirebaseManager.firestoreDatabase.collectionGroup(SSFirestoreConstants.product_ids)
                                                .whereEqualTo(SSFirestoreConstants.productid, products.get(position).getProductid());
                                        collectionRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                            @RequiresApi(api = Build.VERSION_CODES.N)
                                            @Override
                                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                if(queryDocumentSnapshots.size() > 0) {
                                                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                                                        doc.getReference().delete();
                                                    }
                                                }
                                                products.remove(position);
                                                notifyDataSetChanged();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {

                                            }
                                        });
                                    }
                                });
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        }).show();
            }
        });

        if(position == products.size()-1) {
            Query collectionRef;
            collectionRef = SSFirebaseManager.firestoreDatabase.collectionGroup(SSFirestoreConstants.userproducts)
                    .orderBy(SSFirestoreConstants.time, Query.Direction.DESCENDING)
                    .startAfter(HomeLastProduct.shared.getAdminHomeLastProduct()).limit(16);

            collectionRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    if (queryDocumentSnapshots.size() > 0) {
                        HomeLastProduct.shared.setAdminHomeLastProduct(queryDocumentSnapshots.getDocuments()
                                .get(queryDocumentSnapshots.size() - 1));
                    }
                    List<Product> productList = queryDocumentSnapshots.toObjects(Product.class);
                    if(productList.size()== 0) {
                    } else {
                        products.addAll(productList);
                        notifyDataSetChanged();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(context, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }
        return v;
    }
}
