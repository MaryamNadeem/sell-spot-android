package com.sellspot.app.Activities.Splash;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.sellspot.app.Activities.Login.SSLoginVM;
import com.sellspot.app.Activities.MainActivity.SSMainActivity;
import com.sellspot.app.Activities.Welcome.SSWelcomeView;
import com.sellspot.app.AdminDirectory.Activities.MainAdminView;
import com.sellspot.app.Constants.SSFirestoreConstants;
import com.sellspot.app.Handlers.SSCompletionHandler;
import com.sellspot.app.Handlers.SSFirebaseHandler;
import com.sellspot.app.Models.GuestUser;
import com.sellspot.app.Networking.SSFirebaseDynamicLinksManager;
import com.sellspot.app.Networking.SSFirestoreManager;
import com.sellspot.app.R;
import com.sellspot.app.Shared.CurrentUser;

import java.io.IOException;

public class SSSplashVC extends AppCompatActivity {

    private Context context = this;
    private SSLoginVM viewModel = new SSLoginVM();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
        initUIAttributes();
        initialiseVM();
        SSFirestoreConstants.PACKAGE_NAME = getApplicationContext().getPackageName();
        SSFirestoreConstants.dynamicLinkHost = SSFirestoreConstants.PACKAGE_NAME.equals("com.sellspot.app.production") ? "sellspotapp.page.link" : "sellspotstaging.page.link";
        SSFirestoreConstants.dynamicLinkURL = SSFirestoreConstants.PACKAGE_NAME.equals("com.sellspot.app.production") ? "https://sellspotapp.page.link" : "https://sellspotstaging.page.link";
        Log.i("SSSplashVC", SSFirestoreConstants.PACKAGE_NAME);
        Log.i("SSSplashVC", SSFirestoreConstants.dynamicLinkHost);
        Log.i("SSSplashVC", SSFirestoreConstants.dynamicLinkURL);
        handleDynamicLink(new SSCompletionHandler() {
            @Override
            public void completionHandler(Boolean success, String error) {
                if (success) {
                    try {
                        autoLogin();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

    }

    private void initUIAttributes() {
        this.getSupportActionBar().hide();
    }

    private void autoLogin() throws IOException, ClassNotFoundException {
        final String email = CurrentUser.shared.getCurrentUserEmail(getBaseContext());
        final String password = CurrentUser.shared.getCurrentUserPassword(getBaseContext());
        final Boolean isGuest = CurrentUser.shared.getIsGuest(getBaseContext());
        if(email != null && password != null && email != "" && password != "") {
            Log.i("SplashVC", email + " " +password);
            if(email.equals("sellspotadmin") && password.equals("password@sellspot")) {
                Intent intent = new Intent(this, MainAdminView.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
            } else {
                viewModel.loginUser(email, password, context);
            }
        }
        else if(isGuest == true){
            CurrentUser.shared.setGuest(context, true);
            GuestUser.sharedGuest.readFromFile(context);
            proceedToMainActivity();
        }
        else {
            Intent intent = new Intent(this, SSWelcomeView.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
        }
    }
    private void initialiseVM() {
        viewModel.loginHandler = new SSFirebaseHandler() {
            @Override
            public void completionHandler(Boolean success, String error) {
                if (success) {
                    proceedToMainActivity();
                } else {
                    Intent intent = new Intent(context, SSWelcomeView.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
                    Toast.makeText(context,error,Toast.LENGTH_LONG).show();
                }
            }
        };
    }

    private void proceedToMainActivity() {
        Intent i = new Intent(context, SSMainActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
    }

    private void handleDynamicLink(SSCompletionHandler completionHandler) {
        SSFirebaseDynamicLinksManager.shared.handleDynamicLinks(getIntent(), new SSCompletionHandler() {
            @Override
            public void completionHandler(Boolean success, String error) {
                Log.i("SplashVC", "Coming in completion handler");
                if (success) {
                    Log.i("SplashVC", "Coming in success");
                    final String email = CurrentUser.shared.getCurrentUserEmail(getBaseContext());
                    final String password = CurrentUser.shared.getCurrentUserPassword(getBaseContext());
                    if(email != null && password != null && email != "" && password != "") {
                        // set Current User here somehow
                        if (CurrentUser.shared.getCUser(context) != null) {
                            CurrentUser.shared.setCurrentUser(CurrentUser.shared.getCUser(context));
                            proceedToMainActivity();
                        } else {
                            try {
                                CurrentUser.shared.saveCurrentUser(context, null);
                                CurrentUser.shared.setCurrentUser(null);
                                CurrentUser.shared.removeCurrentUserPassword(context);
                                CurrentUser.shared.removeCurrentUserEmail(context);
                                setAsGuestUser();
                                proceedToMainActivity();
                            } catch (Exception e) {
                            }
                        }
                    } else {
                        Log.i("SplashVC", "Continuing as guest");
                        try {
                            setAsGuestUser();
                            proceedToMainActivity();
                        } catch (Exception e) {
                        }
                    }
                } else {
                    completionHandler.completionHandler(true,"");
                }
            }
        });
    }

    private void setAsGuestUser() throws IOException, ClassNotFoundException {
        CurrentUser.shared.setGuest(context, true);
        GuestUser.sharedGuest.readFromFile(context);
    }
}