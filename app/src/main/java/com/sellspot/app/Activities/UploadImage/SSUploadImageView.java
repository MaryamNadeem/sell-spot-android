package com.sellspot.app.Activities.UploadImage;

import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.view.View;
import android.os.Bundle;
import android.content.Intent;
import android.content.Context;
import android.graphics.Bitmap;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.sellspot.app.Activities.Camera.SSProfilePictureCameraVC;
import com.sellspot.app.Activities.MainActivity.SSMainActivity;
import com.sellspot.app.Handlers.SSFirebaseHandler;
import com.sellspot.app.R;

import android.widget.Button;
import android.provider.MediaStore;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.sellspot.app.Shared.CurrentUser;
import com.theartofdev.edmodo.cropper.CropImage;
import com.sellspot.app.Constants.SSConstants;
import com.sellspot.app.Shared.SharedFunctions;

public class SSUploadImageView extends AppCompatActivity {
    private int checkActivity;

    private Context context;

    private Button continueButton;
    private TextView headingLabel;
    private ProgressBar progressBar;
    private LinearLayout addLaterView;
    private ImageView profilePictureImageView;

    boolean doubleBackToExitPressedOnce = false;

    private SSUploadImageVM viewModel = new SSUploadImageVM();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upload_image_view);
        initUIAttributes();
        initialiseVM();
    }

    private void initUIAttributes() {
        this.getSupportActionBar().hide();
        context = this;
        continueButton = (Button) findViewById(R.id.continueButton);
        headingLabel = (TextView) findViewById(R.id.headingLabel);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        addLaterView = (LinearLayout) findViewById(R.id.addLaterView);
        checkActivity = getIntent().getExtras().getInt(SSConstants.IMAGE_KEY);
        profilePictureImageView = (ImageView) findViewById(R.id.profilePictureImageView);
        setContinueButtonText();
        if (checkActivity == SSConstants.SETTINGS_PICTURE_VALUE) {
            addLaterView.setVisibility(View.GONE);
            continueButton.setVisibility(View.VISIBLE);
            headingLabel.setText("Update Profile Picture");
        }
    }

    public void onCameraClick(View view) {
        if (SharedFunctions.checkCameraHardware(this)) {
            Intent intent = new Intent(context, SSProfilePictureCameraVC.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
        } else {
            Toast.makeText(context, "This device has no Camera", Toast.LENGTH_LONG).show();
        }
    }

    public void onGalleryClick(View view) {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(Intent.createChooser(galleryIntent, "Select Image From Gallery"), SSConstants.GALLERY_REQUEST_CODE);
    }

    public void onSkipClick(View view) {
        Intent i = new Intent(context, SSMainActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
    }

    public void onContinueClick(View view) {
        if(checkActivity == SSConstants.PROFILE_PICTURE_VALUE) {
            Intent i = new Intent(context, SSMainActivity.class);
            startActivity(i);
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
        } else if (checkActivity == SSConstants.SETTINGS_PICTURE_VALUE) {
            finish();
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
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
            CropImage.activity(imageUri).setAspectRatio(1,1).start(this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            progressBar.setVisibility(View.VISIBLE);
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), resultUri);
                    viewModel.setUserProfileImage(bitmap);
                } catch (Exception e) {
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                progressBar.setVisibility(View.INVISIBLE);
                Exception error = result.getError();
                Toast.makeText(context, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

    public void initialiseVM() {
        viewModel.pictureSuccess = new SSFirebaseHandler() {
            @Override
            public void completionHandler(Boolean success, String error) {
            if (success) {
                displayImage();
            } else {
                progressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(context, error, Toast.LENGTH_LONG).show();
            }
            }
        };
    }

    public void displayImage() {
        String image =  CurrentUser.shared.getCurrentUser().getImage();
        if (!image.equals("")) {
            progressBar.setVisibility(View.VISIBLE);
            Glide.with(context).load(image).listener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    progressBar.setVisibility(View.INVISIBLE);
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    progressBar.setVisibility(View.INVISIBLE);
                    return false;
                }
            }).into(profilePictureImageView);
            addLaterView.setVisibility(View.GONE);
            continueButton.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    public void setContinueButtonText() {
        if(checkActivity == SSConstants.PROFILE_PICTURE_VALUE) {
            continueButton.setText("Continue");
        } else if (checkActivity == SSConstants.SETTINGS_PICTURE_VALUE) {
            continueButton.setText("Done");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        displayImage();
    }

    @Override
    public void onBackPressed() {
        if(checkActivity == SSConstants.SETTINGS_PICTURE_VALUE)
        {
            super.onBackPressed();
        }
        else {
            if (doubleBackToExitPressedOnce) {
                finishAffinity();
                return;
            }

            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        }
    }
}