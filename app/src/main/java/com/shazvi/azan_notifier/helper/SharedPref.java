package com.shazvi.azan_notifier.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SharedPref {
    // LogCat tag
    private static String TAG = SharedPref.class.getSimpleName();

    // Shared Preferences
    private SharedPreferences pref;
    private Editor editor;
    private Context _context;

    // Shared pref mode
    private int PRIVATE_MODE = 0;

    // Shared preferences file name
    private static final String PREF_NAME = "AzanPrefKey";

    private static final String KEY_VOLUME = "volume";
    private static final String KEY_PLAY_AZAN = "play_azan";
    private static final String KEY_START_UP = "start_up";
    private static final String KEY_STATUS_BAR_ICON = "status_bar_icon";
    private static final String KEY_NOTIFICATION = "notification";
    private static final String KEY_CITY = "city";
    private static final String KEY_HIJRI_OFFSET = "hijri_offset";

    private static final int defaultVolume = 50;

    public SharedPref(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }


    // Volume getter and setter
    public int getVolume(){
        return pref.getInt(KEY_VOLUME, defaultVolume);
    }
    public void setVolume(int value) {
        editor.putInt(KEY_VOLUME, value);
        editor.commit();
    }

    // Play azan getter and setter
    public boolean getPlayAzan(){
        return pref.getBoolean(KEY_PLAY_AZAN, false);
    }
    public void setPlayAzan(boolean value) {
        editor.putBoolean(KEY_PLAY_AZAN, value);
        editor.commit();
    }

    // Start up getter and setter
    public boolean getStartUp(){
        return pref.getBoolean(KEY_START_UP, false);
    }
    public void setStartUp(boolean value) {
        editor.putBoolean(KEY_START_UP, value);
        editor.commit();
    }

    // Status bar icon getter and setter
    public boolean getStatusBarIcon(){
        return pref.getBoolean(KEY_STATUS_BAR_ICON, true);
    }
    public void setStatusBarIcon(boolean value) {
        editor.putBoolean(KEY_STATUS_BAR_ICON, value);
        editor.commit();
    }

    // Status bar icon getter and setter
    public boolean getNotification(){
        return pref.getBoolean(KEY_NOTIFICATION, true);
    }
    public void setNotification(boolean value) {
        editor.putBoolean(KEY_NOTIFICATION, value);
        editor.commit();
    }

    // City getter and setter
    public String getCity(){
        return pref.getString(KEY_CITY, "colombo");
    }
    public void setCity(String value) {
        editor.putString(KEY_CITY, value);
        editor.commit();
    }

    // Hijri offset getter and setter
    public int getHijriOffset(){
        return pref.getInt(KEY_HIJRI_OFFSET, 0);
    }
    public void setHijriOffset(int value) {
        editor.putInt(KEY_HIJRI_OFFSET, value);
        editor.commit();
    }


    // Check if key name exists in shared preference
    public boolean hasKey(String keyName) {
        return pref.contains(keyName);
    }

    // Clearing all data from Shared Preferences
    public void clearData() {
        editor.clear();
        editor.commit();
    }
}
