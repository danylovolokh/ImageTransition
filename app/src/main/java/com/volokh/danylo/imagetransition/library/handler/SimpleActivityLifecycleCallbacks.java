package com.volokh.danylo.imagetransition.library.handler;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

/**
 * Created by danylo.volokh on 3/8/16.
 */
public class SimpleActivityLifecycleCallbacks implements Application.ActivityLifecycleCallbacks {

    /**
     * For overriding
     */
    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
    }

    /**
     * For overriding
     */
    @Override
    public void onActivityStarted(Activity activity) {
    }

    /**
     * For overriding
     */
    @Override
    public void onActivityResumed(Activity activity) {
    }

    /**
     * For overriding
     */
    @Override
    public void onActivityPaused(Activity activity) {
    }

    /**
     * For overriding
     */
    @Override
    public void onActivityStopped(Activity activity) {
    }

    /**
     * For overriding
     */
    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
    }

    /**
     * For overriding
     */
    @Override
    public void onActivityDestroyed(Activity activity) {
    }
}
