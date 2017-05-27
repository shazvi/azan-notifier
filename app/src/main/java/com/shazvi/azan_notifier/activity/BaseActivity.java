package com.shazvi.azan_notifier.activity;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.shazvi.azan_notifier.R;
import com.shazvi.azan_notifier.application.AppController;
import com.shazvi.azan_notifier.helper.AppUtils;
import com.shazvi.azan_notifier.helper.SharedPref;

public class BaseActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    // Initialize core variables
    public AppController mAppController;
    public AppUtils appUtils;
    public SharedPref sharedPref;
    public FirebaseAnalytics mFirebaseAnalytics;

    // Initialize other variables
    public static int DO_REFRESH = 7210;
    public static int DONT_REFRESH = 4256;
    public static int SETTINGS_PAGE = 8374;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAppController = AppController.getInstance();
        appUtils = new AppUtils(getApplicationContext());
        sharedPref = new SharedPref(getApplicationContext());
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
    }

    @Override
    public void setContentView(int layoutResID)
    {
        DrawerLayout fullView = (DrawerLayout) getLayoutInflater().inflate(R.layout.activity_base, null);
        NavigationView navigationView = (NavigationView) fullView.findViewById(R.id.nav_view);
        navigationView.setItemTextColor(ColorStateList.valueOf(Color.BLACK));
        navigationView.setItemIconTintList(ColorStateList.valueOf(Color.DKGRAY));
        navigationView.setNavigationItemSelectedListener(this);

        // Insert child view to base view
        FrameLayout activityContainer = (FrameLayout) fullView.findViewById(R.id.activity_content);
        getLayoutInflater().inflate(layoutResID, activityContainer, true);
        super.setContentView(fullView);
    }

    // On pressing system back button, if side nav is open, close it instead of going to previous screen
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    // Side nav item onclick event handlers
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Intent intent = null;

        switch(id) {
            case R.id.nav_home: intent = new Intent(getApplicationContext(), HomeActivity.class); break;
            case R.id.nav_prayer_times: intent = new Intent(getApplicationContext(), HomeActivity.class); break;
            case R.id.nav_calendar: intent = new Intent(getApplicationContext(), HomeActivity.class); break;
        }

        if(intent != null) {
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
