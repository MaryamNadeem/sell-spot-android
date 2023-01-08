package com.sellspot.app.Activities.Welcome;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.sellspot.app.Activities.Login.SSLoginView;
import com.sellspot.app.Activities.MainActivity.SSMainActivity;
import com.sellspot.app.Activities.Signup.SSSignupView;
import com.sellspot.app.Models.GuestUser;
import com.sellspot.app.R;
import com.sellspot.app.Shared.CurrentUser;

import java.io.IOException;

public class SSWelcomeView extends AppCompatActivity {

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_s_s_welcome_view);
        initUIAttributes();
    }

    private void initUIAttributes() {
        this.getSupportActionBar().hide();
        context = this;
    }

    public void onLoginClick(View v) {
        Intent intent = new Intent(this, SSLoginView.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
    }

    public void onSignupClick(View v) {
        Intent intent = new Intent(this, SSSignupView.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
    }

    private void proceedToMainActivity() {
        Intent i = new Intent(context, SSMainActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
    }

    public void onSkipClick(View v) throws IOException, ClassNotFoundException {
        CurrentUser.shared.setGuest(context, true);
        GuestUser.sharedGuest.readFromFile(context);
        proceedToMainActivity();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }
}