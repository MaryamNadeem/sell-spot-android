package com.sellspot.app.Activities.MainActivity;

import android.app.Activity;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.os.Bundle;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.Intent;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.GridView;
import android.content.Context;

import com.sellspot.app.Activities.AboutUsInfo.SSAboutUsInfoView;
import com.sellspot.app.Activities.AddNewItem.SSAddNewItemView;
import com.sellspot.app.Activities.Login.SSLoginView;
import com.sellspot.app.Activities.MainActivity.Cart.SSCartView;
import com.sellspot.app.Activities.MainActivity.Home.SSHomeView;
import com.sellspot.app.Activities.MainActivity.Profile.SSProfileView;
import com.sellspot.app.Activities.Search.SSSearchView;
import com.sellspot.app.Activities.Settings.SSSettingsView;
import com.sellspot.app.Activities.Signup.SSSignupView;
import com.sellspot.app.Activities.UserImage.SSUserImageView;
import com.sellspot.app.Adapters.SSAboutUsInfoAdapter;
import com.sellspot.app.Adapters.SSNewHomeAdapter;
import com.sellspot.app.Constants.SSFirestoreConstants;
import com.sellspot.app.Handlers.SSCartProductHandler;
import com.sellspot.app.Handlers.SSCompletionHandler;
import com.sellspot.app.Handlers.SSPostsHandler;
import com.sellspot.app.Handlers.SSCartHandler;
import com.sellspot.app.Models.CartPerStore;
import com.sellspot.app.Models.GuestUser;
import com.sellspot.app.Models.Product;
import com.sellspot.app.Networking.SSFirebaseDynamicLinksManager;
import com.sellspot.app.Networking.SSFirestoreManager;
import com.sellspot.app.R;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;

import com.sellspot.app.Shared.CurrentUser;
import com.sellspot.app.Shared.HomeLastProduct;
import com.sellspot.app.Shared.SharedVariables;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.tabs.TabLayout;
import com.sellspot.app.Adapters.SSCartAdapter;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.sellspot.app.Adapters.SSProfileAdapter;
import com.sellspot.app.Models.SSSwipeOffViewPager;
import com.sellspot.app.Adapters.SSMainActivityPagerAdapter;

public class SSMainActivity extends AppCompatActivity implements SSHomeView.OnFragmentInteractionListener, SSCartView.OnFragmentInteractionListener, SSProfileView.OnFragmentInteractionListener, SSAboutUsInfoView.OnFragmentInteractionListener {

    // self attributes
    private Context context = this;
    private TabLayout tabLayout;
    private TabLayout.Tab home;
    private TabLayout.Tab cart;
    private TabLayout.Tab profile;
    private TabLayout.Tab aboutApp;
    private ArrayList<TabLayout.Tab> tabIdHistory = new ArrayList<TabLayout.Tab>();
    private SSSwipeOffViewPager SSSwipeOffViewPager;
    private com.sellspot.app.Adapters.SSMainActivityPagerAdapter SSMainActivityPagerAdapter;

    //homeattributes
    private GridView homeGridView;
    private ImageButton searchButton;
    private TextView noProductsLabel;
    private SSNewHomeAdapter homeAdapter;
    private SwipeRefreshLayout homerefresh;

    //profile attributes
    private ImageButton settingsButton;
    private GridView profileGridView;
    private SSProfileAdapter profileAdapter;
    private FloatingActionButton addNewItem;
    private TextView noProfileProductsLabel;

    //cart attributes
    private RecyclerView cartRecyclerView;
    private SSCartAdapter cartAdapter;
    private TextView noCartProductsLabel;
    private ProgressBar cartProgress;

