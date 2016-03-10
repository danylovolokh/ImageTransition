package com.volokh.danylo.imagetransition.library.animators;

import android.view.View;

/**
 * Created by danylo.volokh on 3/10/16.
 */
public abstract class SharedElementTransitionAnimator extends TransitionAnimator {

    public SharedElementTransitionAnimator(long duration) {
        super(duration);
    }

    public abstract void runSharedElementTransitionAnimation(View fromView, View toView);
}
