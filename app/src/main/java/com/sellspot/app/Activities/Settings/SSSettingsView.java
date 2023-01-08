package com.sellspot.app.Activities.Settings;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.os.Bundle;
import android.widget.Button;
import android.content.Intent;
import android.content.Context;
import android.graphics.Bitmap;
import android.widget.EditText;
import android.widget.TextView;
import android.provider.MediaStore;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.sellspot.app.Activities.Followers.SSFollowersView;
import com.sellspot.app.Activities.Following.SSFollowingView;
import com.sellspot.app.Activities.UploadImage.SSUploadImageView;
import com.sellspot.app.Activities.Welcome.SSWelcomeView;
import com.sellspot.app.Handlers.SSFirebaseHandler;
import com.sellspot.app.Handlers.SSFollowersHandler;
import com.sellspot.app.Handlers.SSFollowingsHandler;
import com.sellspot.app.Models.FollowerAndFollowing;
import com.sellspot.app.Networking.SSFirebaseManager;
import com.sellspot.app.Networking.SSFirestoreManager;
import com.sellspot.app.R;

import androidx.annotation.Nullable;
import com.sellspot.app.Models.User;
import com.sellspot.app.Shared.HomeLastProduct;
import com.theartofdev.edmodo.cropper.CropImage;
import androidx.appcompat.app.AppCompatActivity;

import com.sellspot.app.Shared.CurrentUser;
import com.sellspot.app.Constants.SSConstants;
import de.hdodenhof.circleimageview.CircleImageView;
import com.sellspot.app.Shared.SharedFunctions;

import java.util.List;

public class SSSettingsView extends AppCompatActivity {

    private Context context;

    private TextView usernameTextView;
    private EditText settingsEmail;
    private EditText settingsPhone;
    private EditText settingsAddress;
    private EditText settingsPassword;
    private Button updateButton;
    private Button followersButton;
    private Button followingButton;
    private String newPass;
    private String newEmail;
    private String newPhone;
    private String newAddress;
    private CircleImageView settingsImage;

    private ProgressDialog progressDialog;

