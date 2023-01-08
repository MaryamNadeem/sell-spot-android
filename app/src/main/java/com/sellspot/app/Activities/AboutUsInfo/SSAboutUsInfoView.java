package com.sellspot.app.Activities.AboutUsInfo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sellspot.app.R;

public class SSAboutUsInfoView extends Fragment {
    private OnFragmentInteractionListener mListener;

    public SSAboutUsInfoView() {}

    public static SSAboutUsInfoView newInstance(String param1, String param2) {
        SSAboutUsInfoView fragment = new SSAboutUsInfoView();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_about_us_info_view, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mListener.setAppInfoUI();
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
        public void setAppInfoUI();
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}