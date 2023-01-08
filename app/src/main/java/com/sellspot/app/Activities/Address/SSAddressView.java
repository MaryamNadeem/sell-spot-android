package com.sellspot.app.Activities.Address;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.sellspot.app.Activities.UploadImage.SSUploadImageView;
import com.sellspot.app.Constants.SSConstants;
import com.sellspot.app.Handlers.SSFirebaseHandler;
import com.sellspot.app.R;

public class SSAddressView extends AppCompatActivity {

    private Context context;
    private EditText onboardingAddress;

    private SSAddressVM viewModel = new SSAddressVM();

    private ProgressDialog progressDialog;

    boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_s_s_address_view);
        initUIAttributes();
        initialiseVM();
    }

    private void initUIAttributes() {
        this.getSupportActionBar().hide();
        context = this;
        onboardingAddress = (EditText) findViewById(R.id.onboardingAddress);
    }

    private void initialiseVM() {
        viewModel.updateHandler = new SSFirebaseHandler() {
            @Override
            public void completionHandler(Boolean success, String error) {
                progressDialog.dismiss();
                proceedToImageUpload();
            }
        };
    }

    private void proceedToImageUpload() {
        Intent intent = new Intent(this , SSUploadImageView.class);
        intent.putExtra(SSConstants.IMAGE_KEY, SSConstants.PROFILE_PICTURE_VALUE);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
    }

    public void onSubmitClick(View view)  {
        progressDialog = ProgressDialog.show(this, "Updating Address", "Please wait...", true);
        if(onboardingAddress.getText().toString().length() < 1) {
            onboardingAddress.setError("Address cannot be Empty");
            progressDialog.dismiss();
            return;
        }
        viewModel.updateUserAddress(onboardingAddress.getText().toString());
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            finishAffinity();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }
}