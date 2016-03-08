package com.volokh.danylo.imagetransition;

import android.app.Application;

import com.volokh.danylo.imagetransition.library.handler.TransitionHandler;

/**
 * Created by danylo.volokh on 3/7/16.
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        TransitionHandler.instance(this);

    }
}
