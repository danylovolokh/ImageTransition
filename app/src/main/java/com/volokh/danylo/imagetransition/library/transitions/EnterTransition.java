package com.volokh.danylo.imagetransition.library.transitions;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.volokh.danylo.imagetransition.library.Config;
import com.volokh.danylo.imagetransition.library.animators.SharedElementTransitionAnimator;
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

    private final EnterTransitionCallback mEnterTransitionCallback;

    @Override
    protected void handleActivityCreated(Activity activity, Bundle savedInstanceState) {

        if (mToActivity.getClass() == activity.getClass()) {
            if (SHOW_LOGS)
                Log.v(TAG, "handleActivityCreated, savedInstanceState " + savedInstanceState);
            if (savedInstanceState == null) {
                if (SHOW_LOGS)
                    Log.v(TAG, "handleActivityCreated, savedInstanceState null, animate entering");
                final View decorView = activity.getWindow().getDecorView();

                decorView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {


                    @Override
                    public boolean onPreDraw() {

                        if (SHOW_LOGS)
                            Log.v(TAG, "onPreDraw, handleActivityCreated");

                        decorView.getViewTreeObserver().removeOnPreDrawListener(this);

                        mEnterTransitionCallback.runSharedElementsTransition(getSharedElementsTransitionViews());


                        return true;
                    }
                });
            }
        }
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
