package com.sellspot.app.Activities.Signup;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.View;
import android.util.Patterns;
import android.widget.Button;
import android.content.Intent;
import android.widget.EditText;
import android.content.Context;

import com.sellspot.app.Activities.Authenitication.SSAuthenticationView;
import com.sellspot.app.Handlers.SSFirebaseHandler;
import com.sellspot.app.R;
import com.sellspot.app.Constants.SSConstants;


import androidx.appcompat.app.AppCompatActivity;

public class SSSignupView extends AppCompatActivity {

    private Context context;

    private Button signupButton;

    private EditText signupEmail;
    private EditText signupUsername;
    private EditText signupPassword;
    private SSSignupVM viewModel = new SSSignupVM();

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_view);
        initUIAttributes();
        initialiseVM();
    }

    private void initUIAttributes() {
        this.getSupportActionBar().hide();
        signupEmail = (EditText) findViewById(R.id.signupEmail);
        signupUsername = (EditText) findViewById(R.id.signupUsername);
        signupPassword = (EditText) findViewById(R.id.signupPassword);
        signupButton = (Button) findViewById(R.id.signupButton);
        context = this;
        signupUsername.setFilters(new InputFilter[] {new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                return source.toString().toLowerCase().replace(' ','_');
            }
        }});
    }

    public void signupClick(View v) {
        progressDialog = ProgressDialog.show(this, "", "Please wait...", true);
        if (!checkFields()) {
            progressDialog.dismiss();
            return;
        }
        viewModel.checkEmail(signupEmail.getText().toString());
    }

    private Boolean checkFields() {
        Boolean alright = true;

        if (signupEmail.getText().toString().length() < 1) {
            signupEmail.setError("Email cannot be empty");
            alright = false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(signupEmail.getText().toString()).matches()) {
            signupEmail.setError("Please enter a valid email address");
            alright = false;
        }

        if (signupUsername.getText().toString().length() < 1) {
            signupUsername.setError("Username cannot be empty");
            alright = false;
        }

        if (signupPassword.getText().toString().length() < 1) {
            signupPassword.setError("Password cannot be empty");
            alright = false;
        }

        if (signupPassword.getText().toString().length() < 6) {
            signupPassword.setError("Password should be atleast 6 characters");
            alright = false;
        }

        return alright;
    }

    private void proceedToAuthentication() {
        Intent i = new Intent(context, SSAuthenticationView.class);
        i.putExtra(SSConstants.signupEmail,signupEmail.getText().toString());
        i.putExtra(SSConstants.signupPassword,signupPassword.getText().toString());
        i.putExtra(SSConstants.signupUsername,signupUsername.getText().toString());
        startActivity(i);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
    }

    private void initialiseVM() {
        viewModel.emailHandler = new SSFirebaseHandler() {
            @Override
            public void completionHandler(Boolean success, String error) {
                if (success) {
                    viewModel.checkUsername(signupUsername.getText().toString());

                } else {
                    progressDialog.dismiss();
                    signupEmail.setError(error);
                }
            }
        };

        viewModel.usernameHandler = new SSFirebaseHandler() {
            @Override
            public void completionHandler(Boolean success, String error) {
                progressDialog.dismiss();
                if (success) {
                    proceedToAuthentication();

                } else {
                    signupUsername.setError(error);

                }
            }
        };
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
    }
}