    //aboutus attributes
    private RecyclerView infoRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_view);
        initUIAttributes();
        setTabLayout();
        setViewPager();
        setTabChangeListener();
        if (SSFirebaseDynamicLinksManager.shared.isLinkReceived == true) {
            handleDynamicLink();
        }
    }

    void handleDynamicLink() {
        String productID = SSFirebaseDynamicLinksManager.shared.receivedLink.getQueryParameter(SSFirestoreConstants.productid);
        String userID = SSFirebaseDynamicLinksManager.shared.receivedLink.getQueryParameter(SSFirestoreConstants.userid);
        Intent intent = new Intent(context, SSUserImageView.class);
        intent.putExtra("produserid", userID);
        intent.putExtra("prodid", productID);
        SSFirebaseDynamicLinksManager.shared.removeDynamicLinkData();
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
    }

    public void initUIAttributes() {
        this.getSupportActionBar().hide();
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.bringToFront();
        SSSwipeOffViewPager = (SSSwipeOffViewPager) findViewById(R.id.viewPager);

        SharedVariables.shared.increaseCartCount = new SSCompletionHandler() {
            @Override
            public void completionHandler(Boolean success, String error) {
                View cartTabView = tabLayout.getTabAt(1).getCustomView();
                TextView countLabel = cartTabView.findViewById(R.id.cartCount);
                String currentCount = countLabel.getText().toString();
                Integer updatedCount = Integer.parseInt(currentCount);
                countLabel.setText((updatedCount+1) + "");
            }
        };

        SharedVariables.shared.decreaseCartCount = new SSCompletionHandler() {
            @Override
            public void completionHandler(Boolean success, String error) {
                View cartTabView = tabLayout.getTabAt(1).getCustomView();
                TextView countLabel = cartTabView.findViewById(R.id.cartCount);
                String currentCount = countLabel.getText().toString();
                Integer updatedCount = Integer.parseInt(currentCount);
                countLabel.setText((updatedCount-1) + "");
            }
        };

        SharedVariables.shared.updateCartCount = new SSCompletionHandler() {
            @Override
            public void completionHandler(Boolean success, String error) {
                if(CurrentUser.shared.getIsGuest(getBaseContext()) == false) {
                    setUpCartCount();
                }
            }
        };

        SharedVariables.shared.changeTab = new SSCompletionHandler() {
            @Override
            public void completionHandler(Boolean success, String error) {
                tabLayout.selectTab(home);
            }
        };
    }

    public void setTabLayout() {
        home = tabLayout.newTab();
        cart = tabLayout.newTab();
        profile = tabLayout.newTab();
        aboutApp = tabLayout.newTab();

        tabLayout.setSelectedTabIndicatorColor(ContextCompat.getColor(this, R.color.appthemecolor));

        tabLayout.addTab(home);
        tabLayout.addTab(cart);
        tabLayout.addTab(aboutApp);
        tabLayout.addTab(profile);

        tabIdHistory.add(home);

    }

    @Override
    public void onBackPressed() {
        if (tabIdHistory.size() > 0) {
            tabIdHistory.remove(tabIdHistory.size() - 1);
            if(tabIdHistory.size() > 0) {
                tabIdHistory.get(tabIdHistory.size()-1).select();
            } else {
                FragmentManager mgr = getSupportFragmentManager();
                if (mgr.getBackStackEntryCount() == 0) {
                    super.onBackPressed();
                    finishAffinity();
                } else {
                    mgr.popBackStack();
                }
            }
        } else {
            super.onBackPressed();
            finishAffinity();
        }
    }

    public void setViewPager() {
        SSMainActivityPagerAdapter = new SSMainActivityPagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount(), context);
        SSSwipeOffViewPager.setAdapter(SSMainActivityPagerAdapter);
        SSSwipeOffViewPager.setPagingEnabled(false);
        SSSwipeOffViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            tab.setCustomView(SSMainActivityPagerAdapter.getInitialViews(i));
        }
        if(CurrentUser.shared.getIsGuest(getBaseContext()) == false)
        {
            setUpCartCount();
        }
        else{
            setUpGuestCartCount();
        }
    }

    public void setTabChangeListener() {
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                SSSwipeOffViewPager.setCurrentItem(tab.getPosition());
                for (int i = 0; i < tabLayout.getTabCount(); i++) {
                    TabLayout.Tab t = tabLayout.getTabAt(i);
                    SSMainActivityPagerAdapter.updateView(tab.getPosition(),i, t.getCustomView());
                }
                switch(tab.getPosition())
                {
                    case 0:
                        tabIdHistory.remove(tab);
                        tabIdHistory.add(tab);
                        if (cartRecyclerView != null) {
                            cartAdapter = new SSCartAdapter(context, (Activity) context,new ArrayList<List<CartPerStore>>());
                            cartRecyclerView.setAdapter(cartAdapter);
                        }
                        break;
                    case 1:
                        tabIdHistory.remove(tab);
                        tabIdHistory.add(tab);
                        setCartUI();
                        if(CurrentUser.shared.getIsGuest(getBaseContext()) == false) {
                            setUpCartCount();
                        }
                        else{
                            setUpGuestCartCount();
                        }
                        break;
                    case 2:
                        tabIdHistory.remove(tab);
                        tabIdHistory.add(tab);
                        if (cartRecyclerView != null) {
                            cartAdapter = new SSCartAdapter(context, (Activity) context, new ArrayList<List<CartPerStore>>());
                            cartRecyclerView.setAdapter(cartAdapter);
                        }
                        break;
                    case 3:
                        tabIdHistory.remove(tab);
                        tabIdHistory.add(tab);
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    @Override
    public void setHomeUI() throws IOException {
        homerefresh = (SwipeRefreshLayout) findViewById(R.id.homerefresh);
        noProductsLabel = (TextView) findViewById(R.id.noProductsLabel);
        homeGridView = (GridView) findViewById(R.id.homeGridView);
        homeAdapter = new SSNewHomeAdapter(context, new ArrayList<Product>(), (Activity) context);
        homeGridView.setAdapter(homeAdapter);
        final FloatingActionButton upButton = (FloatingActionButton) findViewById(R.id.upButton);
        upButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                homeGridView.smoothScrollToPosition(0);
            }
        });
        populateHome();
        homerefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                HomeLastProduct.shared.setHomeLastProduct(null);
                homeAdapter.products.clear();
                populateHome();
            }
        });

        homeGridView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if(firstVisibleItem > 0) {
                    upButton.setVisibility(View.VISIBLE);
                } else {
                    upButton.setVisibility(View.INVISIBLE);
                }
            }
        });
        if(CurrentUser.shared.getIsGuest(getBaseContext()) == true && CurrentUser.shared.getCurrentUser() != null)
        {
            CurrentUser.shared.setGuest(context, false);
            final List<List<CartPerStore>> cartsArray = GuestUser.sharedGuest.getCartperstoreproducts();
            if(cartsArray != null) {
                cartsArray.clear();
                GuestUser.sharedGuest.setCartperstoreproducts(cartsArray, context);
            }
        }
    }

    void populateHome() {
        SSFirestoreManager.firestoreHelper.getHomeScreenProducts(new SSPostsHandler() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void completionHandler(Boolean success, List<Product> products, String error) {
                if (success) {
                    homerefresh.setRefreshing(false);
                    if (products.size() > 0 ) {
                        int preRemovalSize = products.size();
                        if(CurrentUser.shared.getIsGuest(getBaseContext()) == false) {
                            for (int i = 0; i < products.size(); i++) {
                                if (products.get(i).getUserid().equals(CurrentUser.shared.getCurrentUser().getUserid())) {
                                    products.remove(i);
                                    i--;
                                }

                            }
                        }
                        if(products.size()<= 0 && preRemovalSize > 0)
                        {
                            populateHome();
                            return;
                        }
                        if(products.size() <= 0) {
                            noProductsLabel.setVisibility(View.VISIBLE);
                            homeGridView.setVisibility(View.INVISIBLE);
                        }
                        else {
                            noProductsLabel.setVisibility(View.INVISIBLE);
                            homeGridView.setVisibility(View.VISIBLE);
                            homeAdapter.products = products;
                            homeAdapter.notifyDataSetChanged();
                        }

                    } else {
                        noProductsLabel.setVisibility(View.VISIBLE);
                        homeGridView.setVisibility(View.INVISIBLE); }
                } else {
                    Toast.makeText(context, error, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public void goToSearch() {
        searchButton = (ImageButton) findViewById(R.id.searchButton);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            Intent intent = new Intent(context, SSSearchView.class);
            startActivity(intent);
            }
        });
    }

    @Override
    public void settingsButtonListener() {
        settingsButton = (ImageButton) findViewById(R.id.settingsButton);
        if(CurrentUser.shared.getIsGuest(getBaseContext()) == true)
        {
            settingsButton.setVisibility(View.INVISIBLE);
        }
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, SSSettingsView.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
            }
        });
    }

    @Override
    public void setOrderHistoryButtonListener() {
    }

    @Override
    public void setAddNewItemButtonListener() {
        addNewItem = (FloatingActionButton) findViewById(R.id.addNewItemButton);
        if(CurrentUser.shared.getIsGuest(getBaseContext()) == true)
        {
            addNewItem.setVisibility(View.INVISIBLE);
        }
        addNewItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, SSAddNewItemView.class);
                startActivity(i);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
            }
        });
    }

    @Override
    public void setProfileUI() {
        noProfileProductsLabel = (TextView) findViewById(R.id.noProfileProductsLabel);
        profileGridView = (GridView) findViewById(R.id.profileGridView);
        LinearLayout guestActionView = (LinearLayout) findViewById(R.id.guestActionView);
        if(CurrentUser.shared.getIsGuest(getBaseContext()) == false) {
            guestActionView.setVisibility(View.INVISIBLE);
            SSFirestoreManager.firestoreHelper.getUserProducts(CurrentUser.shared.getCurrentUser().getUserid(), new SSPostsHandler() {
                @Override
                public void completionHandler(Boolean success, List<Product> products, String error) {
                    if (success) {
                        if (products.size() > 0) {
                            noProfileProductsLabel.setVisibility(View.INVISIBLE);
                            profileGridView.setVisibility(View.VISIBLE);
                            profileAdapter = new SSProfileAdapter((Activity) context, context, products);
                            profileGridView.setAdapter(profileAdapter);
                        } else {
                            noProfileProductsLabel.setVisibility(View.VISIBLE);
                            profileGridView.setVisibility(View.INVISIBLE);
                        }
                    } else {
                        Toast.makeText(context, error, Toast.LENGTH_LONG).show();
                    }
                }
            });

            final SwipeRefreshLayout pullToRefresh = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
            pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    SSFirestoreManager.firestoreHelper.getUserProducts(CurrentUser.shared.getCurrentUser().getUserid(), new SSPostsHandler() {
                        @Override
                        public void completionHandler(Boolean success, List<Product> products, String error) {
                            if (success) {
                                profileAdapter = new SSProfileAdapter((Activity) context, context, products);
                                profileGridView.setAdapter(profileAdapter);
                            } else {
                                Toast.makeText(context, error, Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                    pullToRefresh.setRefreshing(false);
                }
            });
        }
        else {
            guestActionView.setVisibility(View.VISIBLE);
            Button signupButton = (Button) findViewById(R.id.signupfromprofile);
            Button loginButton = (Button) findViewById(R.id.loginfromprofile);
            signupButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, SSSignupView.class);
                    context.startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);

                }
            });
            loginButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, SSLoginView.class);
                    context.startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
                }
            });
        }
    }

    @Override
    public void setNextViewsListeners() {}

    private void populateCart() {
            SSFirestoreManager.firestoreHelper.getItemsInCart( new SSCartHandler() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void completionHandler(Boolean success, final List<String> cartperstoreid, String error) {
            if (success) {
                Collections.sort(cartperstoreid);
                final List<List<CartPerStore>> cartsArr = new ArrayList<>() ;
                if (cartperstoreid.size() <= 0) {
                    cartRecyclerView.setVisibility(View.INVISIBLE);
                    noCartProductsLabel.setVisibility(View.VISIBLE);
                }
                final Integer[] j = {0};
                for(int i = 0; i <cartperstoreid.size(); i++) {
                    final int position=i;
                    SSFirestoreManager.firestoreHelper.getCartPerStore(cartperstoreid.get(i), new SSCartProductHandler() {
                        @Override
                        public void completionHandler(Boolean success, List<CartPerStore> cartperstoreproducts, String error) {
                            j[0] +=1;
                            if(success) {
                            if(cartperstoreproducts.size()>0) {
                                cartsArr.add(cartperstoreproducts);
                            }
                            if (j[0] == cartperstoreid.size()) {
                                if (cartsArr.size() > 0) {
                                    cartRecyclerView.setVisibility(View.VISIBLE);
                                    noCartProductsLabel.setVisibility(View.INVISIBLE);
                                    cartAdapter = new SSCartAdapter(context, (Activity) context, cartsArr);
                                    cartRecyclerView.setAdapter(cartAdapter);
                                } else {
                                    cartRecyclerView.setVisibility(View.INVISIBLE);
                                    noCartProductsLabel.setVisibility(View.VISIBLE);
                                }
                            }
                        }
                        }
                    });
                }
            } else {
                Toast.makeText(context, error, Toast.LENGTH_LONG).show();
            }
        }});
    }

    private void populateGuestCart() {
        final List<List<CartPerStore>> cartsArr = GuestUser.sharedGuest.getCartperstoreproducts();
        if(cartsArr != null) {
            if (cartsArr.size() <= 0) {
                cartRecyclerView.setVisibility(View.INVISIBLE);
                noCartProductsLabel.setVisibility(View.VISIBLE);
            } else {

                cartRecyclerView.setVisibility(View.VISIBLE);
                noCartProductsLabel.setVisibility(View.INVISIBLE);
                cartAdapter = new SSCartAdapter(context, (Activity) context, cartsArr);
                cartRecyclerView.setAdapter(cartAdapter);
            }
        }
    }

    @Override
    public void setUpGuestCartCount()
    {
        int itemCount = 0;
        List<List<CartPerStore>> cartsArray = GuestUser.sharedGuest.getCartperstoreproducts();
        if(cartsArray != null) {
            for (int i = 0; i < cartsArray.size(); i++) {
                for (int j = 0; j < cartsArray.get(i).size(); j++) {
                    itemCount++;
                }
            }
        }
        SSMainActivityPagerAdapter.updateCartCount(itemCount+"",tabLayout.getTabAt(1).getCustomView());
    }

    @Override
    public void setUpCartCount() {
        final Integer[] count = {0};

        SSFirestoreManager.firestoreHelper.getItemsInCart( new SSCartHandler() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void completionHandler(Boolean success, final List<String> cartperstoreid, String error) {
                if (success) {
                    Collections.sort(cartperstoreid);
                    final List<List<CartPerStore>> cartsArr = new ArrayList<>() ;
                    final Integer[] j = {0};
                    if (cartperstoreid.size() <= 0) {
                        tabLayout.getTabAt(1).getOrCreateBadge().setNumber(0);
                    }
                    for(int i = 0; i <cartperstoreid.size(); i++) {
                        final int position=i;
                        SSFirestoreManager.firestoreHelper.getCartPerStore(cartperstoreid.get(i), new SSCartProductHandler() {
                            @Override
                            public void completionHandler(Boolean success, List<CartPerStore> cartperstoreproducts, String error) {
                                j[0] +=1;
                                if(success) {
                                    if(cartperstoreproducts.size()>0) {
                                        count[0] += cartperstoreproducts.size();
                                        cartsArr.add(cartperstoreproducts);
                                    }
                                    if (j[0] == cartperstoreid.size()) {
                                        SSMainActivityPagerAdapter.updateCartCount(count[0]+"",tabLayout.getTabAt(1).getCustomView());
                                    }
                                }
                            }
                        });
                    }
                } else {
                }
            }});
    }

    @Override
    public void setCartUI() {
        cartProgress = (ProgressBar) findViewById(R.id.cartProgress);
        noCartProductsLabel  = (TextView) findViewById(R.id.noCartProductsLabel);
        cartRecyclerView = (RecyclerView) findViewById(R.id.cartRecyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        cartRecyclerView.setLayoutManager(linearLayoutManager);
        cartRecyclerView.setItemAnimator(new DefaultItemAnimator());
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(cartRecyclerView.getContext(), linearLayoutManager.getOrientation());
        cartRecyclerView.addItemDecoration(dividerItemDecoration);
        cartRecyclerView.setHasFixedSize(true);
        cartRecyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        if(CurrentUser.shared.getIsGuest(getBaseContext()) == false) {
            populateCart();
        }
        else{
            populateGuestCart();
        }
        final SwipeRefreshLayout pullToRefresh =(SwipeRefreshLayout) findViewById(R.id.cart_swipe_container);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(CurrentUser.shared.getIsGuest(getBaseContext()) == false) {
                    populateCart();
                }
                else{
                    populateGuestCart();
                }
                pullToRefresh.setRefreshing(false);
            }
        });
        SharedVariables.shared.cartProgressHandler = new SSCompletionHandler() {
            @Override
            public void completionHandler(Boolean success, String error) {
                if (success) {
                    cartProgress.animate();
                    cartProgress.setVisibility(View.VISIBLE);
                } else {
                    cartProgress.setIndeterminate(true);
                    cartProgress.setVisibility(View.INVISIBLE);
                }
            }
        };
    }

    @Override
    public void setAppInfoUI() {
        infoRecyclerView = (RecyclerView) findViewById(R.id.infoRecyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        infoRecyclerView.setLayoutManager(linearLayoutManager);
        infoRecyclerView.setItemAnimator(new DefaultItemAnimator());
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(infoRecyclerView.getContext(), linearLayoutManager.getOrientation());
        infoRecyclerView.addItemDecoration(dividerItemDecoration);
        infoRecyclerView.setHasFixedSize(true);
        infoRecyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        SSAboutUsInfoAdapter adapter = new SSAboutUsInfoAdapter(context, (Activity) context);
        infoRecyclerView.setAdapter(adapter);
    }
}
