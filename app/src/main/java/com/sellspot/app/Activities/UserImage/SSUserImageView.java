package com.sellspot.app.Activities.UserImage;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.sellspot.app.Activities.EditProduct.SSEditProductView;
import com.sellspot.app.Activities.Followers.SSFollowersView;
import com.sellspot.app.Activities.Search.SSSearchView;
import com.sellspot.app.Adapters.ImagePagerAdapter;
import com.sellspot.app.Constants.SSConstants;
import com.sellspot.app.Constants.SSFirestoreConstants;
import com.sellspot.app.Handlers.SSCompletionHandler;
import com.sellspot.app.Handlers.SSFirebaseHandler;
import com.sellspot.app.Handlers.SSPostHandler;
import com.sellspot.app.Models.CartPerStore;
import com.sellspot.app.Models.GuestUser;
import com.sellspot.app.Networking.SSFirebaseDynamicLinksManager;
import com.sellspot.app.Networking.SSFirestoreManager;
import com.sellspot.app.R;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import com.sellspot.app.Models.Product;
import com.sellspot.app.Shared.CurrentUser;
import com.sellspot.app.Shared.SharedVariables;
import com.zhpan.indicator.IndicatorView;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SSUserImageView extends AppCompatActivity {

    private TextView productPrice;
    private TextView productSize;
    private TextView productMaterial;
    private TextView productDescription;
    private TextView tags;
    private Button addToCartBtn;
    private ScrollView scrollView;
    private SSUserImageVM viewModel = new SSUserImageVM();
    private Context context;
    private ViewPager2 viewPager2;
    private ImagePagerAdapter imagePagerAdapter;

    String userID;
    String productID;
    private Product prod;

    IndicatorView indicatorView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_image_view);
        initUIAttribites();
        initialiseVM();
    }

    private void initUIAttribites() {
        context = this;
        this.getSupportActionBar().hide();
        viewPager2 = (ViewPager2) findViewById(R.id.viewimagePager);
        scrollView = (ScrollView) findViewById(R.id.scrollView);
        productPrice = (TextView) findViewById(R.id.productPriceTextView);
        productSize = (TextView) findViewById(R.id.productSizeTextView);
        productMaterial = (TextView) findViewById(R.id.productMaterialTextView);
        productDescription = (TextView) findViewById(R.id.productDescriptionTextView);
        addToCartBtn = (Button) findViewById(R.id.addToCartBtn);
        tags = (TextView) findViewById(R.id.tagsTextview);
        indicatorView = (IndicatorView) findViewById(R.id.indicator_view);

        userID = getIntent().getStringExtra("produserid");
        productID = getIntent().getStringExtra("prodid");

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
        productPrice.setText("Price: RS " + Integer.toString(prod.getPrice()));
        productSize.setText(prod.getSize());
        productMaterial.setText(prod.getMaterial());
        productDescription.setText(prod.getDescription());
        String tagsString = "";
        List<String> tagsArray = prod.getTags();
        if (tagsArray != null) {
            for (String tag : tagsArray) {
                tagsString += "#" + tag + " ";
            }
        }
        tags.setMovementMethod(LinkMovementMethod.getInstance());
        SpannableStringBuilder ssb = addClickablePart(tagsString);
        tags.setText(ssb, TextView.BufferType.SPANNABLE);
        if (CurrentUser.shared.getIsGuest(getBaseContext()) == false)
        {
            if (CurrentUser.shared.getCurrentUser().getUserid().equals(prod.getUserid())) {
                addToCartBtn.setVisibility(View.GONE);
            }
        }
        if(!prod.getStatus().equals(SSFirestoreConstants.forsale)) {
            addToCartBtn.setText("Sold Out");
            addToCartBtn.setEnabled(false);
        }
    }

    private SpannableStringBuilder addClickablePart(String str) {
        SpannableStringBuilder ssb = new SpannableStringBuilder(str);
        int index1 = str.indexOf("#");
        int index2 = 0;
        while (index1 != -1) {
            index2 = str.indexOf(" ", index1);
            final String clickString = str.substring(index1, index2);
            ssb.setSpan(new ForegroundColorSpan(Color.BLUE),index1,index2, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            ssb.setSpan(new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    Intent intent = new Intent(context, SSSearchView.class);
                    intent.putExtra(SSConstants.viewTags, true);
                    intent.putExtra(SSConstants.tagString, clickString);
                    startActivity(intent);
                }
            }, index1, index2, 0);
            index1 = str.indexOf("#", index2);
        }
        return ssb;
    }

    public void addToCartClick(View v) throws IOException, ClassNotFoundException {
        CartPerStore cps = new CartPerStore(prod.getUserid(), prod.getProductid(), prod.getPrice(), prod.getSize(), prod.getImages().get(0));
        if (CurrentUser.shared.getIsGuest(getBaseContext()) == false)
        {
            viewModel.addProductToCart(cps);
        }
        else
        {
            GuestUser.sharedGuest.saveToFile(cps,context);
            addToCartBtn.setText("  Item Added to Cart  ");
            addToCartBtn.setEnabled(false);
            SharedVariables.shared.increaseCartCount.completionHandler(true,"");

        }
    }

    public void onShareClick(View v) {
        ArrayList<String> options = new ArrayList<>();
         if (CurrentUser.shared.getCurrentUser() != null) {
            if (prod.getUserid().equals(CurrentUser.shared.getCurrentUser().getUserid()) && prod.getStatus().equals(SSFirestoreConstants.forsale)) {
                options.add("Edit");
            }
        }
         options.add("Share");
        if (CurrentUser.shared.getCurrentUser() != null) {
            if (prod.getUserid().equals(CurrentUser.shared.getCurrentUser().getUserid()) && prod.getStatus().equals(SSFirestoreConstants.forsale)) {
                options.add("Delete");
            }
        }

        String[] finalOptions = new String[options.size()];
        finalOptions = options.toArray(finalOptions);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        String[] finalOptions1 = finalOptions;
        builder.setItems(finalOptions, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (finalOptions1[which] == "Edit") {
                    goToEditProduct();
                } else if (finalOptions1[which] == "Share") {
                    shareProduct();
                } else if (finalOptions1[which] == "Delete") {
                    deleteProduct();
                }
            }
        });
        builder.show();
    }

    private void goToEditProduct() {
        Intent intent = new Intent(context , SSEditProductView.class);
        SharedVariables.shared.sharedProduct = prod;
        startActivity(intent);
    }

    private void shareProduct() {
        if (prod.getShareablelink() != null) {
            if (prod.getShareablelink().equals("") == false) {
                openShareSheet(prod.getShareablelink());
            }
        } else {
            String title = "Hey! Check out this Product on Sell Spot for RS " + Integer.toString(prod.getPrice());
            SSFirebaseDynamicLinksManager.shared.createDynamicLink(productID, prod.getUserid(), title, prod.getDescription(), Uri.parse(prod.getImages().get(0)), new SSCompletionHandler() {
                @Override
                public void completionHandler(Boolean success, String error) {
                    if (success) {
                        prod.setShareablelink(error);
                        openShareSheet(error);
                        SSFirestoreManager.firestoreHelper.updateProductShareableLink(productID, prod.getUserid(), error);
                    }
                }
            });
        }
    }

    private void deleteProduct() {
        new AlertDialog.Builder(context)
                .setTitle("Are you sure you want to delete the product?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        viewModel.deleteProduct(prod.getProductid());
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).show();
    }

    public void openShareSheet(String uri) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, uri);
        sendIntent.setType("text/plain");

        Intent shareIntent = Intent.createChooser(sendIntent, null);
        startActivity(shareIntent);
    }

    public void checkIfItemInGuestCart()
    {
        List<List<CartPerStore>> cartsArray = GuestUser.sharedGuest.getCartperstoreproducts();
        if(cartsArray != null) {
            for (int i = 0; i < cartsArray.size(); i++) {
                for (int j = 0; j < cartsArray.get(i).size(); j++) {
                    if (cartsArray.get(i).get(j).getProductid().equals(prod.getProductid())) {
                        addToCartBtn.setText("  Item already in Cart  ");
                        addToCartBtn.setEnabled(false);
                        return;
                    }
                }
            }
        }

    }

    private void initialiseVM() {
        viewModel.addToCartHandler = new SSFirebaseHandler() {
            @Override
            public void completionHandler(Boolean success, String error) {
                if (success) {
                    addToCartBtn.setText("  Item Added to Cart  ");
                    addToCartBtn.setEnabled(false);
                    SharedVariables.shared.increaseCartCount.completionHandler(true,"");
                } else {
                    Toast.makeText(context, "Unsuccessful", Toast.LENGTH_LONG).show();
                }
            }
        };
        viewModel.itemInCartHandler = new SSFirebaseHandler() {
            @Override
            public void completionHandler(Boolean success, String error) {
                 if (success) {
                    addToCartBtn.setText("  Item already in Cart  ");
                    addToCartBtn.setEnabled(false);
                }
            }
        };
        viewModel.deleteHandler = new SSFirebaseHandler() {
            @Override
            public void completionHandler(Boolean success, String error) {
                if(success) {
                    finish();
                }
                else {
                    Toast.makeText(context, "Unsuccessful", Toast.LENGTH_LONG).show();
                }
            }
        };
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
    }

    @Override
    protected void onResume() {
        super.onResume();
        SSFirestoreManager.firestoreHelper.getProductById(userID, productID, new SSPostHandler() {
            @Override
            public void completionHandler(Boolean success, Product products, String error) {
                if (success) {
                    prod = products;
                    if (CurrentUser.shared.getIsGuest(getBaseContext()) == false)
                    {
                        viewModel.checkIfItemInCart(prod);
                    }
                    else{
                        checkIfItemInGuestCart();
                    }
                    setupProduct();
                }
            }
        });
    }
}
