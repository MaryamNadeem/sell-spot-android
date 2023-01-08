package com.sellspot.app.Activities.EditProduct;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.common.util.ArrayUtils;
import com.sellspot.app.Adapters.ImagePagerAdapter;
import com.sellspot.app.Handlers.SSCompletionHandler;
import com.sellspot.app.Handlers.SSFirebaseHandler;
import com.sellspot.app.Models.Product;
import com.sellspot.app.Networking.SSFirebaseDynamicLinksManager;
import com.sellspot.app.Networking.SSFirestoreManager;
import com.sellspot.app.R;
import com.sellspot.app.Shared.SharedVariables;
import com.zhpan.indicator.IndicatorView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SSEditProductView extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private Context context;

    private Spinner newItemSize;

    private EditText newItemTags;
    private EditText newItemPrice;
    private EditText newItemMaterial;
    private EditText newItemDescription;
    private EditText customSizeDesciption;
    private ViewPager2 viewPager2;
    private ImagePagerAdapter imagePagerAdapter;
    IndicatorView indicatorView;

    private String allTags = "";
    private String[] sizes = new String[]{"Select Size","Unstitched", "Extra Small", "Small", "Medium", "Large", "Extra Large", "Custom-Made"};
    private List<String> editsizes = Arrays.asList("unstitched", "extra small", "small", "medium", "large", "extra large");
    private String material;
    private int price;
    private List<String> allTagLabels = new ArrayList<String>();
    private String description;
    private String size;

    private Product product;

    private ProgressDialog progressDialog;

    private SSEditProductVM viewModel = new SSEditProductVM();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product_view);
        initUIAttributes();
        setSizeDropDown();
        initialiseVM();
        setupProduct();
    }

    private void initUIAttributes() {
        this.getSupportActionBar().hide();
        context = this;
        viewPager2 = (ViewPager2) findViewById(R.id.imagePager);
        newItemPrice = (EditText) findViewById(R.id.newItemPrice);
        newItemMaterial = (EditText) findViewById(R.id.newItemMaterial);
        newItemDescription = (EditText) findViewById(R.id.newItemDescription);
        newItemSize = (Spinner) findViewById(R.id.newItemSize);
        customSizeDesciption = (EditText) findViewById(R.id.customSizeDesciption);
        newItemTags = (EditText) findViewById(R.id.newItemTags);
        indicatorView = (IndicatorView) findViewById(R.id.indicator_view2);
        indicatorView.setSliderColor(getResources().getColor(R.color.borderColor),getResources().getColor(R.color.appthemecolor));
        indicatorView.setPageSize(0);
        indicatorView.setSliderWidth(25, 25);
        indicatorView.notifyDataChanged();

        product = new Product(SharedVariables.shared.sharedProduct);

        imagePagerAdapter = new ImagePagerAdapter(SharedVariables.shared.productImages, context, (Activity) this);
        viewPager2.setAdapter(imagePagerAdapter);

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

    private void setSizeDropDown() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, sizes);
        newItemSize.setAdapter(adapter);
        newItemSize.setOnItemSelectedListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        switch (i) {
            case 7:
                customSizeDesciption.setVisibility(View.VISIBLE);
                break;
            default:
                customSizeDesciption.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {}

    private void initialiseVM() {
        viewModel.updateItemHandler = new SSFirebaseHandler() {
            @Override
            public void completionHandler(Boolean success, String error) {
                if (success) {
                    finish();
                } else {
                    Toast.makeText(context, error, Toast.LENGTH_LONG).show();
                }
            }
        };
    }

    private void setupProduct() {
        imagePagerAdapter = new ImagePagerAdapter(context, product.getImages(), (Activity) this);
        indicatorView.setPageSize(product.getImages().size());
        if (product.getImages().size() == 1) {
            indicatorView.setVisibility(View.GONE);
        }
        viewPager2.setAdapter(imagePagerAdapter);
        indicatorView.notifyDataChanged();

        newItemPrice.setText(Integer.toString(product.getPrice()));
        newItemMaterial.setText(product.getMaterial());
        newItemDescription.setText(product.getDescription());
        String tagsString = "";
        List<String> tagsArray = product.getTags();
        if (tagsArray != null) {
            for (String tag : tagsArray) {
                tagsString += "#" + tag + " ";
            }

        }
        newItemTags.setText(tagsString);
        Integer index = editsizes.indexOf(product.getSize());
        if (index > -1) {
            newItemSize.setSelection(index+1);
        } else {
            newItemSize.setSelection(7);
            customSizeDesciption.setText(product.getSize());
            customSizeDesciption.setVisibility(View.VISIBLE);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public boolean dataValidation() {
        if (newItemPrice.getText().toString().length() < 1) {
            Toast.makeText(context, "Please add product price.", Toast.LENGTH_LONG).show();
            return false;
        }
        if (newItemMaterial.getText().toString().length() < 1) {
            Toast.makeText(context, "Please add product material.", Toast.LENGTH_LONG).show();
            return false;
        }
        if (newItemSize.getSelectedItemPosition() == 0) {
            Toast.makeText(context, "Please add product size.", Toast.LENGTH_LONG).show();
            return false;
        }
        if (newItemSize.getSelectedItemPosition() == 7 && customSizeDesciption.getText().toString().length() < 1) {
            Toast.makeText(context, "Please add custom size.", Toast.LENGTH_LONG).show();
            return false;
        }
        if (newItemTags.getText().toString().length() > 1) {
            updateTags();
        }
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void addNewItemClick(View v) {
        if(dataValidation() == true) {
            progressDialog = ProgressDialog.show(this, "Updating...", "Please wait...", true);
            price = Integer.parseInt(newItemPrice.getText().toString());
            material = newItemMaterial.getText().toString().toLowerCase();
            description = newItemDescription.getText().toString();
            if (customSizeDesciption.getVisibility() == View.VISIBLE) {
                size = customSizeDesciption.getText().toString().toLowerCase();
            } else {
                size = newItemSize.getSelectedItem().toString().toLowerCase();
            }
            product.setPrice(price);
            product.setMaterial(material);
            product.setDescription(description);
            product.setSize(size);
            product.setTags(allTagLabels);

            String title = "Hey! Check out this Product on Sell Spot for RS " + Integer.toString(product.getPrice());
            SSFirebaseDynamicLinksManager.shared.createDynamicLink(product.getProductid(), product.getUserid(), title, product.getDescription(), Uri.parse(product.getImages().get(0)), new SSCompletionHandler() {
                @Override
                public void completionHandler(Boolean success, String error) {
                    if (success) {
                        product.setShareablelink(error);
                        viewModel.updateProduct(product);
                    } else {
                        Toast.makeText(context, error, Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    public void updateTags() {
        allTagLabels= new ArrayList<String>();
        String tagsString = newItemTags.getText().toString();
        List<String> tagsList = Arrays.asList(tagsString.split("#", -1));
        for (String tag: tagsList) {
            String hashesRemoved = tag.replace("#", " ");
            String spacesRemoved = hashesRemoved.replace(" ", "");
            String extraLinesRemoved = spacesRemoved.replace("\n", "").replace("\r", "");
            if (!extraLinesRemoved.equals("")) {
                allTagLabels.add(extraLinesRemoved);
            }
        }
    }
}
