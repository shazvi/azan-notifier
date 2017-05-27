package com.shazvi.azan_notifier.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shazvi.azan_notifier.R;
import com.shazvi.azan_notifier.application.AppController;
import com.shazvi.azan_notifier.helper.AppUtils;
import com.shazvi.azan_notifier.helper.SharedPref;

import org.json.JSONObject;

public class HomeFragment extends Fragment {

    // Initialize Core Variables
    private AppController mAppController;
    private HomeActivity mHomeActivity;
    private Activity mActivity;
    private AppUtils appUtils;
    private SharedPref sharedPref;
    private JSONObject jsonTimes;

    // Initialize UI elements

    // Initialize other variable
    private static final String ARG_SECTION_NUMBER = "section_number";
    private int selectedTab;

    // Mandatory empty constructor for the fragment manager to instantiate the fragment (e.g. upon screen orientation changes).
    public HomeFragment() {}

    // Returns a new instance of this fragment for the given section number.
    public static HomeFragment newInstance(int sectionNumber) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHomeActivity = (HomeActivity) getActivity();
        mActivity = this.getActivity();
        mAppController = mHomeActivity.mAppController;
        appUtils = mHomeActivity.appUtils;
        sharedPref = mHomeActivity.sharedPref;
        jsonTimes = mHomeActivity.jsonTimes;
        selectedTab = getArguments().getInt(ARG_SECTION_NUMBER);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // todo Define UI elements

        // todo: Load prayer times

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == BaseActivity.SETTINGS_PAGE && resultCode == BaseActivity.DO_REFRESH) {
            // refresh activity if relevant settings changed
        }
    }
}