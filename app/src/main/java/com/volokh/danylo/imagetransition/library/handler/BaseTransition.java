package com.volokh.danylo.imagetransition.library.handler;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.volokh.danylo.imagetransition.library.Config;
import com.volokh.danylo.imagetransition.library.animators.SharedElementTransitionAnimator;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by danylo.volokh on 3/8/16.
 */
public abstract class BaseTransition {

    private static final boolean SHOW_LOGS = Config.SHOW_LOGS;
    private static final String TAG = BaseTransition.class.getSimpleName();

    private final Map<String, View> mSharedElementsTransitionViews = new HashMap<>();

    protected Map<String, View> getSharedElementsTransitionViews() {
        return mSharedElementsTransitionViews;
    }

    protected abstract void handleActivityCreated(Activity activity, Bundle savedInstanceState);

    public BaseTransition addSharedElement(String transitionName, View view) {
        if(SHOW_LOGS) Log.v(TAG, "addSharedElement, transitionName[" + transitionName + "], view " + view);
        mSharedElementsTransitionViews.put(transitionName, view);
        return this;
    }
}
