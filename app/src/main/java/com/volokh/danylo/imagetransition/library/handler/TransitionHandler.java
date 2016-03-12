package com.volokh.danylo.imagetransition.library.handler;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.volokh.danylo.imagetransition.library.Config;
import com.volokh.danylo.imagetransition.library.transitions.EnterTransition;
import com.volokh.danylo.imagetransition.library.transitions.ExitTransition;

import java.util.Map;

/**
 * Created by danylo.volokh on 3/7/16.
 */
public class TransitionHandler implements EnterTransition.EnterTransitionCallback {

    private static final String TAG = TransitionHandler.class.getSimpleName();
    private static final boolean SHOW_LOGS = Config.SHOW_LOGS;

    private static TransitionHandler mTransitionHandler;

    private BaseTransition mExitTransition;
    private BaseTransition mEnterTransition;

    TransitionHandler(Application application){

        application.registerActivityLifecycleCallbacks(new SimpleActivityLifecycleCallbacks() {

            private Bundle mRecentBundle;

            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                if (SHOW_LOGS) Log.v(TAG, "onActivityCreated, activity " + activity + ", savedInstanceState " + savedInstanceState);
                mRecentBundle = savedInstanceState;
            }

            @Override
            public void onActivityResumed(Activity activity) {
                if (SHOW_LOGS) Log.v(TAG, "onActivityResumed, activity " + activity);

                if (SHOW_LOGS) Log.v(TAG, "onActivityResumed, mExitTransition " + mExitTransition);
                if (SHOW_LOGS) Log.v(TAG, "onActivityResumed, mRecentBundle " + mRecentBundle);

                if(mRecentBundle == null){
                    // only if activity is started for the first time we run the animation
                    if (mExitTransition != null) {
                        mExitTransition.handleActivityResumed(activity);
                        mExitTransition = null;
                    }

                    if (mEnterTransition != null) {
                        mEnterTransition.handleActivityResumed(activity);
                        mEnterTransition = null;
                    }
                    
                }

            }
        });
    }



    public static TransitionHandler instance() {
        if(SHOW_LOGS) Log.v(TAG, "instance");

        if (mTransitionHandler == null) {
            throw new RuntimeException("You need to call instance(Application) from your application class first");
        }
        return mTransitionHandler;
    }

    public static TransitionHandler instance(Application application) {
        if(Thread.currentThread().getId() != 1){
            throw new RuntimeException("Need to be called from main thread");
        }
        if(mTransitionHandler == null){
            mTransitionHandler = new TransitionHandler(application);

        }
        return mTransitionHandler;
    }

    public ExitTransition exitTransition(){
        ExitTransition exitTransition = new ExitTransition();
        mExitTransition = exitTransition;
        return exitTransition;
    }

    public EnterTransition enterTransition() {
        EnterTransition enterTransition = new EnterTransition(this);
        mEnterTransition = enterTransition;
        return enterTransition;
    }

    @Override
    public void runSharedElementsTransition(Map<String, View> sharedElementsTransitionViews) {
        if (SHOW_LOGS) Log.v(TAG, "runSharedElementsTransition, sharedElementsTransitionViews " + sharedElementsTransitionViews);
        if (SHOW_LOGS) Log.v(TAG, "runSharedElementsTransition, sharedElementsTransitionAnimator ");


//        mExitTransition.getSharedElementsTransitionAnimator()
//        sharedElementsTransitionAnimator.
    }
}
