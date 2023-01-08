package com.sellspot.app.AdminDirectory.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sellspot.app.Handlers.SSUserHandler;
import com.sellspot.app.Models.User;
import com.sellspot.app.Networking.SSFirestoreManager;
import com.sellspot.app.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserInfo extends AppCompatActivity {

    CircleImageView userimageinfo;
    TextView useridinfo;
    TextView usernameinfo;
    TextView userpasswordinfo;
    TextView useremailinfo;
    TextView usercontactinfo;
    TextView useraddressinfo;
    TextView usertime;
    Context context;

    String userid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        initUIAttributes();
        fetchData();
    }

    void initUIAttributes() {
        this.getSupportActionBar().hide();
        context = this;
        userimageinfo = (CircleImageView) findViewById(R.id.userimageinfo);
        useridinfo = (TextView) findViewById(R.id.useridinfo);
        usernameinfo = (TextView) findViewById(R.id.usernameinfo);
        userpasswordinfo = (TextView) findViewById(R.id.userpasswordinfo);
        useremailinfo = (TextView) findViewById(R.id.useremailinfo);
        usercontactinfo = (TextView) findViewById(R.id.usercontactinfo);
        useraddressinfo = (TextView) findViewById(R.id.useraddressinfo);
        usertime = (TextView) findViewById(R.id.usertime);
        userid = getIntent().getStringExtra("useridforinfo");
    }

    void fetchData() {
        SSFirestoreManager.firestoreHelper.getUserProfile(userid, new SSUserHandler() {
            @Override
            public void completionHandler(Boolean success, User user, String error) {
                if (success) {
                    useridinfo.setText(user.getUserid());
                    usernameinfo.setText(user.getUsername());
                    userpasswordinfo.setText(user.getPassword());
                    useremailinfo.setText(user.getEmail());
                    usercontactinfo.setText(user.getPhonenumber());
                    useraddressinfo.setText(user.getAddress());
                    usertime.setText(user.getTime());
                    if (!user.getImage().equals("")) {
                        Glide.with(context).load(user.getImage()).into(userimageinfo);
                    }
                }
            }
        });
    }
}