    private SSSettingsVM viewModel = new SSSettingsVM();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_view);
        initUIAttributes();
        addUIListeners();
        initialiseVM();
    }

    private void initUIAttributes() {
        this.getSupportActionBar().hide();
        context = this;
        usernameTextView = (TextView) findViewById(R.id.usernameTextView);
        settingsEmail = (EditText) findViewById(R.id.settingsEmail);
        settingsPassword = (EditText) findViewById(R.id.settingsPassword);
        settingsPhone = (EditText) findViewById(R.id.settingsPhone);
        settingsAddress = (EditText) findViewById(R.id.settingsAddress);
        settingsImage = (CircleImageView) findViewById(R.id.settingsImage);
        updateButton = (Button) findViewById(R.id.updateButton);
        followersButton = (Button) findViewById(R.id.followersButton);
        followingButton = (Button) findViewById(R.id.followingButton);
        updateButton.setEnabled(false);
        User user= CurrentUser.shared.getCurrentUser();
        usernameTextView.setText(user.getUsername());
        settingsEmail.setText(user.getEmail());
        settingsPassword.setText(user.getPassword());
        settingsPhone.setText(user.getPhonenumber());
        settingsAddress.setText(user.getAddress());
        String image = user.getImage();
        if (!image.equals("")) {
            Glide.with(context).load(image).into(settingsImage);
        }
        setFollowerFollowing();
    }

    void setFollowerFollowing() {
        SSFirestoreManager.firestoreHelper.getUserFollowings(CurrentUser.shared.getCurrentUser().getUserid(), new SSFollowingsHandler() {
            @Override
            public void completionHandler(Boolean success, List<FollowerAndFollowing> followings, String error) {
                if(success)
                {
                    followingButton.setText(followings.size() + " Following");
                }

            }
        });
        SSFirestoreManager.firestoreHelper.getUserFollowers(CurrentUser.shared.getCurrentUser().getUserid(), new SSFollowersHandler() {
            @Override
            public void completionHandler(Boolean success, List<FollowerAndFollowing> followers, String error) {
                if(success)
                {
                    followersButton.setText(followers.size() + " Followers");
                }
            }
        });
    }

    public void onCameraClick(View view) {
        proceedToImageUpload();
    }

    private void proceedToImageUpload() {
        Intent intent = new Intent(this , SSUploadImageView.class);
        intent.putExtra(SSConstants.IMAGE_KEY, SSConstants.SETTINGS_PICTURE_VALUE);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
    }

    private void addUIListeners() {
        settingsEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(CurrentUser.shared.getCurrentUser().getEmail().equals(settingsEmail.getText().toString()))
                {
                    updateButton.setEnabled(false);
                }
                else {
                    updateButton.setEnabled(true);
                }
            }
        });
        settingsPassword.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(CurrentUser.shared.getCurrentUser().getPassword().equals(settingsPassword.getText().toString()))
                {
                    updateButton.setEnabled(false);
                }
                else {
                    updateButton.setEnabled(true);
                }
            }
        });

        settingsPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(CurrentUser.shared.getCurrentUser().getPhonenumber().equals(settingsPhone.getText().toString()))
                {
                    updateButton.setEnabled(false);
                }
                else {
                    updateButton.setEnabled(true);
                }
            }
        });

        settingsAddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(CurrentUser.shared.getCurrentUser().getAddress().equals(settingsAddress.getText().toString()))
                {
                    updateButton.setEnabled(false);
                }
                else {
                    updateButton.setEnabled(true);
                }
            }
        });
    }

    private void initialiseVM() {
        viewModel.updateHandler = new SSFirebaseHandler() {
            @Override
            public void completionHandler(Boolean success, String error) {
                progressDialog.dismiss();
                if (success) {
                    finish();
                } else {
                    Toast.makeText(context, error, Toast.LENGTH_LONG).show();
                }
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        String image = CurrentUser.shared.getCurrentUser().getImage();
        if (!image.equals("")) {
            Glide.with(context).load(image).into(settingsImage);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        if (requestCode == SSConstants.GALLERY_REQUEST_CODE && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();
            CropImage.activity(imageUri)
                    .setAspectRatio(1,1)
                    .start(this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), resultUri);
                    viewModel.setUserProfileImage(bitmap);
                    settingsImage.setImageBitmap(bitmap);
                } catch (Exception e) {
                    Log.d("Error", e.getMessage());
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
        else { return; }
    }

    public void updateClick(View v) {
        progressDialog = ProgressDialog.show(this, "Updating Information...", "Please wait...", true);
        newPass = settingsPassword.getText().toString();
        newEmail = settingsEmail.getText().toString();
        newPhone = settingsPhone.getText().toString();
        newAddress = settingsAddress.getText().toString();
        viewModel.update(context, newEmail, newPass, newPhone, newAddress);
    }

    public void logoutClick(View v) {
        new AlertDialog.Builder(context)
            .setTitle("Are you sure you want to Logout?")
            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    HomeLastProduct.shared.setHomeLastProduct(null);
                    CurrentUser.shared.removeCurrentUserEmail(context);
                    CurrentUser.shared.removeCurrentUserPassword(context);
                    SSFirebaseManager.firebaseAuth.signOut();
                    CurrentUser.shared.setCurrentUser(null);
                    CurrentUser.shared.saveCurrentUser(context, null);
                    Intent intent = new Intent(context, SSWelcomeView.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
                }
            })
            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            }).show();
    }

    public void onFollowersClick(View view) {
        Intent intent = new Intent(context , SSFollowersView.class);
        intent.putExtra(SSConstants.FOLLOWER_KEY, CurrentUser.shared.getCurrentUser().getUserid());
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
    }

    public void onFollowingClick(View view) {
        Intent intent = new Intent(context , SSFollowingView.class);
        intent.putExtra(SSConstants.FOLLOWING_KEY, CurrentUser.shared.getCurrentUser().getUserid());
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
    }
}
