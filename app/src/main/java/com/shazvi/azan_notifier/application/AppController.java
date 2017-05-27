package com.shazvi.azan_notifier.application;

import android.app.Application;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class AppController extends Application {

    public static final String TAG = AppController.class.getSimpleName();
    private RequestQueue mRequestQueue;
    private static AppController mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
//        FontsOverride.setDefaultFont(this, "DEFAULT", "fonts/lato/lato-regular.ttf");
//        FontsOverride.setDefaultFont(this, "DEFAULT_BOLD", "fonts/lato/lato-bold.ttf");
//        FontsOverride.setDefaultFont(this, "MONOSPACE", "fonts/lato/lato-regular.ttf");
//        FontsOverride.setDefaultFont(this, "SERIF", "fonts/lato/lato-regular.ttf");
//        FontsOverride.setDefaultFont(this, "SANS_SERIF", "fonts/lato/lato-regular.ttf");
        mInstance = this;

        // todo: move defining imageLoader to this appcontroller
    }

    public static synchronized AppController getInstance() {
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }
}