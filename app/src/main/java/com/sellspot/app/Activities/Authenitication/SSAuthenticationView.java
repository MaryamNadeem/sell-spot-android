package com.sellspot.app.Activities.Authenitication;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Patterns;
import android.view.View;
import android.content.Intent;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sellspot.app.Activities.Address.SSAddressView;
import com.sellspot.app.Handlers.SSCompletionHandler;
import com.sellspot.app.Handlers.SSFirebaseHandler;
import com.sellspot.app.Models.User;
import com.sellspot.app.Shared.CurrentUser;
import com.sellspot.app.Shared.SharedFunctions;

import com.sellspot.app.R;

import androidx.appcompat.app.AppCompatActivity;
import com.sellspot.app.Constants.SSConstants;

public class SSAuthenticationView extends AppCompatActivity {

    private EditText code;
    private EditText phoneNumber;
    private TextView headingLabel;
    private LinearLayout codeView;
    private LinearLayout phoneView;
    private TextView phoneNumberLabel;

    boolean doubleBackToExitPressedOnce = false;

    private Context context;
    private SSAuthenticationVM viewModel = new SSAuthenticationVM();
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.authentication_view);
        initUIAttribites();
        initialiseVM();
    }

    private void initUIAttribites() {
        code = (EditText) findViewById(R.id.code);
        phoneNumber = (EditText) findViewById(R.id.phoneNumber);
        headingLabel = (TextView) findViewById(R.id.headingLabel);
        codeView = (LinearLayout) findViewById(R.id.codeView);
        phoneView = (LinearLayout) findViewById(R.id.phoneView);
        phoneNumberLabel = (TextView) findViewById(R.id.phoneNumberLabel);
        context = this;
        this.getSupportActionBar().hide();
    }

    public void createUser() {
        User user = new User("", getIntent().getExtras().getString(SSConstants.signupEmail), "", "","", getIntent().getExtras().getString(SSConstants.signupPassword),"+92"+ phoneNumber.getText().toString(), SharedFunctions.getCurrentTime(), "", getIntent().getExtras().getString(SSConstants.signupUsername));
        viewModel.createNewUser(user, context);
    }

    private void proceedToProfilePictureUpload() {
        Intent intent = new Intent(this , SSAddressView.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
    }

    public void onSendCodeClick(View view) {
        if (!Patterns.PHONE.matcher("+92" +phoneNumber.getText().toString()).matches()) {
            phoneNumber.setError("Please enter a vaild Phone Number");
            return;
        }

        if (phoneNumber.getText().toString().length() < 1) {
            phoneNumber.setError("Phone Number cannot be empty");
            return;
        }

        progressDialog = ProgressDialog.show(context, "Sending Code", "Please wait...", true);

        viewModel.sendVerificationCode("+92" + phoneNumber.getText().toString(), this);
    }

    public void onResendCodeClick(View view) {
        progressDialog = ProgressDialog.show(context, "Sending Code", "Please wait...", true);
        viewModel.resendVerificationCode(phoneNumber.getText().toString(), this);
    }

    public void onVerifyCheckClick(View view) {
        progressDialog = ProgressDialog.show(context, "Creating account...", "Please wait...", true);
        String codeNumber = code.getText().toString();
        viewModel.verifyPhoneNumber(codeNumber);
    }

    private void initialiseVM() {
        viewModel.signupHandler = new SSFirebaseHandler() {
            @Override
            public void completionHandler(Boolean success, String error) {
                progressDialog.dismiss();
                if (success) {
                    CurrentUser.shared.setCurrentUserEmail(context, getIntent().getExtras().getString(SSConstants.signupEmail));
                    CurrentUser.shared.setCurrentUserPassword(context, getIntent().getExtras().getString(SSConstants.signupPassword));
                    proceedToProfilePictureUpload();
                    if(CurrentUser.shared.getIsGuest(getBaseContext())==true)
                    {
                        viewModel.uploadGuestUserCart();
                    }
                } else {
                    Toast.makeText(context,error,Toast.LENGTH_LONG).show();
                }
            }
        };

        viewModel.verificationHandler = new SSFirebaseHandler() {
            @Override
            public void completionHandler(Boolean success, String error) {
                if (success) {
                    createUser();
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(context,"Phone Number Verification Failed",Toast.LENGTH_LONG).show();
                }
            }
        };

        viewModel.failureHandler = new SSCompletionHandler() {
            @Override
            public void completionHandler(Boolean success, String error) {
                progressDialog.dismiss();
                Toast.makeText(context, error, Toast.LENGTH_LONG).show();
            }
        };

        viewModel.autoDetectHandler = new SSCompletionHandler() {
            @Override
            public void completionHandler(Boolean success, String error) {
                progressDialog = ProgressDialog.show(context, "Creating account...", "Please wait...", true);
                phoneView.setVisibility(View.INVISIBLE);
                headingLabel.setText("Enter Code");
                phoneNumberLabel.setText("* Enter the code sent at " + phoneNumber.getText().toString());
                codeView.setVisibility(View.VISIBLE);
                code.setText(error);
            }
        };

        viewModel.successHandler = new SSCompletionHandler() {
            @Override
            public void completionHandler(Boolean success, String error) {
                progressDialog.dismiss();
                headingLabel.setText("Enter Code");
                phoneView.setVisibility(View.INVISIBLE);
                phoneNumberLabel.setText("* Enter the code sent at " + phoneNumber.getText().toString());
                codeView.setVisibility(View.VISIBLE);
            }
        };
        viewModel.uploadGuestCartHandler = new SSFirebaseHandler() {
            @Override
            public void completionHandler(Boolean success, String error) {
                if(success)
                {


                }
            }
        };
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
