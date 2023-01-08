package com.sellspot.app.AdminDirectory.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.sellspot.app.Activities.UserImage.SSUserImageVM;
import com.sellspot.app.Adapters.ImagePagerAdapter;
import com.sellspot.app.Constants.SSFirestoreConstants;
import com.sellspot.app.Handlers.SSPostHandler;
import com.sellspot.app.Models.Product;
import com.sellspot.app.Networking.SSFirebaseManager;
import com.sellspot.app.Networking.SSFirestoreManager;
import com.sellspot.app.R;
import com.sellspot.app.Shared.CurrentUser;
import com.sellspot.app.Shared.SharedVariables;
import com.zhpan.indicator.IndicatorView;

import java.util.List;

public class AdminViewProduct extends AppCompatActivity {

    private TextView productPrice;
    private TextView productSize;
    private TextView productMaterial;
    private TextView productDescription;
    private TextView tags;
    private Button deleteBtn;
    private ScrollView scrollView;
    private SSUserImageVM viewModel = new SSUserImageVM();
    private Context context;
    private ViewPager2 viewPager2;
    private TextView adminproductId;
    private TextView adminUserId;
    private TextView adminproducttime;
    private TextView adminproductlink;
    private ImagePagerAdapter imagePagerAdapter;

    String userID;
    String productID;
    private Product prod;

    IndicatorView indicatorView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_view_product);
        initUIAttribites();
    }

    private void initUIAttribites() {
        context = this;
        this.getSupportActionBar().hide();
        viewPager2 = (ViewPager2) findViewById(R.id.adminviewimagePager);
        scrollView = (ScrollView) findViewById(R.id.adminscrollView);
        productPrice = (TextView) findViewById(R.id.adminproductPriceTextView);
        productSize = (TextView) findViewById(R.id.adminproductSizeTextView);
        productMaterial = (TextView) findViewById(R.id.adminproductMaterialTextView);
        productDescription = (TextView) findViewById(R.id.adminproductDescriptionTextView);
        deleteBtn = (Button) findViewById(R.id.admindeleteButton);
        tags = (TextView) findViewById(R.id.admintagsTextview);
        indicatorView = (IndicatorView) findViewById(R.id.admin_indicator_view);
        adminproductId = (TextView) findViewById(R.id.adminproductId);
        adminUserId = (TextView) findViewById(R.id.adminuserId);
        adminproducttime = (TextView) findViewById(R.id.adminproducttime);
        adminproductlink = (TextView) findViewById(R.id.adminproductlink);

        userID = getIntent().getStringExtra("produserid");
        productID = getIntent().getStringExtra("prodid");

        SSFirestoreManager.firestoreHelper.getProductById(userID, productID, new SSPostHandler() {
            @Override
            public void completionHandler(Boolean success, Product products, String error) {
                if (success) {
                    prod = products;
                    setupProduct();
                }
            }
        });

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                indicatorView.onPageScrolled(position,positionOffset,positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                indicatorView.onPageSelected(position);
            }
        });
    }

    void setupProduct() {
        indicatorView.setSliderColor(getResources().getColor(R.color.borderColor), getResources().getColor(R.color.appthemecolor));
        if (SharedVariables.shared.localProductImages.containsKey(prod.getProductid()) == true) {
            List<Bitmap> localImages = SharedVariables.shared.localProductImages.get(prod.getProductid());
            imagePagerAdapter = new ImagePagerAdapter(localImages, context, (Activity) this, true);
            indicatorView.setPageSize(localImages.size());
            if (localImages.size() == 1) {
                indicatorView.setVisibility(View.GONE);
            }
        } else {
            imagePagerAdapter = new ImagePagerAdapter(context, prod.getImages(), (Activity) this);
            indicatorView.setPageSize(prod.getImages().size());
            if (prod.getImages().size() == 1) {
                indicatorView.setVisibility(View.GONE);
            }
        }
        viewPager2.setAdapter(imagePagerAdapter);
        indicatorView.notifyDataChanged();
        indicatorView.setSliderWidth(20, 20);
        productPrice.setText("RS " + Integer.toString(prod.getPrice()));
        productSize.setText(prod.getSize());
        productMaterial.setText(prod.getMaterial());
        productDescription.setText(prod.getDescription());
        adminproductId.setText(prod.getProductid());
        adminUserId.setText(prod.getUsername() + "\n\n" + prod.getUserid());
        adminproducttime.setText(prod.getTime());
        adminproductlink.setText(prod.getShareablelink());
        String tagsString = "";
        List<String> tagsArray = prod.getTags();
        if (tagsArray != null) {
            for (String tag : tagsArray) {
                tagsString += "#" + tag + " ";
            }
        }
        tags.setText(tagsString);
    }

    public void onDeleteClick(View v)
    {
        new AlertDialog.Builder(context)
                .setTitle("Are you sure you want to delete the product?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        deleteProduct();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).show();
    }

    void deleteProduct() {
        String doc= SSFirestoreConstants.products+"/"+userID+"/"
                +SSFirestoreConstants.userproducts+"/"+productID;
        DocumentReference docRef = SSFirebaseManager.firestoreDatabase.document(doc);
        docRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Query collectionRef = SSFirebaseManager.firestoreDatabase.collectionGroup(SSFirestoreConstants.product_ids)
                        .whereEqualTo(SSFirestoreConstants.productid, productID);
                collectionRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if(queryDocumentSnapshots.size() > 0) {
                            for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                                doc.getReference().delete();
                            }
                        }
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
            }
        });
    }
}