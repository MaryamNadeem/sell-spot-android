package com.sellspot.app.Activities.ResetPassword;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.sellspot.app.Handlers.SSFirebaseHandler;
import com.sellspot.app.R;

public class SSResetPasswordView extends AppCompatActivity {

    private Button sendEmailButton;
    private EditText resetEmailText;
    private Context context;
    private SSResetPasswordVM viewModel = new SSResetPasswordVM();
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reset_password_view);
        initAttributes();
        initialiseVM();
    }

    private void initAttributes() {
        this.getSupportActionBar().hide();
        context = this;
        sendEmailButton = (Button) findViewById(R.id.resetEmailButton);
        resetEmailText = (EditText) findViewById(R.id.resetEmail);

    }

    public void onSendEmailClick(View v) {
        if(resetEmailText.getText().toString().length() < 1) {
            resetEmailText.setError("Email cannot be Empty");
            return;
        }
        progressDialog = ProgressDialog.show(this, "Processing...", "Please wait...", true);
        viewModel.resetPassword(resetEmailText.getText().toString());
    }

    private void initialiseVM() {
        viewModel.resetPasswordHandler = new SSFirebaseHandler() {
            @Override
            public void completionHandler(Boolean success, String error) {
                progressDialog.dismiss();
                if (success) {
                    Toast.makeText(context, "Password reset email sent successfully!", Toast.LENGTH_LONG).show();
                    onBackPressed();
                } else {
                    Toast.makeText(context, error, Toast.LENGTH_LONG).show();
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
