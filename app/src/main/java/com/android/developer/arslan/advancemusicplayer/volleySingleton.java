package com.android.developer.arslan.advancemusicplayer;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class volleySingleton {
    private static volleySingleton mInstance;
    private RequestQueue mRequestQueue;
    private  static Context mCtx;

    private volleySingleton(Context context){
        mCtx = context;
        mRequestQueue = getmRequestQueue();
    }

    public static  synchronized volleySingleton getInstance(Context context){
        if(mInstance == null){
            mInstance = new volleySingleton(context);
        }
        return  mInstance;
    }

    public RequestQueue getmRequestQueue(){
        if(mRequestQueue == null){
            mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
        }
        return  mRequestQueue;
    }
}
