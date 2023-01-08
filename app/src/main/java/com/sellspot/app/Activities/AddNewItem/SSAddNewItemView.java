package com.sellspot.app.Activities.AddNewItem;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Html;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.content.Context;
import android.widget.EditText;
import android.widget.ImageView;

import com.sellspot.app.Activities.Camera.SSCameraView;
import com.sellspot.app.Adapters.ImagePagerAdapter;
import com.sellspot.app.Constants.SSConstants;
import com.sellspot.app.Constants.SSFirestoreConstants;
import com.sellspot.app.Handlers.SSFirebaseHandler;
import com.sellspot.app.Models.Product;
import com.sellspot.app.Networking.SSFirebaseStorageManager;
import com.sellspot.app.R;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.sellspot.app.Services.ProductImageService;
import com.sellspot.app.Shared.CurrentUser;
import com.sellspot.app.Shared.SharedFunctions;
import com.sellspot.app.Shared.SharedVariables;
import com.theartofdev.edmodo.cropper.CropImage;
import com.zhpan.indicator.IndicatorView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class SSAddNewItemView extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private Context context;

    private Spinner newItemSize;

    private TextView addImageLabel;

    private FrameLayout imageOptionsLayout;
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

    private String material;
    private int price;
    private List<String> allTagLabels = new ArrayList<String>();
    private String description;
    private String size;

    private FrameLayout retakeView;
    private FrameLayout addMoreView;

    private ImageButton closeButton;

    private Product product;

    private ProgressDialog progressDialog;

    Boolean retake = false;
    int retakeIndex = -1;

    Boolean isCamera = false;

    private SSAddNewItemVM viewModel = new SSAddNewItemVM();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_new_item_view);
        initUIAttributes();
        setSizeDropDown();
        initialiseVM();
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
        imageOptionsLayout = (FrameLayout) findViewById(R.id.imageOptionsLayout);
        retakeView = (FrameLayout) findViewById(R.id.retakeView);
        addMoreView = (FrameLayout) findViewById(R.id.addMoreView);
        closeButton = (ImageButton) findViewById(R.id.closeButton);
        addImageLabel = (TextView) findViewById(R.id.addImageLabel);
        indicatorView = (IndicatorView) findViewById(R.id.indicator_view2);
        indicatorView.setSliderColor(getResources().getColor(R.color.borderColor),getResources().getColor(R.color.appthemecolor));
        indicatorView.setPageSize(0);
        indicatorView.setSliderWidth(25, 25);
        indicatorView.notifyDataChanged();

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
        viewModel.addItemHandler = new SSFirebaseHandler() {
            @Override
            public void completionHandler(Boolean success, String error) {
            if (success) {
                progressDialog.dismiss();
                List<Bitmap> productImages = new ArrayList<Bitmap>();

                productImages.addAll(SharedVariables.shared.productImages);
                SharedVariables.shared.localProductImages.put(product.getProductid(), productImages);
                Intent intent = new Intent(context, ProductImageService.class);
                intent.putExtra(SSConstants.productId, product.getProductid());
                ContextCompat.startForegroundService(context, intent);
                finish();

            } else {
                Toast.makeText(context, error, Toast.LENGTH_LONG).show();
            }
            }
        };
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    public boolean dataValidation() {

        if (SharedVariables.shared.productImages.size() <= 0) {
            Toast.makeText(context, "Please add product image.", Toast.LENGTH_LONG).show();
            return false;
        }
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
            progressDialog = ProgressDialog.show(this, "Adding new item...", "Please wait...", true);
            price = Integer.parseInt(newItemPrice.getText().toString());
            material = newItemMaterial.getText().toString().toLowerCase();
            description = newItemDescription.getText().toString();
            if (customSizeDesciption.getVisibility() == View.VISIBLE) {
                size = customSizeDesciption.getText().toString().toLowerCase();
            } else {
                size = newItemSize.getSelectedItem().toString().toLowerCase();
            }
            product = new Product(description, new ArrayList<String>(), material, price, "", size, SSFirestoreConstants.forsale, allTagLabels, SharedFunctions.getCurrentTime(), CurrentUser.shared.getCurrentUser().getUserid(), CurrentUser.shared.getCurrentUser().getUsername(), "");
            viewModel.addNewItem(product);
            SharedVariables.shared.sharedProduct = new Product(product);
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

    public void onCameraClick(View v) {
        goToCamera();
    }

    private void goToCamera() {
        if (SharedFunctions.checkCameraHardware(this)) {
            Intent intent = new Intent(context, SSCameraView.class);
            intent.putExtra(SSConstants.IMAGE_KEY, SSConstants.ADD_NEW_ITEM_VALUE);
            isCamera = true;
            if(retake == true) {
                intent.putExtra("retake_index", retakeIndex);
            }
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
        } else {
            Toast.makeText(context, "This device has no Camera", Toast.LENGTH_LONG).show();
        }
    }

    public void onGalleryClick(View v) {
        goToGallery();
    }

    public void goToGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(Intent.createChooser(galleryIntent, "Select Image From Gallery"), SSConstants.GALLERY_REQUEST_CODE);
    }

    public void retakeClick(View v) {
        imageOptionsLayout.setVisibility(View.VISIBLE);
        retakeView.setVisibility(View.GONE);
        closeButton.setVisibility(View.VISIBLE);
        addImageLabel.setVisibility(View.GONE);
        addMoreView.setVisibility(View.GONE);
        retake = true;
        retakeIndex = viewPager2.getCurrentItem();
    }

    public void addMoreClick(View v) {
        imageOptionsLayout.setVisibility(View.VISIBLE);
        retakeView.setVisibility(View.GONE);
        closeButton.setVisibility(View.VISIBLE);
        addImageLabel.setVisibility(View.GONE);
        addMoreView.setVisibility(View.GONE);
    }

    public void onCloseClick(View v) {
        imageOptionsLayout.setVisibility(View.GONE);
        retakeView.setVisibility(View.VISIBLE);
        closeButton.setVisibility(View.GONE);
        addImageLabel.setVisibility(View.VISIBLE);

        if (retake == true) {
            retake = false;
            retakeIndex = -1;
        }

        if (SharedVariables.shared.productImages.size() == 0) {
            addMoreView.setVisibility(View.GONE);
        }
        if (SharedVariables.shared.productImages.size() > 0 && SharedVariables.shared.productImages.size() < 4) {
            addMoreView.setVisibility(View.VISIBLE);
        } else {
            addMoreView.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        if (requestCode == SSConstants.GALLERY_REQUEST_CODE && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();
            CropImage.activity(imageUri).setAspectRatio(3,4).start(this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), resultUri);
                    imageOptionsLayout.setVisibility(View.GONE);
                    retakeView.setVisibility(View.VISIBLE);
                    if (retake == true) {
                        retake = false;
                        SharedVariables.shared.productImages.set(retakeIndex, bitmap);
                        retakeIndex = -1;
                    } else {
                        SharedVariables.shared.productImages.add(bitmap);
                    }
                } catch (Exception e) {
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(context, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onResume() {
        super.onResume();
        if (SharedVariables.shared.productImages.size() > 0) {
            imagePagerAdapter.images = SharedVariables.shared.productImages;
            imagePagerAdapter.notifyDataSetChanged();
            if (retake == true) {
                viewPager2.setCurrentItem(retakeIndex);
                if (isCamera == true) {
                    retake = false;
                    retakeIndex = -1;
                    isCamera = false;
                }
            } else {
                indicatorView.setPageSize(SharedVariables.shared.productImages.size());
                indicatorView.notifyDataChanged();
                viewPager2.setCurrentItem(SharedVariables.shared.productImages.size() - 1);
            }
            imageOptionsLayout.setVisibility(View.GONE);
            retakeView.setVisibility(View.VISIBLE);
        }

        if (SharedVariables.shared.productImages.size() <= 1) {
            indicatorView.setVisibility(View.GONE);
        } else {
            indicatorView.setVisibility(View.VISIBLE);
        }

        if (isCamera == true) {
            isCamera = false;
        }

        if (SharedVariables.shared.productImages.size() > 0 && SharedVariables.shared.productImages.size() < 4) {
            addMoreView.setVisibility(View.VISIBLE);
        } else {
            addMoreView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        SharedVariables.shared.productImages.clear();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
    }

    @Override
    public void finish() {
        super.finish();
//        SharedVariables.shared.productImages.clear();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
    }
}
