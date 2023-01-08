package com.sellspot.app.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.sellspot.app.Activities.AboutUsInfo.SSAboutUsInfoView;
import com.sellspot.app.Activities.MainActivity.Home.SSHomeView;
import com.sellspot.app.Activities.MainActivity.Cart.SSCartView;
import com.sellspot.app.Activities.MainActivity.Profile.SSProfileView;
import com.sellspot.app.R;

public class SSMainActivityPagerAdapter extends FragmentPagerAdapter {
    private int nooftabs;
    private Fragment home = new SSHomeView();
    private Fragment cart = new SSCartView();
    private Fragment profile = new SSProfileView();
    private Fragment aboutus = new SSAboutUsInfoView();
    private Context context;

    private String tabTitles[] = new String[] { "Home", "Cart", "Learn More", "My Shop" };
    private int[] imageBlackResId = { R.mipmap.home_black, R.mipmap.cart_black, R.mipmap.learn_more_black, R.mipmap.profile_black };
    private int[] imageYellowResId = { R.mipmap.home_yellow, R.mipmap.cart_yellow, R.mipmap.learn_more_yellow, R.mipmap.profile_yellow };
    public SSMainActivityPagerAdapter(FragmentManager fm, int NumberOfTabs, Context context) {
        super(fm);
        nooftabs = NumberOfTabs;
        this.context = context;
    }

    @Override
    public  Fragment getItem(int position) {
        switch (position) {
            case 0:
                return home;
            case 1:
                return cart;
            case 2:
                return aboutus;
            case 3:
                return profile;
            default:
                return null;
        }
    }

    public View getInitialViews(int position) {
        View v = LayoutInflater.from(context).inflate(R.layout.tab_row, null);
        TextView tv = (TextView) v.findViewById(R.id.tablabel);
        tv.setText(tabTitles[position]);
        ImageView img = (ImageView) v.findViewById(R.id.tabimage);
        TextView cartCount = (TextView) v.findViewById(R.id.cartCount);
        cartCount.setVisibility(View.GONE);
        if (position == 0) {
            tv.setTextColor(context.getResources().getColor(R.color.appthemecolor));
            img.setImageResource(imageYellowResId[position]);
        } else if (position == 1) {
            cartCount.setVisibility(View.VISIBLE);
        } else {
            img.setImageResource(imageBlackResId[position]);
        }
        return v;
    }

    public void updateCartCount(String count, View v) {
        TextView cartCount = (TextView) v.findViewById(R.id.cartCount);
        cartCount.setText(count);
    }

    public void updateView(int currentPosition, int tabPosition, View v) {
        TextView tv = (TextView) v.findViewById(R.id.tablabel);
        ImageView img = (ImageView) v.findViewById(R.id.tabimage);
        tv.setText(tabTitles[tabPosition]);
        switch (currentPosition) {
            case 0:
                switch (tabPosition) {
                    case 0:
                        tv.setTextColor(context.getResources().getColor(R.color.appthemecolor));
                        img.setImageResource(imageYellowResId[tabPosition]);
                        break;
                    default:
                        tv.setTextColor(context.getResources().getColor(R.color.black));
                        img.setImageResource(imageBlackResId[tabPosition]);
                        break;
                }
                break;
            case 1:
                switch (tabPosition) {
                    case 1:
                        tv.setTextColor(context.getResources().getColor(R.color.appthemecolor));
                        img.setImageResource(imageYellowResId[tabPosition]);
                        break;
                    default:
                        tv.setTextColor(context.getResources().getColor(R.color.black));
                        img.setImageResource(imageBlackResId[tabPosition]);
                        break;
                }
                break;
            case 2:
                switch (tabPosition) {
                    case 2:
                        tv.setTextColor(context.getResources().getColor(R.color.appthemecolor));
                        img.setImageResource(imageYellowResId[tabPosition]);
                        break;
                    default:
                        tv.setTextColor(context.getResources().getColor(R.color.black));
                        img.setImageResource(imageBlackResId[tabPosition]);
                        break;
                }
                break;
            case 3:
                switch (tabPosition) {
                    case 3:
                        tv.setTextColor(context.getResources().getColor(R.color.appthemecolor));
                        img.setImageResource(imageYellowResId[tabPosition]);
                        break;
                    default:
                        tv.setTextColor(context.getResources().getColor(R.color.black));
                        img.setImageResource(imageBlackResId[tabPosition]);
                        break;
                }
                break;
            default:
                break;
        }
    }

    @Override
    public int getCount() {
        return nooftabs;
    }
}
