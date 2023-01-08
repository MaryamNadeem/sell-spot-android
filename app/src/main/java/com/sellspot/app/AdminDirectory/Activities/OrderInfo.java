package com.sellspot.app.AdminDirectory.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.sellspot.app.Constants.SSFirestoreConstants;
import com.sellspot.app.Handlers.SSUserHandler;
import com.sellspot.app.Models.Order;
import com.sellspot.app.Models.User;
import com.sellspot.app.Networking.SSFirebaseManager;
import com.sellspot.app.Networking.SSFirestoreManager;
import com.sellspot.app.R;
import com.sellspot.app.Shared.HomeLastProduct;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;

public class OrderInfo extends AppCompatActivity {

    TextView orderbyuserId;
    TextView orderbyusername;
    TextView orderbyemail;
    TextView orderbycontact;
    TextView orderbyaddress;
    TextView orderfromuserId;
    TextView orderfromusername;
    TextView orderfromemail;
    TextView orderfromcontact;
    TextView orderfromaddress;
    Button placedButton;
    Button inprocessButton;
    Button dispatchedButton;
    Button deliveredButton;
    Button forsaleButton;

    String type;
    String updateType;
    Order order;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_info);
        initUIAttributes();
        fetchData();
    }

    void initUIAttributes() {
        this.getSupportActionBar().hide();
        order = HomeLastProduct.shared.selectedOrder;
        orderbyuserId = (TextView) findViewById(R.id.orderbyuserId);
        orderbyusername = (TextView) findViewById(R.id.orderbyusername);
        orderbyemail = (TextView) findViewById(R.id.orderbyemail);
        orderbycontact = (TextView) findViewById(R.id.orderbycontact);
        orderbyaddress = (TextView) findViewById(R.id.orderbyaddress);
        orderfromuserId = (TextView) findViewById(R.id.orderfromuserId);
        orderfromusername = (TextView) findViewById(R.id.orderfromusername);
        orderfromemail = (TextView) findViewById(R.id.orderfromemail);
        orderfromcontact = (TextView) findViewById(R.id.orderfromcontact);
        orderfromaddress = (TextView) findViewById(R.id.orderfromaddress);
        placedButton = (Button) findViewById(R.id.placedButton);
        inprocessButton = (Button) findViewById(R.id.inprocessButton);
        dispatchedButton = (Button) findViewById(R.id.dispatchedButton);
        deliveredButton = (Button) findViewById(R.id.deliveredButton);
        forsaleButton = (Button) findViewById(R.id.forsaleButton);
        type = getIntent().getStringExtra("order type");
        switch (type) {
            case SSFirestoreConstants.placed:
                placedButton.setVisibility(View.GONE);
                break;
            case SSFirestoreConstants.inprocess:
                inprocessButton.setVisibility(View.GONE);
                break;
            case SSFirestoreConstants.dispatched:
                dispatchedButton.setVisibility(View.GONE);
                break;
            case SSFirestoreConstants.delivered:
                deliveredButton.setVisibility(View.GONE);
                break;
            case SSFirestoreConstants.cancelledOrReturned:
                forsaleButton.setVisibility(View.GONE);
                break;
            default:
                break;
        }
    }

    void fetchData() {
        if (HomeLastProduct.shared.selectedOrder != null) {
            orderbyuserId.setText(order.getPlacedBy());
            SSFirestoreManager.firestoreHelper.getUserProfile(order.getPlacedBy(), new SSUserHandler() {
                @Override
                public void completionHandler(Boolean success, User user, String error) {
                    if (success) {
                        orderbyusername.setText(user.getUsername());
                        orderbyemail.setText(user.getEmail());
                        orderbycontact.setText(user.getPhonenumber());
                        orderbyaddress.setText(user.getAddress());
                    }
                }
            });

            orderfromuserId.setText(order.getPlacedFrom());
            SSFirestoreManager.firestoreHelper.getUserProfile(order.getPlacedFrom(), new SSUserHandler() {
                @Override
                public void completionHandler(Boolean success, User user, String error) {
                    if (success) {
                        orderfromusername.setText(user.getUsername());
                        orderfromemail.setText(user.getEmail());
                        orderfromcontact.setText(user.getPhonenumber());
                        orderfromaddress.setText(user.getAddress());
                    }
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        HomeLastProduct.shared.selectedOrder = null;
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
    }

    public void markForSale(View v) {
        String collectionId = SSFirestoreConstants.orders;
        DocumentReference documentReference = SSFirebaseManager.firestoreDatabase.collection(collectionId).document(HomeLastProduct.shared.selectedOrder.getOrderId());
        documentReference.update(SSFirestoreConstants.status, SSFirestoreConstants.cancelledOrReturned
        ).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) { System.out.println("Failed" + e.getLocalizedMessage().toString()); }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                System.out.println("Success");
            }
        });

        for(int i = 0; i < order.getProductId().size(); i++) {
            String document1 = SSFirestoreConstants.products+"/"+order.getPlacedFrom()+ "/" +SSFirestoreConstants.userproducts+"/"+order.getProductId().get(i);
            DocumentReference docRef1 = SSFirebaseManager.firestoreDatabase.document(document1);
            docRef1.update(SSFirestoreConstants.status, SSFirestoreConstants.forsale).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    // for later user
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // for later user
                }
            });
        }
        finish();
    }

    public void markPlaced(View v) {
        String collectionId = SSFirestoreConstants.orders;
        DocumentReference documentReference = SSFirebaseManager.firestoreDatabase.collection(collectionId).document(HomeLastProduct.shared.selectedOrder.getOrderId());
        documentReference.update(SSFirestoreConstants.status,SSFirestoreConstants.placed
        ).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) { System.out.println("Failed" + e.getLocalizedMessage().toString()); }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                System.out.println("Success");
            }
        });

        for(int i = 0; i < order.getProductId().size(); i++) {
            String document1 = SSFirestoreConstants.products+"/"+order.getPlacedFrom()+ "/" +SSFirestoreConstants.userproducts+"/"+order.getProductId().get(i);
            DocumentReference docRef1 = SSFirebaseManager.firestoreDatabase.document(document1);
            docRef1.update(SSFirestoreConstants.status, SSFirestoreConstants.placed).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    // for later user
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // for later user
                }
            });
        }
        finish();
    }

    public void markInProcess(View v) {
        String collectionId = SSFirestoreConstants.orders;
        DocumentReference documentReference = SSFirebaseManager.firestoreDatabase.collection(collectionId).document(HomeLastProduct.shared.selectedOrder.getOrderId());
        documentReference.update(
                SSFirestoreConstants.status,SSFirestoreConstants.inprocess
        ).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                System.out.println("Failed" + e.getLocalizedMessage().toString());
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                System.out.println("Success");
            }
        });
        for(int i = 0; i < order.getProductId().size(); i++) {
            String document1 = SSFirestoreConstants.products+"/"+order.getPlacedFrom()+ "/" +SSFirestoreConstants.userproducts+"/"+order.getProductId().get(i);
            DocumentReference docRef1 = SSFirebaseManager.firestoreDatabase.document(document1);
            docRef1.update(SSFirestoreConstants.status, SSFirestoreConstants.inprocess).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    // for later user
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // for later user
                }
            });
        }
        finish();
    }

    public void markDispatched(View v) {
        String collectionId = SSFirestoreConstants.orders;
        DocumentReference documentReference = SSFirebaseManager.firestoreDatabase.collection(collectionId).document(HomeLastProduct.shared.selectedOrder.getOrderId());
        documentReference.update(
                SSFirestoreConstants.status,SSFirestoreConstants.dispatched
        ).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                System.out.println("Failed" + e.getLocalizedMessage().toString());
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                System.out.println("Success");
            }
        });
        for(int i = 0; i < order.getProductId().size(); i++) {
            String document1 = SSFirestoreConstants.products+"/"+order.getPlacedFrom()+ "/" +SSFirestoreConstants.userproducts+"/"+order.getProductId().get(i);
            DocumentReference docRef1 = SSFirebaseManager.firestoreDatabase.document(document1);
            docRef1.update(SSFirestoreConstants.status, SSFirestoreConstants.dispatched).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    // for later user
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // for later user
                }
            });
        }
        finish();
    }

    public void markDelivered(View v) {
        String collectionId = SSFirestoreConstants.orders;
        DocumentReference documentReference = SSFirebaseManager.firestoreDatabase.collection(collectionId).document(HomeLastProduct.shared.selectedOrder.getOrderId());
        documentReference.update(
                SSFirestoreConstants.status,SSFirestoreConstants.delivered
        ).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                System.out.println("Failed" + e.getLocalizedMessage().toString());
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                System.out.println("Success");
            }
        });
        for(int i = 0; i < order.getProductId().size(); i++) {
            String document1 = SSFirestoreConstants.products+"/"+order.getPlacedFrom()+ "/" +SSFirestoreConstants.userproducts+"/"+order.getProductId().get(i);
            DocumentReference docRef1 = SSFirebaseManager.firestoreDatabase.document(document1);
            docRef1.update(SSFirestoreConstants.status, SSFirestoreConstants.soldout).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    // for later user
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // for later user
                }
            });
        }
        finish();
    }

    public void viewProducts(View v) {
        Intent intent = new Intent(this, OrderProducts.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
    }
}