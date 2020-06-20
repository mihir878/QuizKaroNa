package com.example.triviatest.controller;

import android.app.Application;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class AppController extends Application {
        public static final String TAG = AppController.class.getSimpleName();
        private static AppController mInstance;
        private RequestQueue mRequestQueue;


        public static synchronized AppController getInstance() {
           // if (instance == null) {
             //   instance = new AppController(context);
            //}
            return mInstance;
        }


    @Override
    public void onCreate() {
        super.onCreate();
        mInstance=this;
    }

    public RequestQueue getRequestQueue() {
            if (mRequestQueue == null) {
                // getApplicationContext() is key, it keeps you from leaking the
                // Activity or BroadcastReceiver if someone passes one in.
                mRequestQueue = Volley.newRequestQueue(getApplicationContext());
            }
            return mRequestQueue;
        }

        public <T> void addToRequestQueue(Request<T> req,String tag) {
            req.setTag(TextUtils.isEmpty(tag)?TAG:tag);
            getRequestQueue().add(req);
        }

        public <T> void addToRequestQueue(Request<T> req){
            req.setTag(TAG);
            getRequestQueue().add(req);
        }

        public void cancelPendingRequest(Object tag){
            if(mRequestQueue!=null)
                mRequestQueue.cancelAll(tag);
        }

    }
