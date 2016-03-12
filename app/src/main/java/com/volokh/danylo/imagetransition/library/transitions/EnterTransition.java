package com.volokh.danylo.imagetransition.library.transitions;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;

import com.volokh.danylo.imagetransition.library.Config;
import com.volokh.danylo.imagetransition.library.handler.BaseTransition;

import java.util.Map;

/**
 * Created by danylo.volokh on 3/9/16.
 */
public class EnterTransition extends BaseTransition {

    private static final boolean SHOW_LOGS = Config.SHOW_LOGS;
    private static final String TAG = EnterTransition.class.getSimpleName();

    private Activity mToActivity;

    public interface EnterTransitionCallback {
        void runSharedElementsTransition(Map<String, View> sharedElementsTransitionViews);
    }

    public EnterTransition(EnterTransitionCallback enterTransitionCallback) {
        mEnterTransitionCallback = enterTransitionCallback;
    }

    private EnterTransitionCallback mEnterTransitionCallback;

    @Override
    protected void handleActivityResumed(Activity activity) {
        if (SHOW_LOGS) Log.v(TAG, ">> handleActivityResumed");

        if (mToActivity.getClass() == activity.getClass()) {
            if (SHOW_LOGS) Log.v(TAG, "handleActivityResumed");

                if (SHOW_LOGS) Log.v(TAG, "handleActivityResumed, window " + activity.getWindow());
                final View decorView = activity.getWindow().getDecorView();

                decorView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {

                    @Override
                    public boolean onPreDraw() {

                        if (SHOW_LOGS)
                            Log.v(TAG, "onPreDraw, handleActivityResumed");

                        decorView.getViewTreeObserver().removeOnPreDrawListener(this);

                        mEnterTransitionCallback.runSharedElementsTransition(getSharedElementsTransitionViews());
                        mEnterTransitionCallback = null;
                        return true;
                    }
                });
        }
        if (SHOW_LOGS) Log.v(TAG, "<< handleActivityResumed");
    }

    @Override
    public EnterTransition addSharedElement(String transitionName, View view) {
        super.addSharedElement(transitionName, view);
        return this;
    }

    public BaseTransition toActivity(Activity activity) {
        mToActivity = activity;
        return this;
    }
}
