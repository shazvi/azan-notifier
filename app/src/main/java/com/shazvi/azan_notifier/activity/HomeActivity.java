package com.shazvi.azan_notifier.activity;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;

import com.shazvi.azan_notifier.R;
import com.shazvi.azan_notifier.application.AppConfig;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

public class HomeActivity extends BaseActivity {

    // Initialize other variables
    public JSONObject jsonTimes;
    public String[] TABNAMES = {"sdfsdf", "fdsfd"};
    public int TABCOUNT = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mFirebaseAnalytics.logEvent(AppConfig.FA_OPENED_HOME, null);
        AssetManager assetManager = getAssets();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("");

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        // Create the adapter that will return a fragment for each of the three primary sections of the activity.
        // Set up the ViewPager with the sections adapter.
        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        ViewPager mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        // Get JSON file contents
        try {
            InputStream input = assetManager.open(sharedPref.getCity()+".json");
            int size = input.available();
            byte[] buffer = new byte[size];
            input.read(buffer);
            input.close();
            jsonTimes = new JSONObject(new String(buffer));
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }


    // A FragmentPagerAdapter that returns a fragment corresponding to one of the sections/tabs/pages.
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int pos) {
            // getItem is called to instantiate the fragment for the given page. Return the appropriate fragment
            return HomeFragment.newInstance(pos);
        }

        @Override
        public int getCount() {
            return TABCOUNT;
        }

        @Override
        public CharSequence getPageTitle(int pos) {
            return TABNAMES[pos];
        }
    }
}
