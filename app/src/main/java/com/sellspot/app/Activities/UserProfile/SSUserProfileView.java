package com.sellspot.app.Activities.UserProfile;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import android.widget.Button;
import android.content.Intent;
import android.content.Context;
import android.widget.GridView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sellspot.app.R;
import com.sellspot.app.Models.User;
import com.sellspot.app.Models.Product;
import androidx.appcompat.app.AppCompatActivity;
import com.sellspot.app.Shared.CurrentUser;
import de.hdodenhof.circleimageview.CircleImageView;
import com.sellspot.app.Constants.SSConstants;
import com.sellspot.app.Handlers.SSUserHandler;
import com.sellspot.app.Handlers.SSPostsHandler;
import com.sellspot.app.Handlers.SSFirebaseHandler;
import com.sellspot.app.Handlers.SSFollowersHandler;
import com.sellspot.app.Models.FollowerAndFollowing;
import com.sellspot.app.Handlers.SSFollowingsHandler;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.sellspot.app.Adapters.SSUserProfileAdapter;
import com.sellspot.app.Activities.Followers.SSFollowersView;
import com.sellspot.app.Activities.Following.SSFollowingView;

public class SSUserProfileView extends AppCompatActivity {

    private CircleImageView userProfileImage;
    private TextView userProfileUsername;
    private Button userProfileFollowersCount;
    private Button userProfileFollowingCount;
    private Button followUnfollowButton;
    private GridView userProfileGridView;
    private SSUserProfileAdapter userProfileAdapter;
    private String username;
    private String userid;
    private Context context;

    private SSUserProfileVM viewModel = new SSUserProfileVM();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_profile_view);
        initUIAttributes();
        initialiseVM();
    }

    @Override
    protected void onResume() {
        super.onResume();
        viewModel.getUserImage(userid);
        viewModel.setUserProfilePosts(userid);
        if(CurrentUser.shared.getIsGuest(getBaseContext()) == false) {
            viewModel.getFollowStatus(userid);
        }
        else{
            followUnfollowButton.setVisibility(View.INVISIBLE);
        }
        viewModel.getFollowers(userid);
        viewModel.getFollowings(userid);
    }

    private void initUIAttributes() {
        this.getSupportActionBar().hide();
        userProfileImage = (CircleImageView) findViewById(R.id.userProfileImage);
        userProfileUsername = (TextView) findViewById(R.id.userProfileUsername);
        userProfileFollowersCount = (Button) findViewById(R.id.userProfileFollowersCount);
        userProfileFollowingCount = (Button) findViewById(R.id.userProfileFollowingCount);
        followUnfollowButton = (Button) findViewById(R.id.followUnfollowButton);
        userProfileGridView = (GridView) findViewById(R.id.userProfileGridView);
        userid = getIntent().getExtras().getString(SSConstants.USERID_KEY);
        final SwipeRefreshLayout pullToRefresh =(SwipeRefreshLayout) findViewById(R.id.user_profile_swipe_container);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                viewModel.setUserProfilePosts(userid);
                viewModel.getUserImage(userid);
                if(CurrentUser.shared.getIsGuest(getBaseContext()) == false) {
                    viewModel.getFollowStatus(userid);
                }
                else{
                    followUnfollowButton.setVisibility(View.INVISIBLE);
                }
                viewModel.getFollowers(userid);
                viewModel.getFollowings(userid);
                pullToRefresh.setRefreshing(false);
            }
        });
        if(CurrentUser.shared.getIsGuest(getBaseContext()) == false)
        {
            if(CurrentUser.shared.getCurrentUser().getUserid().equals(userid)) {
                followUnfollowButton.setVisibility(View.INVISIBLE);
            }
        }
        context = this;
    }

    private void setUserImage(String profileImage) {
        if (!profileImage.equals("")) {
            Glide.with(context).load(profileImage).into(userProfileImage);
        }
    }

    private void setFollowStatus(String status) {
        followUnfollowButton.setText(status);
    }

    public void onFollowersClick(View view) {
        Intent intent = new Intent(context , SSFollowersView.class);
        intent.putExtra(SSConstants.FOLLOWER_KEY, userid);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
    }

    public void onFollowingClick(View view) {
        Intent intent = new Intent(context , SSFollowingView.class);
        intent.putExtra(SSConstants.FOLLOWING_KEY, userid);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
    }

    public void onButtonClick(View v) {
        if(followUnfollowButton.getText().toString().equals(SSConstants.followUser)) {
            viewModel.followUser(userid, username);
        } else if(followUnfollowButton.getText().toString().equals(SSConstants.followingUser)) {
            viewModel.unFollowUser(userid);
        }
    }

    private void initialiseVM() {
        viewModel.getUserHandler = new SSUserHandler() {
            @Override
            public void completionHandler(Boolean success, User user, String error) {
                if (success) {
                    if (!(user.getImage().equals(""))) {
                        username = user.getUsername();
                        setUserImage(user.getImage());
                    }
                    userProfileUsername.setText(user.getUsername());
                } else {
                    Toast.makeText(context, error, Toast.LENGTH_LONG).show();
                }
            }
        };

        viewModel.getPostsHandler = new SSPostsHandler() {
            @Override
            public void completionHandler(Boolean success, List<Product> products, String error) {
                if (success) {
                    userProfileAdapter = new SSUserProfileAdapter((Activity)context, context, products);
                    userProfileGridView.setAdapter(userProfileAdapter);
                } else {
                    Toast.makeText(context, error, Toast.LENGTH_LONG).show();
                }
            }
        };

        viewModel.statusHandler = new SSFirebaseHandler() {
            @Override
            public void completionHandler(Boolean success, String error) {
                if (success) {
                    setFollowStatus(SSConstants.followingUser);
                } else {
                    setFollowStatus(SSConstants.followUser);
                }
            }
        };

        viewModel.followHandler = new SSFirebaseHandler() {
            @Override
            public void completionHandler(Boolean success, String error) {
                if (success) {
                    setFollowStatus(SSConstants.followingUser);
                } else {
                    Toast.makeText(context, error, Toast.LENGTH_LONG).show();
                }
            }
        };

        viewModel.unfollowHandler = new SSFirebaseHandler() {
            @Override
            public void completionHandler(Boolean success, String error) {
                if (success) {
                    setFollowStatus(SSConstants.followUser);
                } else {
                    Toast.makeText(context, error, Toast.LENGTH_LONG).show();
                }
            }
        };

        viewModel.getFollowerHandler = new SSFollowersHandler() {
            @Override
            public void completionHandler(Boolean success, List<FollowerAndFollowing> followers, String error) {
                if(success) {
                    userProfileFollowersCount.setText(followers.size() + " Followers");
                }
            }
        };

        viewModel.getFollowingsHandler = new SSFollowingsHandler() {
            @Override
            public void completionHandler(Boolean success, List<FollowerAndFollowing> followings, String error) {
                if(success)
                {
                    userProfileFollowingCount.setText(followings.size() + " Following");
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
