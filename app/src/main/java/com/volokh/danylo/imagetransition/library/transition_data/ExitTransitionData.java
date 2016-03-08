package com.volokh.danylo.imagetransition.library.transition_data;

import android.app.Activity;
import android.view.View;

import com.volokh.danylo.imagetransition.library.animators.TransitionAnimator;

/**
 * Created by danylo.volokh on 3/7/16.
 */
public class ExitTransitionData extends TransitionData {

    private final Activity mFromActivity;
    final Class mToActivity;

    private final View mExitContentView;

    private TransitionAnimator mExitTransitionAnimator;

    public ExitTransitionData(Activity fromActivity, Class toActivity) {
        mExitContentView = fromActivity.getWindow().getDecorView().findViewById(android.R.id.content);
        mFromActivity = fromActivity;
        mToActivity = toActivity;
    }

    public ExitTransitionData setExitAnimator(TransitionAnimator animator) {
        mExitTransitionAnimator = animator;
        return this;
    }

    public TransitionAnimator getTransitionAnimator() {
        return mExitTransitionAnimator;
    }

}
