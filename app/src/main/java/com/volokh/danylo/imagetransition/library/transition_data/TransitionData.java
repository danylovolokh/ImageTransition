package com.volokh.danylo.imagetransition.library.transition_data;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by danylo.volokh on 3/7/16.
 */
public abstract class TransitionData {

    private static final String TAG = TransitionData.class.getSimpleName();
    private final Map<String, View> mExitTransitionViews = new HashMap<>();

    private View mExitContentView;
    private Class mEnterActivityClass;

    public void addTransitionView(String transitionName, ImageView view) {
        Log.v(TAG, "addSharedElement, transitionName[" + transitionName + "], view " + view);
        mExitTransitionViews.put(transitionName, view);
    }

    public Map<String, View> getSharedElements() {
        return mExitTransitionViews;
    }


}
