package com.volokh.danylo.imagetransition.library.handler;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.volokh.danylo.imagetransition.library.Config;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by danylo.volokh on 3/8/16.
 */
public abstract class BaseTransition {

    private static final boolean SHOW_LOGS = Config.SHOW_LOGS;
    private static final String TAG = BaseTransition.class.getSimpleName();

    protected Map<String, View> getExitTransitionViews() {
        return mExitTransitionViews;
    }

    private final Map<String, View> mExitTransitionViews = new HashMap<>();

    protected abstract void handleActivityCreated(Activity activity, Bundle savedInstanceState);

    public void addTransitionView(String transitionName, View view) {
        if(SHOW_LOGS) Log.v(TAG, "addTransitionView, transitionName[" + transitionName + "], view " + view);
        mExitTransitionViews.put(transitionName, view);
    }
}
