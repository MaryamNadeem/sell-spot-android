package com.sellspot.app.Adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.sellspot.app.Activities.CheckoutScreen.SSCheckoutScreenView;
import com.sellspot.app.Activities.Login.SSLoginView;
import com.sellspot.app.Activities.Signup.SSSignupView;
import com.sellspot.app.Activities.UserProfile.SSUserProfileView;
import com.sellspot.app.Constants.SSConstants;
import com.sellspot.app.Constants.SSFirestoreConstants;
import com.sellspot.app.Handlers.SSCartProductHandler;
import com.sellspot.app.Handlers.SSCompletionHandler;
import com.sellspot.app.Handlers.SSFirebaseHandler;
import com.sellspot.app.Handlers.SSPostsHandler;
import com.sellspot.app.Handlers.SSUserHandler;
import com.sellspot.app.Models.CartPerStore;
import com.sellspot.app.Models.GuestUser;
import com.sellspot.app.Models.Order;
import com.sellspot.app.Models.Product;
import com.sellspot.app.Models.User;
import com.sellspot.app.Networking.SSFirestoreManager;
import com.sellspot.app.R;
import com.sellspot.app.Shared.CurrentUser;
import com.sellspot.app.Shared.SharedFunctions;
import com.sellspot.app.Shared.SharedVariables;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class SSCartAdapter extends RecyclerView.Adapter<SSCartAdapter.cartViewHolder> {

    private Context context;
    private Activity activity;
    private List<List<CartPerStore>> cartsArr;

    public class cartViewHolder extends RecyclerView.ViewHolder {
        CircleImageView userImage;
        TextView username;
        TextView totalPrice;
        TextView totalItems;
        RecyclerView cartItemRecyclerView;
        Button checkOutButton;
        Button addMoreButton;

        public cartViewHolder(View itemView) {
            super(itemView);
            userImage = (CircleImageView) itemView.findViewById(R.id.cartUserImage);
            username = (TextView) itemView.findViewById(R.id.cartUserName);
            totalPrice = (TextView) itemView.findViewById(R.id.cartTotalPrice);
            totalItems = (TextView) itemView.findViewById(R.id.cartTotalItems);
            cartItemRecyclerView = (RecyclerView) itemView.findViewById(R.id.cartItemRecyclerView);
            checkOutButton = (Button) itemView.findViewById(R.id.chectoutButton);
            addMoreButton = (Button) itemView.findViewById(R.id.addMoreButton);
        }
    }

    public SSCartAdapter(Context context, Activity activity,List<List<CartPerStore>> cartsArr) {
        this.context = context;
        this.cartsArr = cartsArr;
        this.activity = activity;
    }

    @NonNull
    @Override
    public cartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_row, parent, false);
        cartViewHolder holder = new cartViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final cartViewHolder holder, final int position) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL,false);
        holder.cartItemRecyclerView.setLayoutManager(linearLayoutManager);
        holder.cartItemRecyclerView.setItemAnimator(new DefaultItemAnimator());
        holder.cartItemRecyclerView.setHasFixedSize(true);
        holder.cartItemRecyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        holder.cartItemRecyclerView.setAdapter(new SSCartItemAdapter(context, cartsArr.get(position), new SSCompletionHandler() {
            @Override
            public void completionHandler(Boolean success, String error) {
                if (success) {
                    cartsArr.remove(position);
                    if (CurrentUser.shared.getIsGuest(context) == true) {
                        try {
                            GuestUser.sharedGuest.setCartperstoreproducts(cartsArr, context);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    notifyDataSetChanged();
                }
            }
        }, new SSCompletionHandler() {
            @Override
            public void completionHandler(Boolean success, String error) {
                if(success)
                {
                    if(cartsArr.get(position).size()>1) {
                        holder.totalItems.setText(Integer.toString(cartsArr.get(position).size()) + " items");
                    }
                    else{
                        holder.totalItems.setText(Integer.toString(cartsArr.get(position).size()) + " item");
                    }
                }
            }
        }));
        SSFirestoreManager.firestoreHelper.getUserProfile(cartsArr.get(position).get(0).getUserid(), new SSUserHandler() {
            @Override
            public void completionHandler(Boolean success, User user, String error) {
                holder.username.setText(user.getUsername());
                if (!user.getImage().equals("")) {
                    Glide.with(context).load(user.getImage()).into(holder.userImage);

                }
            }
        });
        if(cartsArr.get(position).size()>1) {
            holder.totalItems.setText(Integer.toString(cartsArr.get(position).size()) + " items");
        }
        else{
            holder.totalItems.setText(Integer.toString(cartsArr.get(position).size()) + " item");
        }
        int total = 0;
        for(int i = 0; i <cartsArr.get(position).size();i++) {
            total += cartsArr.get(position).get(i).getPrice();
        }
        holder.totalPrice.setText("Rs "+Integer.toString(total));

        holder.addMoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SSUserProfileView.class);
                intent.putExtra(SSConstants.USERID_KEY, cartsArr.get(position).get(0).getUserid());
                context.startActivity(intent);
                activity.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
            }
        });

        holder.checkOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (CurrentUser.shared.getIsGuest(context) == false) {
                    SharedVariables.shared.cartProgressHandler.completionHandler(true, "");
                    List<String> productIds = new ArrayList<String>();
                    for (int i = 0; i < cartsArr.get(position).size(); i++) {
                        productIds.add(cartsArr.get(position).get(i).getProductid());
                    }
                    SSFirestoreManager.firestoreHelper.getSoldOutUserProducts(cartsArr.get(position).get(0).getUserid(), productIds, new SSPostsHandler() {
                        @Override
                        public void completionHandler(Boolean success, List<Product> products, String error) {
                            if (success) {
                                List<String> ids = new ArrayList<String>();
                                for (int i = 0; i < products.size(); i++) {
                                    ids.add(products.get(i).getProductid());
                                }
                                if (products.size() > 0) {
                                    Toast.makeText(context, "Some products were sold out and are not available anymore so we have updated your cart.", Toast.LENGTH_LONG).show();
                                    SSFirestoreManager.firestoreHelper.deleteSoldOutProductFromCart(cartsArr.get(position).get(0).getUserid(), ids, new SSFirebaseHandler() {
                                        @Override
                                        public void completionHandler(Boolean success, String error) {
                                            SharedVariables.shared.updateCartCount.completionHandler(true, "");
                                            SSFirestoreManager.firestoreHelper.getCartPerStore(cartsArr.get(position).get(0).getUserid(), new SSCartProductHandler() {
                                                @Override
                                                public void completionHandler(Boolean success, List<CartPerStore> cartperstoreproducts, String error) {
                                                    if (success) {
                                                        SharedVariables.shared.cartProgressHandler.completionHandler(false, "");
                                                        if (cartperstoreproducts.size() > 0) {
                                                            cartsArr.set(position, cartperstoreproducts);
                                                            notifyDataSetChanged();
                                                        } else {
                                                            cartsArr.remove(position);
                                                            notifyDataSetChanged();
                                                        }
                                                    } else {
                                                        SharedVariables.shared.cartProgressHandler.completionHandler(false, "");
                                                    }
                                                }
                                            });
                                        }
                                    });
                                } else {
                                    checkout(position);
                                }
                            }
                        }
                    });
                } else {
                    final Dialog dialog = new Dialog(context);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.upload_image_dialog);
                    Button signupButton = (Button) dialog.findViewById(R.id.signupfromcart);
                    Button loginButton = (Button) dialog.findViewById(R.id.loginfromcart);
                    Button cancelButton = (Button) dialog.findViewById(R.id.cancelCheckout);

                    signupButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(context, SSSignupView.class);
                            context.startActivity(intent);
                            activity.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);

                        }
                    });

                    loginButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(context, SSLoginView.class);
                            context.startActivity(intent);
                            activity.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
                        }
                    });

                    cancelButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });

                    dialog.setCancelable(true);
                    dialog.show();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return cartsArr.size();
    }

    public void checkout(final Integer position) {
        final List<String> productIds = new ArrayList<String>();
        final List<CartPerStore> prods = new ArrayList<CartPerStore>();
        prods.addAll(cartsArr.get(position));
        for (int i = 0; i < cartsArr.get(position).size(); i++) {
            productIds.add(cartsArr.get(position).get(i).getProductid());
        }
        final Order newOrder = new Order(CurrentUser.shared.getCurrentUser().getUserid(), cartsArr.get(position).get(0).getUserid(), productIds,
                SharedFunctions.getCurrentTime(), SSFirestoreConstants.placed, "");
        new AlertDialog.Builder(context)
                .setTitle("Proceed to Checkout")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        SSFirestoreManager.firestoreHelper.addOrder(newOrder, new SSFirebaseHandler() {
                            @Override
                            public void completionHandler(Boolean success, String error) {
                                SSFirestoreManager.firestoreHelper.deleteCartProductsObject(cartsArr.get(position).get(0).getUserid(), productIds, new SSFirebaseHandler() {
                                    @Override
                                    public void completionHandler(Boolean success, String error) {
                                        SSFirestoreManager.firestoreHelper.deleteCartObject(cartsArr.get(position).get(0).getUserid(), new SSFirebaseHandler() {
                                            @Override
                                            public void completionHandler(Boolean success, String error) {
                                                SharedVariables.shared.cartProgressHandler.completionHandler(false, "");
                                                SharedVariables.shared.updateCartCount.completionHandler(true, "");
                                                cartsArr.remove(position);
                                                notifyDataSetChanged();
                                                Intent i = new Intent(context, SSCheckoutScreenView.class);
                                                SharedVariables.shared.setCartItems(prods);
                                                context.startActivity(i);
                                                activity.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
                                            }
                                        });
                                    }
                                });
                            }
                        });

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SharedVariables.shared.cartProgressHandler.completionHandler(false, "");
                    }
                }).show();
    }
}
