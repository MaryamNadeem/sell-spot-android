package com.sellspot.app.Activities.MainActivity.Profile;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.content.Context;
import com.sellspot.app.R;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class SSProfileView extends Fragment {

    private OnFragmentInteractionListener mListener;

    public SSProfileView() {}

    public static SSProfileView newInstance(String param1, String param2) {
        SSProfileView fragment = new SSProfileView();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.profile_view, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mListener.setNextViewsListeners();
        mListener.settingsButtonListener();
        mListener.setOrderHistoryButtonListener();
        mListener.setAddNewItemButtonListener();
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
        public void setProfileUI();
        public void setNextViewsListeners();
        public void settingsButtonListener();
        public void setOrderHistoryButtonListener();
        public void setAddNewItemButtonListener();
    }

    @Override
    public void onResume() {
        super.onResume();
        mListener.setProfileUI();
    }
}
