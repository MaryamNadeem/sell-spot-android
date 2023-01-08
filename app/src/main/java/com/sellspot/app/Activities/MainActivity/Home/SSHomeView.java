package com.sellspot.app.Activities.MainActivity.Home;

import android.view.View;
import android.net.Uri;
import android.os.Bundle;
import android.view.ViewGroup;
import android.content.Context;
import com.sellspot.app.R;
import com.sellspot.app.Shared.HomeLastProduct;

import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.io.IOException;

public class SSHomeView extends Fragment {

    private OnFragmentInteractionListener mListener;
    private boolean viewCreated = false;
    private View homeView = null;

    public SSHomeView() {}

    public static SSHomeView newInstance(String param1, String param2) {
        SSHomeView fragment = new SSHomeView();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(homeView == null) {
            homeView = inflater.inflate(R.layout.home_view, container, false);
        }
        return homeView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mListener.goToSearch();
        if(viewCreated == false)
        {
            HomeLastProduct.shared.setHomeLastProduct(null);
            try {
                mListener.setHomeUI();
            } catch (IOException e) {
                e.printStackTrace();
            }
            viewCreated = true;
        }
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {}
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        public void setHomeUI() throws IOException;
        public void goToSearch();
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
