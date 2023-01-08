package com.sellspot.app.Activities.Login;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.content.Intent;
import android.widget.EditText;
import android.content.Context;
import android.app.ProgressDialog;

import com.sellspot.app.Activities.MainActivity.SSMainActivity;
import com.sellspot.app.Activities.Signup.SSSignupView;
import com.sellspot.app.Activities.ResetPassword.SSResetPasswordView;
import com.sellspot.app.AdminDirectory.Activities.MainAdminView;
import com.sellspot.app.Handlers.SSFirebaseHandler;
import com.sellspot.app.R;
import com.sellspot.app.Shared.CurrentUser;

import androidx.appcompat.app.AppCompatActivity;
import android.widget.Toast;

public class SSLoginView extends AppCompatActivity {

    private Button loginButton;

    private EditText loginEmail;
    private EditText loginPassword;

    private Context context;

    private ProgressDialog progressDialog;

    private SSLoginVM viewModel = new SSLoginVM();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_view);
        initUIAttributes();
        initialiseVM();
    }

    private void initUIAttributes() {
        this.getSupportActionBar().hide();
        loginEmail = (EditText) findViewById(R.id.loginEmail);
        loginPassword = (EditText) findViewById(R.id.loginPassword);
        loginButton = (Button) findViewById(R.id.loginButton);
        context = this;
    }

    private Boolean checkFields() {
        Boolean alright = true;
        if(loginEmail.getText().toString().length() < 1) {
            loginEmail.setError("Email cannot be Empty");
            alright = false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(loginEmail.getText().toString()).matches()) {
            loginEmail.setError("Please enter a valid email address");
            alright = false;
        }

        if(loginPassword.getText().toString().length() < 1) {
            loginPassword.setError("Password cannot be Empty");
            alright = false;
        }
        return alright;
    }

    private void proceedToMainActivity() {
        Intent i = new Intent(context, SSMainActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
    }

    public void signupClick(View v) {
        Intent intent = new Intent(this, SSSignupView.class);
        startActivity(intent);
    }

    public void forgotPasswordClick(View v){
        Intent intent = new Intent(this, SSResetPasswordView.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
    }

    private void initialiseVM() {
        viewModel.loginHandler = new SSFirebaseHandler() {
            @Override
            public void completionHandler(Boolean success, String error) {
                progressDialog.dismiss();
                if (success) {
                    viewModel.updatePassword(loginPassword.getText().toString());
                    CurrentUser.shared.setCurrentUserEmail(context, loginEmail.getText().toString());
                    CurrentUser.shared.setCurrentUserPassword(context, loginPassword.getText().toString());
                    proceedToMainActivity();
                    if(CurrentUser.shared.getIsGuest(getBaseContext())==true)
                    {
                        viewModel.uploadGuestUserCart();
                    }
                } else {
                    Toast.makeText(context,error,Toast.LENGTH_LONG).show();
                }
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

    public void loginClick(View v) {
        if(loginEmail.getText().toString().equals("sellspotadmin") && loginPassword.getText().toString().equals("password@sellspot")) {
            CurrentUser.shared.setCurrentUserEmail(context, "sellspotadmin");
            CurrentUser.shared.setCurrentUserPassword(context, "password@sellspot");
            Intent intent = new Intent(this, MainAdminView.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
        } else {
            progressDialog = ProgressDialog.show(this, "Logging in...", "Please wait...", true);
            if (!checkFields()) {
                progressDialog.dismiss();
                return;
            }
            viewModel.loginUser(loginEmail.getText().toString(), loginPassword.getText().toString(), context);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
    }
}