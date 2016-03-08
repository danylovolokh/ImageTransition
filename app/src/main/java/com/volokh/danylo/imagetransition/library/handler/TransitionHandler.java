package com.volokh.danylo.imagetransition.library.handler;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.util.Log;

import com.volokh.danylo.imagetransition.library.Config;
import com.volokh.danylo.imagetransition.library.transition_data.ExitTransitionData;
import com.volokh.danylo.imagetransition.library.transition_data.TransitionData;
import com.volokh.danylo.imagetransition.library.transitions.ExitTransition;

/**
 * Created by danylo.volokh on 3/7/16.
 */
public class TransitionHandler {

    private static final String TAG = TransitionHandler.class.getSimpleName();
    private static final boolean SHOW_LOGS = Config.SHOW_LOGS;

    private static TransitionHandler mTransitionHandler;

    private TransitionData mEnterTransitionData;

    private BaseTransition mExitTransition;

    private ExitTransitionData mExitTransitionData;

    TransitionHandler(Application application){

        application.registerActivityLifecycleCallbacks(new SimpleActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

                if (SHOW_LOGS) Log.v(TAG, "onActivityCreated, activity " + activity);

                if (SHOW_LOGS)
                    Log.v(TAG, "onActivityCreated, mExitTransitionData " + mExitTransitionData);
                if (mExitTransition != null) {

                    mExitTransition.handleActivityCreated(activity, savedInstanceState);

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

    public void setEnterTransitionData(TransitionData transitionData) {
        mEnterTransitionData = transitionData;
    }

}
